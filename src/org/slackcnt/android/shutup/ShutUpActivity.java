package org.slackcnt.android.shutup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ShutUpActivity extends Activity
{
	// private final ServiceConnection serviceConnection = new
	// ServiceConnection() {
	// @Override
	// public void onServiceDisconnected(ComponentName arg0)
	// {
	// }
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service)
	// {
	// }
	// };

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent = new Intent(this, ShutUpService.class);
		bindService(intent, null, Context.BIND_AUTO_CREATE);
	}
}
