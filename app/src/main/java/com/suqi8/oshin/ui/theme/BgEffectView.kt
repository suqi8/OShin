package com.suqi8.oshin.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.suqi8.oshin.R

@SuppressLint("ViewConstructor")
class BgEffectView(context: Context?, mode: Int) : LinearLayout(context) {
    private var mBgEffectView: View? = null
    private var mBgEffectPainter: BgEffectPainter? = null
    private val startTime = System.nanoTime().toFloat()
    private val mHandler = Handler(Looper.getMainLooper())
    private var colorMode = 1
    var runnableBgEffect: Runnable = object : Runnable {
        override fun run() {
            mBgEffectPainter!!.setAnimTime((((System.nanoTime().toFloat()) - startTime) / 1.0E9f) % 62.831852f)
            mBgEffectPainter!!.setResolution(floatArrayOf(mBgEffectView!!.width.toFloat(), mBgEffectView!!.height.toFloat()))
            mBgEffectPainter!!.updateMaterials()
            mBgEffectView!!.setRenderEffect(mBgEffectPainter!!.renderEffect)
            mHandler.postDelayed(runnableBgEffect, 16L)
        }
    }
    init {
        colorMode = mode
        BgEffect(context)
    }
    fun BgEffect(context: Context?) {
        mBgEffectView = LayoutInflater.from(context).inflate(R.layout.layout_effect_bg, this, true)
        mBgEffectView!!.post(Runnable {
            if (context != null) {
                val appContext = context.applicationContext
                mBgEffectPainter = BgEffectPainter(appContext)
                mBgEffectPainter!!.showRuntimeShader(appContext, mBgEffectView!!, colorMode)
                mHandler.post(runnableBgEffect)
            }
        })
    }
    fun updateMode(mode: Int) {
        if (mode != colorMode) {
            colorMode = mode
            mBgEffectPainter!!.updateMode(mode)
        }
    }
}
