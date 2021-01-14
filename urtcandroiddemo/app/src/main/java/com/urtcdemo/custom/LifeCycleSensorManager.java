package com.urtcdemo.custom;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 加速度变化监听器，可感知生命周期
 *
 * @author Richie on 2020.05.22
 */
public final class LifeCycleSensorManager implements LifecycleObserver, SensorEventListener {
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private Lifecycle mLifecycle;
    private OnAccelerometerChangedListener mOnAccelerometerChangedListener;

    public LifeCycleSensorManager(final Context context, final Lifecycle lifecycle) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lifecycle.addObserver(this);
        mLifecycle = lifecycle;
    }

    public void setOnAccelerometerChangedListener(final OnAccelerometerChangedListener onAccelerometerChangedListener) {
        mOnAccelerometerChangedListener = onAccelerometerChangedListener;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        mSensorManager.unregisterListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        mLifecycle.removeObserver(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (mOnAccelerometerChangedListener != null) {
                mOnAccelerometerChangedListener.onAccelerometerChanged(x, y, z);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface OnAccelerometerChangedListener {
        /**
         * 加速度变化
         *
         * @param x
         * @param y
         * @param z
         */
        void onAccelerometerChanged(float x, float y, float z);
    }
}
