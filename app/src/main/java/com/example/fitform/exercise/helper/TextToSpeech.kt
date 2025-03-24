package com.example.fitform

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

object TextToSpeech {
    private var textToSpeech: TextToSpeech? = null

    fun initialize(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    //Error
                }
            }
        }
    }

    fun speak(text: String?) {
        textToSpeech?.setSpeechRate(1.0f)
        textToSpeech?.setPitch(1.0f)
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}