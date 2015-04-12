package com.project.trafficshare;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.example.finalproject.JSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tawsif.finalproject.R;

public class MainPostActivity extends FragmentActivity implements
		android.view.View.OnClickListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	GoogleMap googleMap = null;
	LocationClient locationClient;
	double lat1, lon1, lat2, lon2;
	Marker m1 = null, m2 = null;
	LatLng loc1, loc2;
	Button btn,allBtn;
	Button traffic, accident, police, check_in, close1, close2, close3;
	Button high, med, low;
	Button post;
	public String final_category, final_level;
	public static int flag;
	JSONParser jParser = new JSONParser();
	LayoutInflater li;
	View firstView, secondView, thirdView;
	AlertDialog.Builder builder1, builder2, builder3;
	AlertDialog alertDialog1, alertDialog2, alertDialog3;
	int flag_post=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_condition);
		getActionBar().hide();
		
		btn = (Button) findViewById(R.id.button1);
		allBtn = (Button)findViewById(R.id.button2);
		li = LayoutInflater.from(com.project.trafficshare.MainPostActivity.this);

		firstView = li.inflate(R.layout.custom, null);
		traffic = (Button) firstView.findViewById(R.id.button1);
		accident = (Button) firstView.findViewById(R.id.button2);
		police = (Button) firstView.findViewById(R.id.button3);
		check_in = (Button) firstView.findViewById(R.id.button4);
		close1 = (Button) firstView.findViewById(R.id.exit);
		traffic.setOnClickListener(this);
		accident.setOnClickListener(this);
		police.setOnClickListener(this);
		check_in.setOnClickListener(this);
		close1.setOnClickListener(this);

		secondView = li.inflate(R.layout.secustom, null);
		high = (Button) secondView.findViewById(R.id.sebutton1);
		med = (Button) secondView.findViewById(R.id.sebutton2);
		low = (Button) secondView.findViewById(R.id.sebutton3);
		close2 = (Button) secondView.findViewById(R.id.exit2);
		high.setOnClickListener(this);
		med.setOnClickListener(this);
		low.setOnClickListener(this);
		close2.setOnClickListener(this);

		thirdView = li.inflate(R.layout.thcustom, null);
		post = (Button) thirdView.findViewById(R.id.thbutton1);
		close3 = (Button) thirdView.findViewById(R.id.exit3);
		post.setOnClickListener(this);
		close3.setOnClickListener(this);

		builder1 = new AlertDialog.Builder(com.project.trafficshare.MainPostActivity.this);
		builder1.setCancelable(false);
		builder1.setView(firstView);
		alertDialog1 = builder1.create();
		alertDialog1.getWindow().setBackgroundDrawableResource(
				R.drawable.border_main);
		alertDialog1.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

		builder2 = new AlertDialog.Builder(com.project.trafficshare.MainPostActivity.this);
		builder2.setCancelable(false);
		builder2.setView(secondView);
		alertDialog2 = builder2.create();
		alertDialog2.getWindow().setBackgroundDrawableResource(
				R.drawable.border_main);
		alertDialog2.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

		builder3 = new AlertDialog.Builder(com.project.trafficshare.MainPostActivity.this);
		builder3.setCancelable(false);
		builder3.setView(thirdView);
		alertDialog3 = builder3.create();
		alertDialog3.getWindow().setBackgroundDrawableResource(
				R.drawable.border_main);
		alertDialog3.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				alertDialog1.show();

			}
		});
		
		allBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new GetEverythingTask().execute();
			}
		});

		if (isMapAvailable()) {
			try {
				googleMap = ((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map)).getMap();
				if (googleMap == null) {
					Toast.makeText(getApplicationContext(),
							"Unable to create map", Toast.LENGTH_LONG).show();
				} else {
					googleMap.setMyLocationEnabled(true);
					locationClient = new LocationClient(this, this, this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Google Maps is not in your service", Toast.LENGTH_LONG)
					.show();
			finish();
		}
		
		



	}
	
	public class GetEverythingTask extends AsyncTask<String, String, String>{

		JSONParser jParser = new JSONParser();
		ProgressDialog dialog;
		String res = "";
		JSONObject json;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> param = new ArrayList<NameValuePair>();

			param.add(new BasicNameValuePair("lat", lat1+""));
			param.add(new BasicNameValuePair("lon", lon1+""));
			// params.add(new BasicNameValuePair("regId", regId));

			json = jParser.makeHttpRequest(
					"http://appseden.net/yellowctg/yellowctg/JSON/getEverything.php", "POST",
					param);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			int success = 0;
			try{
			if(json !=null)
				success = json.getInt("success");
			if(success == 1){
				JSONArray products = json.getJSONArray("data");
				Toast.makeText(getApplicationContext(), "here", 1000).show();
			for (int i = 0; i < products.length(); i++) {

				JSONObject c = products.getJSONObject(i);
				String type = c.getString("com_name");
				String lat = c.getString("lat");
				String lon = c.getString("long");
				LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
				
				m1 = googleMap.addMarker(new MarkerOptions().position(loc)
						.title(type));
				m1.setIcon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				m1.showInfoWindow();
			}
			}else
				Toast.makeText(getApplicationContext(), "no event", Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				
			}
		}
		
	}
	
	public class GetInfoTask extends AsyncTask<String, String, String>{

		JSONParser jParser = new JSONParser();
		ProgressDialog dialog;
		String res = "";
		JSONObject json;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> param = new ArrayList<NameValuePair>();

			param.add(new BasicNameValuePair("lat", lat1+""));
			param.add(new BasicNameValuePair("lon", lon1+""));
			// params.add(new BasicNameValuePair("regId", regId));

			json = jParser.makeHttpRequest(
					"http://appseden.net/foundit/tracker/getInfo.php", "POST",
					param);

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			int success = 0;
			try{
			if(json !=null)
				success = json.getInt("success");
			if(success == 1){
				JSONArray products = json.getJSONArray("data");

			for (int i = 0; i < products.length(); i++) {

				JSONObject c = products.getJSONObject(i);
				String type = c.getString("category");
				String lat = c.getString("lati");
				String lon = c.getString("longi");
				LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
				m1 = googleMap.addMarker(new MarkerOptions().position(loc)
						.title(type));
				m1.setIcon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				m1.showInfoWindow();
			}
			}else
				Toast.makeText(getApplicationContext(), "no event", Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				
			}
		}
		
	}

	public void openSecond() {
		alertDialog1.cancel();
		alertDialog2.show();
	}

	public void openThird() {
		alertDialog2.cancel();
		alertDialog3.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			openSecond();
			final_category = "Traffic Jam";
			break;
		case R.id.button2:
			openSecond();
			final_category = "Accident";
			break;
		case R.id.button3:
			openSecond();
			final_category = "Police";
			break;
		case R.id.button4:
			openSecond();
			final_category = "Check in";
			break;
		case R.id.sebutton1:
			openThird();
			final_level = "high";
			break;
		case R.id.sebutton2:
			openThird();
			final_level = "med";
			break;
		case R.id.sebutton3:
			openThird();
			final_level = "low";
			break;
		case R.id.thbutton1:
			alertDialog3.cancel();
			// alertDialog.dismiss();
			new AsyncTaskRunner().execute();
			break;
		case R.id.exit:
			alertDialog1.cancel();
			break;
		case R.id.exit2:
			alertDialog2.cancel();
			break;
		case R.id.exit3:
			alertDialog3.cancel();
			break;
		default:
			break;
		}
	}

	public class AsyncTaskRunner extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();
		ProgressDialog dialog;
		String res = "";
		JSONObject json;

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("category", final_category));
			params.add(new BasicNameValuePair("level", final_level));
			params.add(new BasicNameValuePair("lat", lat1+""));
			params.add(new BasicNameValuePair("lon", lon1+""));
			// params.add(new BasicNameValuePair("regId", regId));

			json = jParser.makeHttpRequest(
					"http://appseden.net/foundit/tracker/postCondition.php", "POST",
					params);

			return res;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(MainPostActivity.this);
			dialog.setMessage("Posting...");

			dialog.show();

		}

		protected void onPostExecute(String result) {
			dialog.dismiss();

			int success = 0;
			try {
				success = json.getInt("success");
				if (success == 1) {
					Toast.makeText(getApplicationContext(), success + "",
							Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "nope", 500).show();
			}

		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Location location = locationClient.getLastLocation();
		if(flag_post == 0){
		if (location == null)
			Toast.makeText(getApplicationContext(),
					"Current location not found", Toast.LENGTH_LONG).show();

		else {
			try {
				lat1 = location.getLatitude();
				lon1 = location.getLongitude();
				if (lat1 != 0 && lon1 != 0) {
					loc1 = new LatLng(lat1, lon1);
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
							loc1, 13.0f));// cameraup hocce 1ta class& newLatLng
											// hocce 1ta method,loc1 dekabe & 13
											// porjonto zoom krbe;
					m1 = googleMap.addMarker(new MarkerOptions().position(loc1)
							.title("You are here")); 
					m1.setIcon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

					

					m1.showInfoWindow();// 1st location e 1 ta window show
										// korbe;
					
					new GetInfoTask().execute();
					Toast.makeText(getApplicationContext(), flag+"", 1000).show();
					flag_post++;
				}

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Current location not found", Toast.LENGTH_LONG).show();
			}
		}
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		lat1 = 0;
//		lon1 = 0;
		// lat2=0;
		// lon2=0;
		locationClient.connect();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub[
		super.onPause();
		locationClient.disconnect();
	}

	public boolean isMapAvailable() {

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (ConnectionResult.SUCCESS == resultCode) {

			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

			Dialog d = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					1);
			d.show();
		} else {
			Toast.makeText(getApplicationContext(),
					"Google MAps API V@ is not supported in your service",
					Toast.LENGTH_LONG).show();
			finish();
		}
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, (android.view.Menu) menu);
		return true;
	}

	
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
