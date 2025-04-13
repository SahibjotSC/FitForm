package com.example.fitform.exercise

import android.content.Context
import android.content.SharedPreferences
import com.example.fitform.MainActivity
import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats
import kotlin.math.min
import com.example.fitform.TextToSpeech
import kotlin.math.max

class Lunges(private val context: Context) {

    var count = 0
    var direction = false

    var maxProgress = 0.0;

    private val maxHistorySize = 10
    private val leftKneeManager = AngleManager(maxHistorySize, 23, 25, 27, 90.0, 150.0)
    private val rightKneeManager = AngleManager(maxHistorySize, 24, 26, 28, 90.0, 150.0)
    private val leftTorsoManager = AngleManager(maxHistorySize, 12, 24, 26, 90.0, 150.0)
    private val rightTorsoManager = AngleManager(maxHistorySize, 11, 23, 25, 90.0, 150.0)

    fun track(resultBundle: PoseLandmarkerHelper.ResultBundle): Stats {
        val lmList = resultBundle.results.firstOrNull()?.landmarks()?.firstOrNull() ?: emptyList()

        if (lmList.isNotEmpty()) {
            leftKneeManager.addAngle(lmList)
            rightKneeManager.addAngle(lmList)
            leftTorsoManager.addAngle(lmList)
            rightTorsoManager.addAngle(lmList)
        }

        //val torsoProgress = min(leftTorsoManager.progress, rightTorsoManager.progress)
        //val progress = (leftKneeManager.progress + rightKneeManager.progress + torsoProgress) / 3.0
        val progress = (leftKneeManager.progress + rightKneeManager.progress) / 2.0
        maxProgress = max(maxProgress, progress)

        if (progress == 100.0 && !direction) {
            count++
            direction = true
            TextToSpeech.speak(count.toString())
            MainActivity.updateDataObject(context, "Lunges")
        } else if (progress == 0.0 && direction) {
            direction = false
            maxProgress = 0.0
        }

        if (progress <= 25 && maxProgress < 100.0 && maxProgress >= 50) {
            TextToSpeech.speak("Not Low Enough")
            MainActivity.updateErrorDataObject(context, "Lunges")

            direction = false
            maxProgress = 0.0
        }

        val tip = "Tip: " + "\n $progress" + "\n ${leftKneeManager.progress}" + "\n ${rightKneeManager.progress}" + "\n ${leftTorsoManager.progress}"
        return Stats(count, progress, direction, tip)
    }
}