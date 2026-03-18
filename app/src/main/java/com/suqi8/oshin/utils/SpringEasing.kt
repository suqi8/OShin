package com.suqi8.oshin.utils

import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 一个基于物理弹簧模型的 Easing 实现。
 * 它模拟了带阻尼的谐振子，可以创建非常自然、逼真的动画效果。
 *
 * 注意：请使用伴生对象 (companion object) 中的工厂方法来创建实例，例如 `SpringEasing.create()`
 * 或 `SpringEasing.createWithStiffness()`。
 *
 * @param dampingRatio 阻尼比 (ζ)。控制弹簧振动的衰减速度。数值越小，回弹效果越明显。
 * @param period 响应周期 (T)。弹簧完成一次完整振荡所需的时间（秒）。值越大，动画感觉越“柔软”和缓慢。
 * @param externalAcceleration 外部加速度 (g)。模拟重力等恒定外力。
 * @param initialVelocity 初始速度。动画开始时的速度。
 */
@Immutable
class SpringEasing private constructor(
    private val dampingRatio: Float,
    private val period: Float,
    private val externalAcceleration: Float,
    private val initialVelocity: Float
) : Easing {

    /**
     * 计算出的动画完成所需的大致时间（毫秒）。
     */
    var durationMillis: Long = 1000L
        private set

    private var timeScale: Float = 1.0f
    private lateinit var solution: SpringSolution

    init {
        updateParameters()
    }

    override fun transform(fraction: Float): Float {
        if (fraction >= 1.0f) {
            return 1.0f
        }
        val time = fraction * this.timeScale
        return solution.getPosition(time).toFloat()
    }

    private fun updateParameters() {
        val angularFrequency = (TWO_PI / period)
        val p = 2.0 * dampingRatio * angularFrequency
        val q = angularFrequency * angularFrequency
        val acceleration = externalAcceleration.toDouble()
        val equilibriumPosition = 1.0 - (acceleration / q)
        val initialDisplacement = 0.0 - equilibriumPosition
        val discriminant = (p * p) - (4.0 * q)
        val initialVel = initialVelocity.toDouble()

        this.solution = when {
            discriminant > 0.0 -> OverDampedSolution(discriminant, initialDisplacement, p, initialVel, equilibriumPosition)
            discriminant == 0.0 -> CriticallyDampedSolution(initialDisplacement, p, initialVel, equilibriumPosition)
            else -> UnderDampedSolution(discriminant, initialDisplacement, p, initialVel, equilibriumPosition)
        }

        val durationInSeconds = calculateDuration(discriminant, acceleration, q, equilibriumPosition)
        this.durationMillis = (durationInSeconds * 1000.0).toLong()
        this.timeScale = durationInSeconds.toFloat()
    }

    private fun calculateDuration(
        discriminant: Double,
        acceleration: Double,
        stiffness: Double,
        equilibriumPos: Double
    ): Double {
        val positionThreshold = if (discriminant >= 0.0) 0.001 else 0.0001
        val velocityThreshold = 0.0005

        if (acceleration == 0.0) {
            var time = 0.0
            val timeStep = 0.001
            while (time < 10.0) {
                time += timeStep
                val position = solution.getPosition(time.toFloat())
                val velocity = solution.getVelocity(time.toFloat())
                if (abs(position - 1.0) <= positionThreshold && abs(velocity) <= velocityThreshold) {
                    return time
                }
            }
            return 10.0
        }

        val initialEnergy = solution.calculateEnergy(0.0, stiffness, acceleration, equilibriumPos)
        val equilibriumEnergy = stiffness * equilibriumPos * equilibriumPos
        val energyMargin = (initialEnergy - equilibriumEnergy) * positionThreshold
        var lowerBoundTime = 0.0
        var upperBoundTime = 1.0
        var energyAtUpperBound = solution.calculateEnergy(upperBoundTime, stiffness, acceleration, equilibriumPos)

        while (energyAtUpperBound > equilibriumEnergy + energyMargin) {
            lowerBoundTime = upperBoundTime
            upperBoundTime *= 2
            energyAtUpperBound = solution.calculateEnergy(upperBoundTime, stiffness, acceleration, equilibriumPos)
        }

        while (upperBoundTime - lowerBoundTime >= positionThreshold) {
            val midTime = (lowerBoundTime + upperBoundTime) / 2.0
            if (solution.calculateEnergy(midTime, stiffness, acceleration, equilibriumPos) > equilibriumEnergy + energyMargin) {
                lowerBoundTime = midTime
            } else {
                upperBoundTime = midTime
            }
        }
        return upperBoundTime
    }

    companion object {
        private const val TWO_PI = 2 * PI

        /**
         * [默认] 创建一个基于 `period` (周期) 的 SpringEasing 实例。
         */
        @JvmStatic
        @JvmOverloads
        fun create(
            dampingRatio: Float = 0.65f,
            period: Float = 0.5f,
            externalAcceleration: Float = 0.0f,
            initialVelocity: Float = 0.0f
        ): SpringEasing {
            return SpringEasing(dampingRatio, period, externalAcceleration, initialVelocity)
        }

        /**
         * 创建一个基于 `stiffness` (刚度) 的 SpringEasing 实例，API 与 Jetpack Compose 更为接近。
         * 这个函数虽然可能在当前项目中“未使用”，但它作为库代码的一部分，为使用者提供了额外的灵活性。
         */
        @JvmStatic
        @JvmOverloads
        fun createWithStiffness(
            dampingRatio: Float,
            stiffness: Float,
            mass: Float = 1.0f,
            externalAcceleration: Float = 0.0f,
            initialVelocity: Float = 0.0f
        ): SpringEasing {
            // 根据刚度、质量计算周期: period = 2π * sqrt(mass / stiffness)
            val period = (TWO_PI * sqrt(mass / stiffness)).toFloat()
            return SpringEasing(dampingRatio, period, externalAcceleration, initialVelocity)
        }


        // --- 预设动画效果 ---

        /**
         * 一种柔和、缓慢的弹簧效果，具有轻微的回弹。
         */
        @JvmStatic
        fun gentle() = create(dampingRatio = 0.7f, period = 0.6f)

        /**
         * 一种具有明显回弹和振荡的弹簧效果，感觉活泼。
         */
        @JvmStatic
        fun bouncy() = create(dampingRatio = 0.45f, period = 0.45f)

        /**
         * 一种快速、生硬的弹簧效果，无回弹（临界阻尼）。
         */
        @JvmStatic
        fun stiff() = create(dampingRatio = 1.0f, period = 0.3f)
    }

    // --- 内部解算器类 ---
    internal abstract class SpringSolution {
        abstract fun getPosition(time: Float): Double
        abstract fun getVelocity(time: Float): Double
        fun calculateEnergy(time: Double, stiffness: Double, acceleration: Double, equilibrium: Double): Double {
            val pos = getPosition(time.toFloat())
            val vel = getVelocity(time.toFloat())
            return (stiffness * pos * pos) + (vel * vel) - (acceleration * 2.0 * (pos - equilibrium))
        }
    }

    internal class CriticallyDampedSolution(initialDisp: Double, p: Double, initialVel: Double, private val equilibriumPos: Double) : SpringSolution() {
        private val r: Double
        private val c1: Double
        private val c2: Double

        init {
            r = -p / 2.0
            c1 = initialDisp
            c2 = initialVel - (initialDisp * r)
        }

        override fun getPosition(time: Float): Double {
            val t = time.toDouble()
            return ((c1 + c2 * t) * exp(r * t)) + equilibriumPos
        }

        override fun getVelocity(time: Float): Double {
            val t = time.toDouble()
            return ((c1 * r) + c2 * (r * t + 1.0)) * exp(r * t)
        }
    }

    internal inner class OverDampedSolution(discriminant: Double, initialDisp: Double, p: Double, initialVel: Double, private val equilibriumPos: Double) : SpringSolution() {
        private val r1: Double
        private val r2: Double
        private val c1: Double
        private val c2: Double

        init {
            val sqrtDisc = sqrt(discriminant)
            r1 = (-p + sqrtDisc) / 2.0
            r2 = (-p - sqrtDisc) / 2.0
            c1 = (initialVel - initialDisp * r2) / sqrtDisc
            c2 = -(initialVel - initialDisp * r1) / sqrtDisc
        }

        override fun getPosition(time: Float): Double {
            val t = time.toDouble()
            return (c1 * exp(r1 * t)) + (c2 * exp(r2 * t)) + equilibriumPos
        }

        override fun getVelocity(time: Float): Double {
            val t = time.toDouble()
            return (c1 * r1 * exp(r1 * t)) + (c2 * r2 * exp(r2 * t))
        }
    }

    internal inner class UnderDampedSolution(discriminant: Double, initialDisp: Double, p: Double, initialVel: Double, private val equilibriumPos: Double) : SpringSolution() {
        private val alpha: Double
        private val beta: Double
        private val c1: Double
        private val c2: Double

        init {
            alpha = -p / 2.0
            beta = sqrt(-discriminant) / 2.0
            c1 = initialDisp
            c2 = (initialVel - initialDisp * alpha) / beta
        }

        override fun getPosition(time: Float): Double {
            val t = time.toDouble()
            return (exp(alpha * t) * (c1 * cos(beta * t) + c2 * sin(beta * t))) + equilibriumPos
        }

        override fun getVelocity(time: Float): Double {
            val t = time.toDouble()
            val expTerm = exp(alpha * t)
            val cosTerm = (c1 * alpha + c2 * beta) * cos(beta * t)
            val sinTerm = (c2 * alpha - c1 * beta) * sin(beta * t)
            return expTerm * (cosTerm + sinTerm)
        }
    }
}
