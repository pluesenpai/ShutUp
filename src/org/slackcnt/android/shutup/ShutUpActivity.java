package org.slackcnt.android.shutup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShutUpActivity extends Activity
{
	private Intent shutUpServiceIntent;

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

		shutUpServiceIntent = new Intent(this, ShutUpService.class);
		Button btnStartStop = (Button) findViewById(R.id.btnStartStop);
		btnStartStop.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(isServiceRunning()) {
					stopService(shutUpServiceIntent);
				} else {
					startService(shutUpServiceIntent);
				}
			}
		});
	}

	private boolean isServiceRunning()
	{
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if(ShutUpService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}

		return false;
	}
}
