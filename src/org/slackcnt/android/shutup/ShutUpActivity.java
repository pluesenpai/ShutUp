package org.slackcnt.android.shutup;

import org.slackcnt.android.shutup.ShutUpService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class ShutUpActivity extends Activity
{
	private static ShutUpService shutUpService;

	private final ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			shutUpService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			shutUpService = ((LocalBinder) service).getService();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Intent intent = new Intent(this, ShutUpService.class);
		if(shutUpService == null) {
			startService(intent);
		}
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
}
