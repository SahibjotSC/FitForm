package com.example.fitform.exercise

import android.content.Context
import com.example.fitform.MainActivity
import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats

class JumpingJacks(private val context: Context) {

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

        if (lmList.isNotEmpty()) {
            leftArmManager.addAngle(lmList)
            rightArmManager.addAngle(lmList)
            leftLegManager.addAngle(lmList)
            rightLegManager.addAngle(lmList)
        }

        // Calculate progress based on arms and legs
        val armProgress = (leftArmManager.progress + rightArmManager.progress) / 2
        val legProgress = (leftLegManager.progress + rightLegManager.progress) / 2

        // Jump cycle detected: arms up, legs wide (max extension)
        if (armProgress >= 90.0 && legProgress >= 80.0 && !direction) {
            count++
            direction = true
            MainActivity.updateDataObject(context, "JumpingJacks")
        } 
        // Reset when arms and legs return to starting position
        else if (armProgress <= 30.0 && legProgress <= 20.0) {
            direction = false
        }

        val tip = "Tip: Ensure full arm extension & proper leg spread.\n" +
                  "Arms Progress: $armProgress%\n" +
                  "Legs Progress: $legProgress%"

        return Stats(count, (armProgress + legProgress) / 2, direction, tip)
    }
}
