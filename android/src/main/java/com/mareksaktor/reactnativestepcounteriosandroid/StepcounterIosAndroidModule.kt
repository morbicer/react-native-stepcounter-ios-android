package com.mareksaktor.reactnativestepcounteriosandroid

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import android.content.Context;
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

class StepcounterIosAndroidModule : ReactContextBaseJavaModule {

  private var mStepCounterRecord:StepCounterRecord? = null
  private var mReactContext:ReactApplicationContext

  private var mSensorManager: SensorManager

  constructor(reactContext: ReactApplicationContext) : super(reactContext) {
    this.mReactContext = reactContext
    mSensorManager = reactContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  }

    override fun getName(): String {
        return "StepcounterIosAndroid"
    }

    @ReactMethod
    fun isSupported(promise: Promise) {
      val mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
      if (mStepCounter != null) {
        promise.resolve(true)
      } else {
        promise.resolve(false)
      }
    }

    @ReactMethod
    fun startStepCounter(): Int {
      if (Build.VERSION.SDK_INT >= 29) {
        Log.d("stepCounter", "message")
        if (ContextCompat.checkSelfPermission(this.mReactContext,
            Manifest.permission.ACTIVITY_RECOGNITION)
          != PackageManager.PERMISSION_GRANTED) {

          if (ActivityCompat.shouldShowRequestPermissionRationale(this.currentActivity!!,
              Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
          } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this.currentActivity!!,
              arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1)
            if (ContextCompat.checkSelfPermission(this.mReactContext,
                Manifest.permission.ACTIVITY_RECOGNITION)
              != PackageManager.PERMISSION_GRANTED) {
              Log.d("stepCounter", "permission not granted")
            }
          }
        }
      }
      if (mStepCounterRecord == null)
        mStepCounterRecord = StepCounterRecord(mReactContext)
      val stepCounterRecord = mStepCounterRecord
      val delay = 1000
      if (stepCounterRecord != null)
        return stepCounterRecord.start(delay)
      return 0
    }

    @ReactMethod
    fun stopStepCounter() {
      val stepCounterRecord = mStepCounterRecord
      if (stepCounterRecord != null)
        mStepCounterRecord?.stop();
    }
}
