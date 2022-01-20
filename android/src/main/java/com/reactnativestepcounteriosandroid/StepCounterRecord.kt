package com.reactnativestepcounteriosandroid

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.ReactApplicationContext
import android.content.Context

class StepCounterRecord(reactContext: ReactApplicationContext) : SensorEventListener {

  private var mSensorManager: SensorManager = reactContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  private lateinit var mStepCounter:Sensor
  private var lastUpdate:Long = 0
  private var i = 0
  private var delay:Int = 0

  private var reactContext:ReactContext = reactContext

  fun start(delay: Int): Int {
		this.delay = delay
    mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST)
    return 1
	}

  fun stop() {
    mSensorManager.unregisterListener(this)
  }

  private fun sendEvent(eventName:String, params:WritableMap?) {
  try
  {
    reactContext
    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
    .emit(eventName, params)
  }
  catch (e:RuntimeException) {
    Log.e("ERROR", "java.lang.RuntimeException: Trying to invoke JS before CatalystInstance has been set!")
  }
}

  override fun onSensorChanged(sensorEvent: SensorEvent) {
    val mySensor = sensorEvent.sensor
    val map = Arguments.createMap()
    if (mySensor.type === Sensor.TYPE_STEP_COUNTER)
    {
      val curTime = System.currentTimeMillis()
      i++
      if ((curTime - lastUpdate) > delay)
      {
        i = 0
        val curSteps = sensorEvent.values[0].toDouble()
          if (curSteps != null) {
            map.putDouble("steps", curSteps)
          }

          sendEvent("StepCounter", map)
          lastUpdate = curTime
      }
    }
  }

  override fun onAccuracyChanged(sensor:Sensor, accuracy:Int) {}
}
