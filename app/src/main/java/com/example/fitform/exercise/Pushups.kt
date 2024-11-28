package com.example.fitform.exercise

import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.helper.Helper
import kotlin.math.abs

class Pushups {
    
    private var count = 0
    private var progress = 0.0
    private var direction = false
    private val angleHistory = mutableListOf<Double>()
    private val maxHistorySize = 10

    fun track(resultBundle: PoseLandmarkerHelper.ResultBundle): Stats {
        val lmList = resultBundle.results.firstOrNull()?.landmarks()?.firstOrNull() ?: emptyList()
        var tip = "Nothing detected"

        if (lmList.isNotEmpty()) {
            val rightAngle = Helper.findAngle(lmList, 12, 14, 16)
            val leftAngle = Helper.findAngle(lmList, 11, 13, 15)
            val angle = (rightAngle + leftAngle) / 2

            angleHistory.add(angle)
            if (angleHistory.size > maxHistorySize) {
                angleHistory.removeAt(0)
            }

            val averageAngle = Helper.calculateAverageAngle(angleHistory)
            //val averageAngle = angle

            val low = 40
            val high = 150
            progress = Helper.interpolate(averageAngle, low.toDouble(), high.toDouble())

            if (progress == 100.0 && !direction) {
                count++
                direction = true
            } else if (progress == 0.0 && direction) {
                direction = false
            }

            tip = "Average angle: %.2f".format(averageAngle) +
                    "\n $progress" +
                    "\n $rightAngle" +
                    "\n $leftAngle" +
                    "\n $angle"
            ;
        }

        return Stats(count, progress, direction, tip)
    }
}
