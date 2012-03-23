package org.slackcnt.android.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShutUpReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.i(ShutUpReceiver.class.getName(), "Starting Service");
		Intent myIntent = new Intent(context, ShutUpService.class);
		context.startService(myIntent);
		Log.i(ShutUpReceiver.class.getName(), "Started Service");
	}
}
