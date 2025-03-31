package com.example.fitform.exercise

import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats

class JumpingJacks {

    var count = 0
    var direction = false

    private val maxHistorySize = 10

    // Track arm and leg positions
    private val leftArmManager = AngleManager(maxHistorySize, 11, 13, 15, 30.0, 170.0)
    private val rightArmManager = AngleManager(maxHistorySize, 12, 14, 16, 30.0, 170.0)
    private val leftLegManager = AngleManager(maxHistorySize, 23, 25, 27, 20.0, 80.0)
    private val rightLegManager = AngleManager(maxHistorySize, 24, 26, 28, 20.0, 80.0)

    fun track(resultBundle: PoseLandmarkerHelper.ResultBundle): Stats {
        val lmList = resultBundle.results.firstOrNull()?.landmarks()?.firstOrNull() ?: emptyList()

        val neededIndexes = listOf(11, 13, 15, 12, 14, 16, 23, 25, 27, 24, 26, 28)
        val notVisible = neededIndexes.any { index ->
            index >= lmList.size || lmList[index].visibility().orElse(0.0f) < 0.5
        }

        if (notVisible) {
            return Stats(count, 0.0, direction, "Tip: Make sure your whole body is visible to the camera.")
        }

        leftArmManager.addAngle(lmList)
        rightArmManager.addAngle(lmList)
        leftLegManager.addAngle(lmList)
        rightLegManager.addAngle(lmList)

        val armProgress = (leftArmManager.progress + rightArmManager.progress) / 2
        val legProgress = (leftLegManager.progress + rightLegManager.progress) / 2

        if (armProgress >= 90.0 && legProgress >= 80.0 && !direction) {
            count++
            direction = true
        } else if (armProgress <= 30.0 && legProgress <= 20.0) {
            direction = false
        }

        val tip = "Tip: Ensure fuller arm extension & proper leg spread.\n" +
                "Arms Progress: $armProgress%\n" +
                "Legs Progress: $legProgress%"

        return Stats(count, (armProgress + legProgress) / 2, direction, tip)
    }
}
