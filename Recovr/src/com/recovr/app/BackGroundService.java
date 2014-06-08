package com.recovr.app;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class BackGroundService extends Service implements GPSTrackerListener {
	
	private GPSTracker gps;
	private long delay = 10*60*1000;

	public BackGroundService()
	{
			
	}
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		gps = new GPSTracker(this);
		if(!gps.isGPSAvailable())
		{			
			Toast.makeText(this, "GPS Not available", Toast.LENGTH_SHORT).show();
			gps = null;
		}
		else
		{
			gps.setListener(this);
			Toast.makeText(this, "Tracking your Phone", Toast.LENGTH_SHORT).show();
		}
	    return super.onStartCommand(intent,flags,startId);
	}
		
	@Override
	public void onDestroy() {
	    Toast.makeText(this, "Tracking of your phone has stopped", Toast.LENGTH_SHORT).show();
	    gps.stopTracking();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void LocationChanged(double latitude, double longitude) {
		Toast.makeText(this, longitude+"x"+latitude, Toast.LENGTH_SHORT).show();
		if(gps != null)
			gps.stopTracking();
		gps = null;
		doSendCoordinates(Util.coordinateURL, Util.Username, Double.toString(latitude), Double.toString(longitude));
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				checkGPSAgain();
			}
		}, delay);
	}
	
	private void checkGPSAgain()
	{
		gps = new GPSTracker(this);
		if(!gps.isGPSAvailable())
		{			
			Toast.makeText(this, "GPS Not available", Toast.LENGTH_SHORT).show();
			gps = null;
		}
		else
		{
			gps.setListener(this);
			Toast.makeText(this, "Again Checking Location", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doSendCoordinatesDone(String result)
    {
		try
		{
			JSONObject json = new JSONObject(result);
    		String msg = json.getString("message");
    		String success = json.getString("response");

        	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        	
    		if(success.equals("true"))
    		{
    			
    		}			
		}
		catch(JSONException e)
		{
			
		}
    }
    
    public void doSendCoordinates(String URL, final String phone, final String latitude, final String longitude)
    {    	
    	class HttpAsyncTask extends AsyncTask<String, Void, String>
    	{
    		@Override
    		protected String doInBackground(String...url)
    		{
    			String result = "";
    			
    			try
    	    	{    				
    				HttpClient client = new DefaultHttpClient();
    				HttpPost post = new HttpPost(url[0]);
    				HttpResponse response;
    				
    				List<NameValuePair> params = new ArrayList<NameValuePair>();
    				params.add(new BasicNameValuePair("phone", phone));
    				params.add(new BasicNameValuePair("lat", latitude));
    				params.add(new BasicNameValuePair("long", longitude));
    				post.setEntity(new UrlEncodedFormEntity(params));
    	    	
    	    		response = client.execute(post);
    	    		result = response.getStatusLine().toString();
    	    		HttpEntity entity = response.getEntity();
    	    		if(entity != null)
    	    		{
    	    			InputStream istream = entity.getContent();
    	    			result = Util.readInputStreamAsString(istream);
    	    			istream.close();
    	    		}
    	    	}
    	    	catch(Exception e)
    	    	{
    	    		result = e.toString();
    	    	}
    			return result;
    		}
    		
    		@Override
    		protected void onPostExecute(String result)
    		{
    			doSendCoordinatesDone(result);
    		}
    	}
    	
    	new HttpAsyncTask().execute(URL);
    }
}
