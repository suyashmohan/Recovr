package com.recovr.app;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private String username = "";
	private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void loginBtnClicked(View view)
    {
    	EditText usernameEdit = (EditText) findViewById(R.id.userNameTextEdit);
		username = usernameEdit.getText().toString();
		EditText passwordEdit = (EditText) findViewById(R.id.passwordTextEdit);
		password = passwordEdit.getText().toString();
		
		if(username.length() == 0 || password.length() == 0)
		{
			Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
		}
		else
		{
	    	doLogin(Util.loginURL);
		}
    }
    
    public void registerBtnClicked(View view)
    {
    	Intent intent = new Intent(this, Register.class);
    	startActivity(intent);
    }
    
    public void doLoginDone(String result)
    {
    	try
    	{
    		JSONObject json = new JSONObject(result);
    		String msg = json.getString("message");
    		String success = json.getString("response");

        	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        	
    		if(success.equals("true"))
    		{
    			Util.Username = username;
    			Intent intent = new Intent(this, AppScreen.class);
    	    	startActivity(intent);
    		}
    	}
    	catch(JSONException e)
    	{
    		
    	}
    }
    
    public void doLogin(String URL)
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
    				HttpPut put = new HttpPut(url[0]);
    				HttpResponse response;
    				
    				List<NameValuePair> params = new ArrayList<NameValuePair>();
    				params.add(new BasicNameValuePair("phone", username));
    				params.add(new BasicNameValuePair("password", password));
    				put.setEntity(new UrlEncodedFormEntity(params));
    	    	
    	    		response = client.execute(put);
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
    			doLoginDone(result);
    		}
    	}
    	
    	new HttpAsyncTask().execute(URL);
    }
    
}
