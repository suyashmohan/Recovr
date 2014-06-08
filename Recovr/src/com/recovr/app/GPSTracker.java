package com.recovr.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSTracker implements LocationListener {
	
	private LocationManager locationManager = null;
	private Context mContext = null;
	private boolean isGPSProvider = false;
	private boolean isNetworkProvider = false;
	private long minTime = 0;
	private float minDistance = 0;
	private Location location = null;
	private boolean canGetLocation = false;
	private GPSTrackerListener mListener = null;
	
	public GPSTracker(Context context)
	{
		mContext = context;
		getLocation();
	}
	
	public Location getLocation()
	{
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		
		if(locationManager != null)
		{		
			isGPSProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if(!isGPSProvider && !isNetworkProvider)
			{
				
			}
			else
			{
				canGetLocation = true;
				
				if(isNetworkProvider)
				{
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(mListener != null && location != null)
						mListener.LocationChanged(location.getLongitude(), location.getLatitude());
				}
				
				if(isGPSProvider)
				{
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if(mListener != null && location != null)
						mListener.LocationChanged(location.getLongitude(), location.getLatitude());
				}
			}
		}
		
		return location;
	}
	
	public boolean isGPSAvailable()
	{
		return canGetLocation;
	}
	
	public void setListener(GPSTrackerListener listener)
	{
		mListener = listener;
	}
	
	public void stopTracking()
	{
		if(locationManager != null)
			locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		if(mListener != null)
			mListener.LocationChanged(loc.getLongitude(), loc.getLatitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}

}
