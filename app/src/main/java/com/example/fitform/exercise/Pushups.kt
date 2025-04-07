package com.example.fitform.exercise

import android.content.Context
import com.example.fitform.MainActivity
import com.example.fitform.PoseLandmarkerHelper
import com.example.fitform.exercise.helper.AngleManager
import com.example.fitform.exercise.helper.Stats

class Pushups(private val context: Context) {
    
    var count = 0
    var direction = false

    private val maxHistorySize = 10
    private val leftKneeManager = AngleManager(maxHistorySize, 12, 14, 16, 40.0, 150.0)
    private val rightKneeManager = AngleManager(maxHistorySize, 11, 13, 15, 40.0, 150.0)

    fun track(resultBundle: PoseLandmarkerHelper.ResultBundle): Stats {
        val lmList = resultBundle.results.firstOrNull()?.landmarks()?.firstOrNull() ?: emptyList()

        var isProper = false;
        if (lmList.isNotEmpty()) {
            leftKneeManager.addAngle(lmList)
            rightKneeManager.addAngle(lmList)

            isProper = lmList[15].y() > lmList[25].y() && lmList[16].y() > lmList[26].y();
        }

        var progress = 0.0;
        if (isProper) {
            progress = (leftKneeManager.progress + rightKneeManager.progress) / 2

            if (progress == 100.0 && !direction) {
                count++
                direction = true
                MainActivity.updateDataObject(context, "Pushups")
            } else if (progress == 0.0 && direction) {
                direction = false
            }
        }

        val tip = "Tip: " + "\n $progress" + "\n ${leftKneeManager.progress}" + "\n ${rightKneeManager.progress}"
        return Stats(count, progress, direction, tip)
    }
}
