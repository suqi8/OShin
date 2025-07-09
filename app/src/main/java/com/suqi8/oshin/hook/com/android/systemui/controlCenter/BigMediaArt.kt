package com.suqi8.oshin.hook.com.android.systemui.controlCenter

import android.graphics.drawable.Icon
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.suqi8.oshin.hook.utils.toRoundedDrawable

class BigMediaArt: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("systemui\\controlCenter").getBoolean("enlarge_media_cover", false)) {
            loadApp(name = "com.android.systemui") {
                val backgroundMap = java.util.WeakHashMap<View, FrameLayout>()

                "com.oplus.systemui.qs.media.OplusQsMediaPanelView".toClass().resolve().apply {
                    // Hook onFinishInflate: 创建背景视图并存入Map
                    firstMethod {
                        name = "onFinishInflate"
                        emptyParameters()
                    }.hook {
                        after {
                            val hookedView = instance as? ViewGroup ?: return@after

                            // 检查是否已处理过，防止重复执行
                            if (backgroundMap.containsKey(hookedView)) return@after

                            // 隐藏原始的封面视图
                            firstField { name = "mCoverImg" }.of(instance).get()?.let {
                                (it as View).visibility = View.GONE
                            }

                            // 创建自定义背景层
                            val mediaBackground = FrameLayout(hookedView.context).apply {
                                layoutParams = ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    ConstraintLayout.LayoutParams.MATCH_PARENT
                                ).apply {
                                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                }
                                id = View.generateViewId()
                            }
                            hookedView.addView(mediaBackground, 0)

                            // ✅ 将创建的背景视图与当前面板实例关联起来，存入Map
                            backgroundMap[hookedView] = mediaBackground
                        }
                    }

                    // Hook bindCoverImg: 从Map中取出背景视图并更新
                    firstMethod {
                        name = "bindCoverImg"
                        parameters("com.android.systemui.media.controls.shared.model.MediaData")
                    }.hook {
                        after {
                            // 隐藏原始的封面视图
                            firstField { name = "mCoverImg" }.of(instance).get()?.let {
                                (it as View).visibility = View.GONE
                            }
                            val hookedView = instance as? View ?: return@after
                            val context = hookedView.context
                            val mediaData = args[0]

                            // ✅ 从Map中根据当前面板实例取出对应的背景视图
                            val mediaBackground = backgroundMap[hookedView] ?: return@after

                            val isResumption = mediaData?.resolve()?.firstMethod { name = "getResumption" }?.invoke() as? Boolean ?: true
                            if (mediaData == null || isResumption) {
                                mediaBackground.visibility = View.GONE
                                return@after
                            }

                            val artworkIcon = mediaData.resolve().firstMethod { name = "getArtwork" }.invoke() as? Icon

                            if (artworkIcon == null) {
                                mediaBackground.visibility = View.GONE
                                return@after
                            }

                            artworkIcon.loadDrawable(context)?.let { originalDrawable ->
                                mediaBackground.background = originalDrawable.toRoundedDrawable(context, 18f)
                                mediaBackground.visibility = View.VISIBLE
                            } ?: run {
                                mediaBackground.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }
}
