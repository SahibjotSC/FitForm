package com.example.fitform.exercise

import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.helper.Helper

class PushUp {
    
    private var count = 0
    private var direction = 0

    fun trackPush(resultBundle: PoseLandmarkerHelper.ResultBundle): Stats {
        val lmList = resultBundle.results.firstOrNull()?.landmarks()?.firstOrNull() ?: emptyList()
        var tip = "No landmarks detected."

        if (lmList.isNotEmpty()) {
            val angle = Helper.findAngle(lmList, 11, 13, 15)

            val low = 70
            val high = 150
            val per = Helper.interpolate(angle, low.toDouble(), high.toDouble(), 0.0, 100.0)

            if (per == 100.0 && direction == 0) {
                count++
                direction = 1
            } else if (per == 0.0 && direction == 1) {
                direction = 0
            }

            tip = "Push-up count: $count\n% count: $per\nAngle count: $angle"
        }

        return Stats(count, 0f, tip);
    }
}
