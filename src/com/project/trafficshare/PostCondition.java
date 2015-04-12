package com.project.trafficshare;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.example.finalproject.EditProfile;
import com.example.finalproject.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class PostCondition extends Activity{
	
	String category, level, lat, lon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		category = "Jam";
		level = "high";
		lat = "91.31455";
		lon = "22.21345";
		
		new AsyncTaskRunner().execute();
		
	}
	
	
	private class AsyncTaskRunner extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();
		ProgressDialog dialog;
		String res = "";
		JSONObject json;

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("category", category));
			params.add(new BasicNameValuePair("level", level));
			params.add(new BasicNameValuePair("lat", lat));
			params.add(new BasicNameValuePair("lon", lon));

			json = jParser.makeHttpRequest(
					"http://tsquareltd.com/tracker/postCondition.php", "POST",
					params);

			return res;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(PostCondition.this);
			dialog.setMessage("Please wait...");

			dialog.show();

		}

		protected void onPostExecute(String result) {
			dialog.dismiss();

			Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_LONG).show();
			
		}

	}
}
