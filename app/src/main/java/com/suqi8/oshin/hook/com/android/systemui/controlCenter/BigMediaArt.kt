package com.suqi8.oshin.hook.com.android.systemui.controlCenter

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Icon
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

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
                    // Hook onFinishInflate: 创建背景视图、查找子视图并设置视图裁切
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

                                // --- ✨ 新增：通过视图轮廓实现圆角裁切 ---
                                val cornerRadiusDp = 18f
                                val cornerRadiusPx = cornerRadiusDp * context.resources.displayMetrics.density
                                outlineProvider = object : ViewOutlineProvider() {
                                    override fun getOutline(view: View, outline: Outline) {
                                        outline.setRoundRect(0, 0, view.width, view.height, cornerRadiusPx)
                                    }
                                }
                                clipToOutline = true
                                // --- 视图裁切设置结束 ---
                            }
                            hookedView.addView(mediaBackground, 0)

                            // 将所有找到的视图存入Map
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

                    // Hook bindCoverImg: 更新背景和分离的颜色
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
                                val fullBitmap = originalDrawable.toBitmap()
                                views.background.background = fullBitmap.toDrawable(context.resources)
                                views.background.visibility = View.VISIBLE

                                if (prefs("systemui\\controlCenter").getBoolean("qs_media_auto_color_label", false)) {
                                    // --- ✨ 颜色分离逻辑 ---
                                    val width = fullBitmap.width
                                    val height = fullBitmap.height
                                    if (width <= 0 || height <= 1) return@let // 确保Bitmap有效

                                    // 裁切出上半部分和下半部分
                                    val upperBitmap = Bitmap.createBitmap(fullBitmap, 0, 0, width, height / 2)
                                    val lowerBitmap = Bitmap.createBitmap(fullBitmap, 0, height / 2, width, height / 2)

                                    // 分析上半部分，设置文本颜色
                                    Palette.from(upperBitmap).generate { palette ->
                                        val dominantColor = palette?.dominantSwatch?.rgb ?: Color.GRAY
                                        val textColor = dominantColor.invert()
                                        hookedView.post {
                                            views.title?.setTextColor(textColor)
                                            views.text?.setTextColor(textColor)
                                        }
                                    }

                                    // 分析下半部分，设置图标颜色
                                    Palette.from(lowerBitmap).generate { palette ->
                                        val dominantColor = palette?.dominantSwatch?.rgb ?: Color.GRAY
                                        val iconColor = dominantColor.invert()
                                        hookedView.post {
                                            val buttonTint = ColorStateList.valueOf(iconColor)
                                            views.preBtn?.imageTintList = buttonTint
                                            views.playOrPauseBtn?.imageTintList = buttonTint
                                            views.nextBtn?.imageTintList = buttonTint
                                        }
                                    }
                                    // --- 颜色分离逻辑结束 ---
                                }
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
