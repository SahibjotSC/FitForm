package com.example.fitform.exercise

import android.content.Context
import com.example.fitform.MainActivity
import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.TextToSpeech
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats
import kotlin.math.max

class JumpingJacks(private val context: Context) {

    var count = 0
    var direction = false

    var maxProgress = 0.0;

    private val maxHistorySize = 1

    // Track arm and leg positions
    private val leftArmManager = AngleManager(maxHistorySize, 13, 11, 23, 140.0, 50.0)
    private val rightArmManager = AngleManager(maxHistorySize, 14, 12, 24, 140.0, 40.0)
    private val leftLegManager = AngleManager(maxHistorySize, 23, 24, 26, 110.0, 95.0)
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

        //val progress = (armProgress.progress + legProgress.progress) / 2
        val progress = (leftArmManager.progress + leftLegManager.progress) / 2
        maxProgress = max(maxProgress, progress)

        if (progress == 100.0 && !direction) {
            count++
            direction = true
            TextToSpeech.speak(count.toString())
            MainActivity.updateDataObject(context, "JumpingJacks")
        } else if (progress == 0.0 && direction) {
            direction = false
            maxProgress = 0.0
        }

        if (progress <= 25 && maxProgress < 100.0 && maxProgress >= 50) {
            TextToSpeech.speak("Not far enough")
            MainActivity.updateErrorDataObject(context, "JumpingJacks")

            direction = false
            maxProgress = 0.0
        }

        return Stats(count, progress, direction, "")
    }
}
