package com.kyant.liquidglass

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class LiquidGlassShadersCache() {

    private var _materialShader: RuntimeShader? = null
    private var _refractionShader: RuntimeShader? = null
    private var isRefractionShaderWithBleed = false
    private var _bleedShader: RuntimeShader? = null

    fun getMaterialShader(): RuntimeShader {
        if (_materialShader == null) {
            _materialShader = RuntimeShader(LiquidGlassShaders.materialShaderString)
        }
        return _materialShader!!
    }

    fun getRefractionShader(withBleed: Boolean): RuntimeShader {
        if (_refractionShader == null || isRefractionShaderWithBleed != withBleed) {
            _refractionShader =
                RuntimeShader(
                    if (withBleed) {
                        LiquidGlassShaders.refractionShaderWithBleedString
                    } else {
                        LiquidGlassShaders.refractionShaderString
                    }
                )
        }
        return _refractionShader!!
    }

    fun getBleedShader(): RuntimeShader {
        if (_bleedShader == null) {
            _bleedShader = RuntimeShader(LiquidGlassShaders.bleedShaderString)
        }
        return _bleedShader!!
    }
}
