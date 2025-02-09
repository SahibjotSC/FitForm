package com.example.fitform.exercise.helper

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class AngleManager(
    private val maxHistorySize: Int,
    private val angle1: Int,
    private val angle2: Int,
    private val angle3: Int,
    private val angleLow: Double,
    private val angleHigh: Double,
) {

    var progress: Double = 0.0
        private set

    private val angleHistory: MutableList<Double> = mutableListOf()

    fun addAngle(poseResult: List<NormalizedLandmark>): Double {
        val singleAngle = findAngle(poseResult, angle1, angle2, angle3)

        angleHistory.add(singleAngle)
        if (angleHistory.size > maxHistorySize) {
            angleHistory.removeAt(0)
        }

        progress = interpolate(calculateAverageAngle(angleHistory), angleLow, angleHigh)
        return progress;
    }

    private fun findAngle(
        poseResult: List<NormalizedLandmark>,
        p1: Int,
        p2: Int,
        p3: Int
    ): Double {
        val point1 = poseResult[p1]
        val point2 = poseResult[p2]
        val point3 = poseResult[p3]

        val x1 = point1.x()
        val y1 = point1.y()
        val x2 = point2.x()
        val y2 = point2.y()
        val x3 = point3.x()
        val y3 = point3.y()

        var angle = Math.toDegrees(
            Math.atan2((y3 - y2).toDouble(), (x3 - x2).toDouble()) -
                    Math.atan2((y1 - y2).toDouble(), (x1 - x2).toDouble())
        )

        angle = if (angle > 180) 360 - angle else angle

        return angle
    }

    private fun interpolate(
        value: Double,
        low: Double,
        high: Double
    ): Double {
        return clamp(((1 - ((value - low) / (high - low))) * 100), 0.0, 100.0)
    }

    private fun clamp(value: Double, min: Double, max: Double): Double {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }

    private fun calculateAverageAngle(angles: List<Double>): Double {
        if (angles.isEmpty()) return 0.0

        val sumX = angles.sumOf { cos(Math.toRadians(it)) }
        val sumY = angles.sumOf { sin(Math.toRadians(it)) }

        return Math.toDegrees(atan2(sumY, sumX))
    }
}