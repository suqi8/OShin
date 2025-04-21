package com.suqi8.oshin.ui.theme

import android.content.Context
import android.content.res.Resources
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.suqi8.oshin.R
import java.util.Scanner

class BgEffectPainter(context: Context) {
    private lateinit var bound: FloatArray
    var mBgRuntimeShader: RuntimeShader
    var mResources: Resources = context.resources
    private lateinit var uResolution: FloatArray
    private var uAnimTime = (System.nanoTime().toFloat()) / 1.0E9f
    private var uBgBound = floatArrayOf(0.0f, 0.4489f, 1.0f, 0.5511f)
    private val uTranslateY = 0.0f
    private var uPoints = floatArrayOf(0.67f, 0.42f, 1.0f, 0.69f, 0.75f, 1.0f, 0.14f, 0.71f, 0.95f, 0.14f, 0.27f, 0.8f)
    private var uColors = floatArrayOf(0.57f, 0.76f, 0.98f, 1.0f, 0.98f, 0.85f, 0.68f, 1.0f, 0.98f, 0.75f, 0.93f, 1.0f, 0.73f, 0.7f, 0.98f, 1.0f)
    private val uAlphaMulti = 1.0f
    private val uNoiseScale = 1.5f
    private val uPointOffset = 0.1f
    private val uPointRadiusMulti = 1.0f
    private var uSaturateOffset = 0.2f
    private var uLightOffset = 0.1f
    private val uAlphaOffset = 0.5f
    private val uShadowColorMulti = 0.3f
    private val uShadowColorOffset = 0.3f
    private val uShadowNoiseScale = 5.0f
    private val uShadowOffset = 0.01f

    init {
        val loadShader = loadShader(mResources, R.raw.bg_frag)
        mBgRuntimeShader = RuntimeShader(loadShader!!)
        mBgRuntimeShader.setFloatUniform("uTranslateY", uTranslateY)
        mBgRuntimeShader.setFloatUniform("uPoints", uPoints)
        mBgRuntimeShader.setFloatUniform("uColors", uColors)
        mBgRuntimeShader.setFloatUniform("uNoiseScale", uNoiseScale)
        mBgRuntimeShader.setFloatUniform("uPointOffset", uPointOffset)
        mBgRuntimeShader.setFloatUniform("uPointRadiusMulti", uPointRadiusMulti)
        mBgRuntimeShader.setFloatUniform("uSaturateOffset", uSaturateOffset)
        mBgRuntimeShader.setFloatUniform("uShadowColorMulti", uShadowColorMulti)
        mBgRuntimeShader.setFloatUniform("uShadowColorOffset", uShadowColorOffset)
        mBgRuntimeShader.setFloatUniform("uShadowOffset", uShadowOffset)
        mBgRuntimeShader.setFloatUniform("uBound", uBgBound)
        mBgRuntimeShader.setFloatUniform("uAlphaMulti", uAlphaMulti)
        mBgRuntimeShader.setFloatUniform("uLightOffset", uLightOffset)
        mBgRuntimeShader.setFloatUniform("uAlphaOffset", uAlphaOffset)
        mBgRuntimeShader.setFloatUniform("uShadowNoiseScale", uShadowNoiseScale)
    }

    val renderEffect: RenderEffect
        get() = RenderEffect.createRuntimeShaderEffect(mBgRuntimeShader, "uTex")

    fun updateMaterials() {
        mBgRuntimeShader.setFloatUniform("uAnimTime", uAnimTime)
        mBgRuntimeShader.setFloatUniform("uResolution", uResolution)
    }

    fun setAnimTime(f: Float) {
        uAnimTime = f
    }

    fun setColors(fArr: FloatArray) {
        uColors = fArr
        mBgRuntimeShader.setFloatUniform("uColors", fArr)
    }

    fun setPoints(fArr: FloatArray) {
        uPoints = fArr
        mBgRuntimeShader.setFloatUniform("uPoints", fArr)
    }

    fun setBound(fArr: FloatArray) {
        this.uBgBound = fArr
        this.mBgRuntimeShader.setFloatUniform("uBound", fArr)
    }

    fun setLightOffset(f: Float) {
        this.uLightOffset = f
        this.mBgRuntimeShader.setFloatUniform("uLightOffset", f)
    }

    fun setSaturateOffset(f: Float) {
        this.uSaturateOffset = f
        this.mBgRuntimeShader.setFloatUniform("uSaturateOffset", f)
    }

    fun setPhoneLight(fArr: FloatArray) {
        setLightOffset(0.1f)
        setSaturateOffset(0.2f)
        setPoints(floatArrayOf(0.67f,0.42f,1.0f,0.69f,0.75f,1.0f,0.14f,0.71f,0.95f,0.14f,0.27f, 0.8f))
        setColors(floatArrayOf(0.57f,0.76f,0.98f,1.0f,0.98f,0.85f,0.68f,1.0f,0.98f,0.75f,0.93f,1.0f,0.73f,0.7f,0.98f,1.0f))
        setBound(fArr)
    }

    fun setPhoneDark(fArr: FloatArray) {
        setLightOffset(-0.1f)
        setSaturateOffset(0.2f)
        setPoints(floatArrayOf(0.63f, 0.5f, 0.88f, 0.69f, 0.75f, 0.8f, 0.17f, 0.66f, 0.81f, 0.14f, 0.24f, 0.72f))
        setColors(floatArrayOf(0.0f, 0.31f, 0.58f, 1.0f, 0.53f, 0.29f, 0.15f, 1.0f, 0.46f, 0.06f, 0.27f, 1.0f, 0.16f, 0.12f, 0.45f, 1.0f))
        setBound(fArr)
    }

    fun setResolution(fArr: FloatArray) {
        this.uResolution = fArr
    }

    private fun loadShader(resources: Resources, i: Int): String? {
        try {
            val openRawResource = resources.openRawResource(i)
            try {
                val scanner = Scanner(openRawResource)
                try {
                    val sb = StringBuilder()
                    while (scanner.hasNextLine()) {
                        sb.append(scanner.nextLine())
                        sb.append("\n")
                    }
                    val sb2 = sb.toString()
                    scanner.close()
                    openRawResource.close()
                    return sb2
                } finally {
                }
            } finally {
            }
        } catch (_: Exception) {
            return null
        }
    }

    fun showRuntimeShader(context: Context, view: View, mode: Int) {
        calcAnimationBound(context, view)
        if (mode == 2) {
            setPhoneDark(this.bound)
        } else {
            setPhoneLight(this.bound)
        }
    }

    fun updateMode(mode: Int) {
        if (mode == 2) {
            setPhoneDark(this.bound)
        } else {
            setPhoneLight(this.bound)
        }
    }

    private fun calcAnimationBound(context: Context, view: View) {
        val height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            416f,
            context.resources.displayMetrics
        )
        val height2 = height / (view.parent as ViewGroup).height
        val width = (view.parent as ViewGroup).width.toFloat()
        if (width <= height) {
            this.bound = floatArrayOf(0.0f, 1.0f - height2, 1.0f, height2)
        } else {
            this.bound = floatArrayOf(((width - height) / 2.0f) / width, 1.0f - height2, height / width, height2)
        }
    }
}
