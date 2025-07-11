package com.suqi8.oshin.hook.com.android.systemui.controlCenter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Icon
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

// 建议将这些扩展函数放在一个单独的工具类文件中
fun Bitmap.toRoundedDrawable(context: Context, cornerRadiusDp: Float): RoundedBitmapDrawable {
    val cornerRadiusPx = cornerRadiusDp * context.resources.displayMetrics.density
    return RoundedBitmapDrawableFactory.create(context.resources, this).apply {
        cornerRadius = cornerRadiusPx
        setAntiAlias(true)
    }
}

// 颜色反转扩展函数
fun Int.invert(): Int {
    val r = 255 - Color.red(this)
    val g = 255 - Color.green(this)
    val b = 255 - Color.blue(this)
    return Color.rgb(r, g, b)
}

// 使用一个数据类来安全地管理每个面板实例对应的视图引用
private data class PanelViews(
    val background: FrameLayout,
    val title: TextView?,
    val text: TextView?,
    val preBtn: ImageView?,
    val playOrPauseBtn: ImageView?,
    val nextBtn: ImageView?
)

class BigMediaArt: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("systemui\\controlCenter").getBoolean("enlarge_media_cover", false)) {
            loadApp(name = "com.android.systemui") {
                // 使用 WeakHashMap 来安全地将视图引用与每个面板实例绑定
                val viewMap = java.util.WeakHashMap<View, PanelViews>()

                "com.oplus.systemui.qs.media.OplusQsMediaPanelView".toClass().resolve().apply {
                    // Hook onFinishInflate: 创建背景视图并查找所有子视图
                    firstMethod {
                        name = "onFinishInflate"
                        emptyParameters()
                    }.hook {
                        after {
                            val hookedView = instance as? ViewGroup ?: return@after
                            if (viewMap.containsKey(hookedView)) return@after

                            // 隐藏原始封面
                            firstField { name = "mCoverImg" }.of(instance).get()?.let {
                                (it as View).visibility = View.GONE
                            }

                            // 使用您提供的变量名查找视图
                            val mTitle = firstField { name = "mTitle" }.of(instance).get() as? TextView
                            val mText = firstField { name = "mText" }.of(instance).get() as? TextView
                            val mPreBtn = firstField { name = "mPreBtn" }.of(instance).get() as? ImageView
                            val mPlayOrPauseBtn = firstField { name = "mPlayOrPauseBtn" }.of(instance).get() as? ImageView
                            val mNextBtn = firstField { name = "mNextBtn" }.of(instance).get() as? ImageView

                            // 创建自定义背景层
                            val mediaBackground = FrameLayout(hookedView.context).apply {
                                id = View.generateViewId()
                                layoutParams = ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    ConstraintLayout.LayoutParams.MATCH_PARENT
                                ).apply {
                                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                }
                            }
                            hookedView.addView(mediaBackground, 0)

                            // 将所有找到的视图存入Map，与当前面板实例关联
                            viewMap[hookedView] = PanelViews(
                                background = mediaBackground,
                                title = mTitle,
                                text = mText,
                                preBtn = mPreBtn,
                                playOrPauseBtn = mPlayOrPauseBtn,
                                nextBtn = mNextBtn
                            )
                        }
                    }

                    // Hook bindCoverImg: 更新背景和颜色
                    firstMethod {
                        name = "bindCoverImg"
                        parameters("com.android.systemui.media.controls.shared.model.MediaData")
                    }.hook {
                        after {
                            val hookedView = instance as? View ?: return@after
                            val context = hookedView.context
                            val mediaData = args[0]
                            val views = viewMap[hookedView] ?: return@after

                            firstField { name = "mCoverImg" }.of(instance).get()?.let {
                                (it as View).visibility = View.GONE
                            }

                            val isResumption = mediaData?.resolve()?.firstMethod { name = "getResumption" }?.invoke() as? Boolean ?: true
                            if (mediaData == null || isResumption) {
                                views.background.visibility = View.GONE
                                return@after
                            }

                            val artworkIcon = mediaData.resolve().firstMethod { name = "getArtwork" }.invoke() as? Icon
                            if (artworkIcon == null) {
                                views.background.visibility = View.GONE
                                return@after
                            }

                            artworkIcon.loadDrawable(context)?.let { originalDrawable ->
                                // 使用 KTX 扩展函数将 Drawable 转换为 Bitmap
                                val bitmap = originalDrawable.toBitmap()

                                // 使用处理过的Bitmap创建背景
                                views.background.background = bitmap.toRoundedDrawable(context, 18f)
                                views.background.visibility = View.VISIBLE

                                // --- ✨ 颜色反转逻辑 ---
                                Palette.from(bitmap).generate { palette ->
                                    // 获取主导色，如果失败则默认为灰色
                                    val dominantColor = palette?.dominantSwatch?.rgb ?: Color.GRAY
                                    // 计算反色
                                    val invertedColor = dominantColor.invert()

                                    hookedView.post {
                                        views.title?.setTextColor(invertedColor)
                                        views.text?.setTextColor(invertedColor)
                                        val buttonTint = ColorStateList.valueOf(invertedColor)
                                        views.preBtn?.imageTintList = buttonTint
                                        views.playOrPauseBtn?.imageTintList = buttonTint
                                        views.nextBtn?.imageTintList = buttonTint
                                    }
                                }
                                // --- 颜色反转逻辑结束 ---

                            } ?: run {
                                views.background.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }
}
