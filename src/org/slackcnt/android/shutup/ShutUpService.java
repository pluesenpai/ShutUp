package org.slackcnt.android.shutup;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ShutUpService extends Service implements SensorEventListener
{
	private static final String TAG = ShutUpService.class.getName();

	private final IBinder mBinder = new LocalBinder();

	private AudioManager audioManager;

	private SensorManager sensorManager;

	private Sensor accelerometer;

	private long lastUpdate = -1;

	private float last_z;

	private int ringerModeBefore = -1;

	private final BroadcastReceiver telephonyBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			PhoneStateListener phoneStateListener = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber)
				{
					Log.i(TAG, "Call state changed");

					switch(state) {
						case TelephonyManager.CALL_STATE_OFFHOOK:
						case TelephonyManager.CALL_STATE_IDLE:
							Log.i(TAG, "Call closed or answered");
							disableSensorListener();
							break;
						case TelephonyManager.CALL_STATE_RINGING:
							Log.i(TAG, "Incoming call");
							enableSensorListener();
							break;
					}
				}
			};

			TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			telephony.listen(phoneStateListener,
					PhoneStateListener.LISTEN_CALL_STATE);
		}
	};

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(TAG, "onCreate");

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(TAG, "onStartCommand");

		registerReceiver(telephonyBroadcastReceiver, new IntentFilter(
				TelephonyManager.ACTION_PHONE_STATE_CHANGED));

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		Log.i(TAG, "onDestroy");

		unregisterReceiver(telephonyBroadcastReceiver);
	}

	private void enableSensorListener()
	{
		Log.i(TAG, "enableSensorListener");
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			// do nothing if ringer is in mode vibrate or silent
			ringerModeBefore = -1;
			return;
		}

		Log.i(TAG, "enabling listeners");

		ringerModeBefore = audioManager.getRingerMode();
		List<Sensor> sensorList = sensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensorList.size() > 0) {
			accelerometer = sensorList.get(0);
			sensorManager.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Log.i(TAG, "No accelerometer");
		}
	}

	private void disableSensorListener()
	{
		Log.i(TAG, "disableSensorListener");
		if(sensorManager == null) {
			return;
		}

		if(ringerModeBefore > -1) {
			Log.i(TAG, "restoring ringer");
			audioManager.setRingerMode(ringerModeBefore);
		}

		sensorManager.unregisterListener(this);
		sensorManager = null;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		Log.i(TAG, "onAccuracyChanged");
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
			return;
		}

		Log.i(TAG, "onSensorChanged");
		long curTime = System.currentTimeMillis();

		if((curTime - lastUpdate) > 100) {
			lastUpdate = curTime;

			float z = event.values[2];
			if((last_z >= 0) && (z < 0)) {
				Log.i(TAG, "phone flipped face down");
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
			last_z = z;
		}
	}

	public class LocalBinder extends Binder
	{
		public ShutUpService getService()
		{
			return ShutUpService.this;
		}
	}
}
