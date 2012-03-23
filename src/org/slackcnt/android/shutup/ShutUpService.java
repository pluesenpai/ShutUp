package org.slackcnt.android.shutup;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ShutUpService extends Service implements SensorEventListener
{
	// TODO: capture phone events, enable ShutUp when ringing and restore old
	// values of ringer and vibrate at end of ringing

	private final IBinder mBinder = new LocalBinder();

	private AudioManager audioManager;

	private SensorManager sensorManager;

	private Sensor accelerometer;

	private long lastUpdate = -1;

	private float last_z;

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(ShutUpService.class.getName(), "onCreate");

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		List<Sensor> sensorList = sensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensorList.size() > 0) {
			accelerometer = sensorList.get(0);
			sensorManager.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Log.i(ShutUpService.class.getName(), "No accelerometer");
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	public class LocalBinder extends Binder
	{
		public ShutUpService getService()
		{
			return ShutUpService.this;
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		Log.i(ShutUpService.class.getName(), "onAccuracyChanged");
	}

	public void onSensorChanged(SensorEvent event)
	{
		Log.i(ShutUpService.class.getName(), "onSensorChanged");
		long curTime = System.currentTimeMillis();

		// only allow one update every 100ms.
		if((curTime - lastUpdate) > 100) {
			lastUpdate = curTime;

			// TODO: save old state of volume and vibrate
			float z = event.values[2];
			if(last_z >= 0 && z < 0) {
				audioManager
						.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
			}
			last_z = z;
		}
	}

}
