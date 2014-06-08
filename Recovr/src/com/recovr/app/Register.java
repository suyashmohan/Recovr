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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	
	private String username = "";
	private String password = "";
	private String fullname = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
	}
	
	public void registerBtnClicked(View view)
	{
		EditText fullnameEdit = (EditText) findViewById(R.id.fullTextEdit);
		fullname = fullnameEdit.getText().toString();
		EditText usernameEdit = (EditText) findViewById(R.id.userNameTextEdit);
		username = usernameEdit.getText().toString();
		EditText passwordEdit = (EditText) findViewById(R.id.passwordTextEdit);
		password = passwordEdit.getText().toString();
		
		if(fullname.length() == 0 || username.length() == 0 || password.length() == 0)
		{
			Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
		}
		else
		{
			doRegister(Util.registerURL);
		}
	}
	
	public void doRegisterDone(String result)
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
    
    public void doRegister(String URL)
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
    				params.add(new BasicNameValuePair("name", fullname));
    				params.add(new BasicNameValuePair("phone", username));
    				params.add(new BasicNameValuePair("password", password));
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
    			doRegisterDone(result);
    		}
    	}
    	
    	new HttpAsyncTask().execute(URL);
    }
}
