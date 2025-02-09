package com.example.fitform.exercise

import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats
import kotlin.math.min

class Lunges {

    var count = 0
    var direction = false

    private val maxHistorySize = 10
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

        if (progress == 100.0 && !direction) {
            count++
            direction = true
        } else if (progress == 0.0 && direction) {
            direction = false
        }

        val tip = "Tip: " + "\n $progress" + "\n ${leftKneeManager.progress}" + "\n ${rightKneeManager.progress}" + "\n ${leftTorsoManager.progress}"
        return Stats(count, progress, direction, tip)
    }
}