package com.quidone.reactnativewheelpickerfeedback

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import android.content.Context

@ReactModule(name = ReactNativeWheelPickerFeedbackModule.NAME)
class ReactNativeWheelPickerFeedbackModule(
    reactContext: ReactApplicationContext
) : NativeReactNativeWheelPickerFeedbackSpec(reactContext) {

    private val soundPool: SoundPool
    private val tickSoundId: Int
    private val vibrator: Vibrator

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        tickSoundId = soundPool.load(
            reactContext,
            R.raw.wheel_tick,
            1
        )

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                reactContext.getSystemService(VibratorManager::class.java)

            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            reactContext.getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }
    }

    override fun getName(): String {
        return NAME
    }

    override fun triggerImpact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createPredefined(
                    VibrationEffect.EFFECT_TICK
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    10L,
                    40
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10L)
        }
    }

    override fun triggerSound() {
        soundPool.play(
            tickSoundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    override fun triggerSoundAndImpact() {
        triggerSound()
        triggerImpact()
    }

    override fun invalidate() {
        soundPool.release()
        super.invalidate()
    }

    companion object {
        const val NAME = "ReactNativeWheelPickerFeedback"
    }
}
