package org.slackcnt.android.shutup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ShutUpActivity extends Activity
{
	public static final String START_AT_BOOT = "startAtBoot";

	private Intent shutUpServiceIntent;

	private Button btnStartStop;

	private TextView txtStatus;

	private CheckBox startAtBootCheckbox;

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

		initViews();
		setupListeners();
		setInitValues();
	}

	private void setupListeners()
	{
		btnStartStop.setOnClickListener(new StartStopOnClickListener());
		startAtBootCheckbox
				.setOnCheckedChangeListener(new StartAtBootOnCheckedChangeListener());
	}

	private void initViews()
	{
		btnStartStop = (Button) findViewById(R.id.btnStartStop);
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		startAtBootCheckbox = (CheckBox) findViewById(R.id.startAtBoot);
	}

	private void setInitValues()
	{
		if(isServiceRunning()) {
			txtStatus.setText(R.string.running);
			btnStartStop.setText(R.string.stop);
		} else {
			txtStatus.setText(R.string.stopped);
			btnStartStop.setText(R.string.start);
		}

		SharedPreferences sharedPreferences;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean startAtBoot = sharedPreferences
				.getBoolean(START_AT_BOOT, false);

		startAtBootCheckbox.setChecked(startAtBoot);
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

	// LISTENERS

	private final class StartStopOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(isServiceRunning()) {
				stopService(shutUpServiceIntent);
				btnStartStop.setText(R.string.start);
				txtStatus.setText(R.string.stopped);
			} else {
				startService(shutUpServiceIntent);
				btnStartStop.setText(R.string.stop);
				txtStatus.setText(R.string.running);
			}
		}
	}

	private final class StartAtBootOnCheckedChangeListener implements
			OnCheckedChangeListener
	{
		private final SharedPreferences sharedPreferences;

		private StartAtBootOnCheckedChangeListener()
		{
			this.sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(ShutUpActivity.this);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked)
		{
			Editor editor = sharedPreferences.edit();
			editor.putBoolean(START_AT_BOOT, isChecked);
			editor.commit();
		}
	}
}
