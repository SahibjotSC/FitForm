package com.example.fitform.exercise

import android.content.Context
import android.content.SharedPreferences
import com.example.fitform.MainActivity
import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats
import kotlin.math.min
import com.example.fitform.TextToSpeech
import java.util.Date

class Lunges(private val context: Context) {

    var count = 0
    var direction = false

    var progressMax = 0.0;
    var notLowEnoughAlert = false

    private val maxHistorySize = 10
    private val recentProgress = mutableListOf<Double>()
    private val leftKneeManager = AngleManager(maxHistorySize, 23, 25, 27, 40.0, 150.0)
    private val rightKneeManager = AngleManager(maxHistorySize, 24, 26, 28, 40.0, 150.0)
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

        val torsoProgress = min(leftTorsoManager.progress, rightTorsoManager.progress)
        val progress = (leftKneeManager.progress + rightKneeManager.progress + torsoProgress) / 3.0

        recentProgress.add(progress)
        if (recentProgress.size > maxHistorySize) {
            recentProgress.removeAt(0)
        }

        val averageProgress = recentProgress.average()
        direction = progress > averageProgress

        if (!direction && progress == 0.0) notLowEnoughAlert = false

        if (!direction && progressMax < 100.0 && !notLowEnoughAlert) {
            TextToSpeech.speak("Not Low Enough")
            notLowEnoughAlert = true
        }

        if (progress == 100.0 && direction) {
            count++
            MainActivity.updateDataObject(context, "Lunges")
        }

        if (direction) progressMax = maxOf(progressMax, progress)

        val tip = "Tip: " + "\n $progress" + "\n ${leftKneeManager.progress}" + "\n ${rightKneeManager.progress}" + "\n ${leftTorsoManager.progress}"
        return Stats(count, progress, direction, tip)
    }
}