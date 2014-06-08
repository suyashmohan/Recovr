package com.recovr.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AppScreen extends Activity {
	
	private GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_screen);
		
		launchService();
	}
	
	public void launchService()
	{
		gps = new GPSTracker(this);
		TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
		if(!gps.isGPSAvailable())
		{			
			statusTextView.setText("GPS Not available");
		}
		else
		{
			statusTextView.setText("Tracking your phone");
			gps.stopTracking();
			gps = null;
			
			Intent serviceIntent = new Intent(this, BackGroundService.class);
			startService(serviceIntent);
		}
	}
	
	
}
