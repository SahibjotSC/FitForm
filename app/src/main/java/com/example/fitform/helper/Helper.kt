package com.example.fitform.helper

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class Helper {
    companion object {
        fun interpolate(
            value: Double,
            low: Double,
            high: Double,
            targetLow: Double,
            targetHigh: Double
        ): Double {
            return targetLow + (value - low) * (targetHigh - targetLow) / (high - low)
        }

        fun findAngle(
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

            // Calculate the angle
            var angle = Math.toDegrees(
                Math.atan2((y3 - y2).toDouble(), (x3 - x2).toDouble()) -
                        Math.atan2((y1 - y2).toDouble(), (x1 - x2).toDouble())
            )

            if (angle < 0) {
                angle = Math.abs(angle)
            } else {
                angle = 360 - angle
            }

            return angle
        }
    }
}