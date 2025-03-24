package com.example.fitform.exercise

import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats

class LateralRaise {

    var count = 0
    var direction = false

    private val maxHistorySize = 10
    private val leftArmManager = AngleManager(maxHistorySize, 11, 13, 15, 30.0, 170.0)
    private val rightArmManager = AngleManager(maxHistorySize, 12, 14, 16, 30.0, 170.0)

    fun track(resultBundle: PoseLandmarkerHelper.ResultBundle): Stats {
        val lmList = resultBundle.results.firstOrNull()?.landmarks()?.firstOrNull() ?: emptyList()

        if (lmList.isNotEmpty()) {
            leftArmManager.addAngle(lmList)
            rightArmManager.addAngle(lmList)
        }

        val progress = (leftArmManager.progress + rightArmManager.progress) / 2.0

        if (progress == 100.0 && !direction) {
            count++
            direction = true
        } else if (progress == 0.0 && direction) {
            direction = false
        }

        val tip = "Tip: Ensure full arm extension.\n" +
                  "Left Arm Progress: ${leftArmManager.progress}%\n" +
                  "Right Arm Progress: ${rightArmManager.progress}%"

        return Stats(count, progress, direction, tip)
    }
}