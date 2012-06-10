package org.slackcnt.android.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ShutUpReceiver extends BroadcastReceiver
{
	private static final String TAG = ShutUpReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean startAtBoot = sharedPreferences.getBoolean(
				ShutUpActivity.START_AT_BOOT, false);

		if(startAtBoot) {
			Log.i(TAG, "Starting Service");
			Intent myIntent = new Intent(context, ShutUpService.class);
			context.startService(myIntent);
			Log.i(TAG, "Started Service");
		} else {
			Log.i(TAG, "Preference set to not start at boot");
		}
	}
}
