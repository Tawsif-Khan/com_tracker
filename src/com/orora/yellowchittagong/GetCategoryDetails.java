package com.orora.yellowchittagong;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class GetCategoryDetails extends Activity {
	LinearLayout applyEIIN, submittedDetails, myEIIN, nearestEIIN, allEIIN,
			searchIntitute, recover, userGuide;
	ArrayList<String> name = new ArrayList<String>();
	ArrayList<String> app_id = new ArrayList<String>();
	ArrayList<String> lat = new ArrayList<String>();
	ArrayList<String> lon = new ArrayList<String>();
	ArrayList<String> nameCenter = new ArrayList<String>();
	ArrayList<String> center_id = new ArrayList<String>();

	JSONParser jParser = new JSONParser();

	boolean statusOfGPS, statusOfInternet, statusOfNetwork;
	Context context;
	int flag =0;
	public static Activity srch;

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}
	LinearLayout call_emer,emer_id;
	TextView category;
	String category1, s;
	ImageButton search, viewall, nearest, home;
	TextView call_now, emer;
	Button h_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		srch = this;
		
		category = (TextView) findViewById(R.id.category);
		search = (ImageButton) findViewById(R.id.search);
		call_emer = (LinearLayout) findViewById(R.id.call_emer);
		emer_id = (LinearLayout) findViewById(R.id.emer_id);

		call_now = (TextView) findViewById(R.id.call_now);
		emer = (TextView) findViewById(R.id.emer);

		home = (ImageButton) findViewById(R.id.image_home);

		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GetCategoryDetails.this, MainActivity.class);
				startActivity(intent);
				MainActivity.ac.finish();
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.right_out_back);
				
				
			}
		});



		// start of code to get the category from share preference in which we r
		// now
		SharedPreferences settings = getSharedPreferences("myprefs", 0);
		category1 = settings.getString("session", "");
		String category1_name = settings.getString("session_name", "");
		category.setText(category1_name);
		// end of code to get the category from share preference in which we r
		// now

		

		SharedPreferences setting = getSharedPreferences("myprefs", 0);
		s = setting.getString("session", "");

		if (s.equals("Police Station")) {
			call_emer.setVisibility(View.VISIBLE);
			call_now.setVisibility(View.VISIBLE);
			call_emer.setPadding(0, 0, 0, 0);
			emer.setHeight(60);
			
			emer.setVisibility(View.INVISIBLE);

		} else if (s.equals("Hospital")) {
			call_emer.setVisibility(View.VISIBLE);
			emer.setVisibility(View.VISIBLE);
		}else {
			call_emer.setVisibility(View.INVISIBLE);
			emer.setVisibility(View.INVISIBLE);
			call_now.setVisibility(View.INVISIBLE);
		}

		emer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GetCategoryDetails.this, Hospital.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_slide_in,
						R.anim.right_slide_out);

			}
		});

		call_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				flag=1;
//				performDial("01731887844");
				nearest();

			}
		});

		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(GetCategoryDetails.this,DetailsInfo.class);
				 if(netConnection()){
				 startActivity(intent);
				 overridePendingTransition (R.anim.right_slide_in,
				 R.anim.right_slide_out);
				 }else{
				 Toast.makeText(getApplicationContext(),
				 "No internet connection.", Toast.LENGTH_LONG).show();
				 }

			}
		});

		viewall = (ImageButton) findViewById(R.id.viewall);
		viewall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(GetCategoryDetails.this, AllDetailsInfo.class);
				if (netConnection()) {
					startActivity(intent);
					overridePendingTransition(R.anim.right_slide_in,
							R.anim.right_slide_out);
				} else {
					Toast.makeText(getApplicationContext(),
							"No internet connection.", Toast.LENGTH_LONG)
							.show();
				}
			}

		});

		nearest = (ImageButton) findViewById(R.id.nearest);
		nearest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				nearest();

			}
		});

	}
	
	public void nearest(){
		try {
			// Loading map
			LocationManager manager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
			statusOfGPS = manager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			statusOfNetwork = manager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			statusOfInternet = isConnectingToInternet();

			if ((statusOfGPS == false && statusOfNetwork == false)
					|| statusOfInternet == false) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView;
				AlertDialog.Builder builder;
				final AlertDialog alertDialog;
				Button btnOk = null;
				if (statusOfGPS == false && statusOfInternet == false
						&& statusOfNetwork == false) {
					promptsView = li.inflate(R.layout.int_loc_dialog,
							null);
					builder = new AlertDialog.Builder(context);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView
							.findViewById(R.id.btnOk);
				} else if (statusOfInternet == false
						&& (statusOfGPS == true || statusOfNetwork == true)) {
					promptsView = li.inflate(R.layout.int_dialog, null);
					builder = new AlertDialog.Builder(context);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView
							.findViewById(R.id.btnOk);
				} else {
					promptsView = li.inflate(R.layout.loc_dialog, null);
					builder = new AlertDialog.Builder(context);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView
							.findViewById(R.id.btnOk);
				}

				btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						alertDialog.cancel();
					}
				});

			}

			else {
				if (statusOfNetwork == false) {
					Toast.makeText(
							getApplicationContext(),
							"Please turn Network status of your device oN",
							Toast.LENGTH_LONG).show();
				} else {
					try {

						new AsyncTaskRunnerNearest().execute();

					} catch (Exception e) {
						Toast.makeText(
								getApplicationContext(),
								"Sorry an error occured duting the execution",
								Toast.LENGTH_LONG).show();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(
					getApplicationContext(),
					"Internet status and GPS status of device can not be accesed",
					Toast.LENGTH_LONG).show();
		}
	}

	
	

	private class AsyncTaskRunnerNearest extends
			AsyncTask<String, String, String> {
		String res = "";
		ArrayList<String> contact;
		ProgressDialog dialog;

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			center_id = new ArrayList<String>();
			name = new ArrayList<String>();
			lat = new ArrayList<String>();
			lon = new ArrayList<String>();
			contact = new ArrayList<String>();
			SharedPreferences settings = getSharedPreferences("myprefs", 0);

			String category = settings.getString("session", "");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("catagory", category));

			JSONObject json = jParser.makeHttpRequest(
					"http://www.appseden.net/yellowctg/yellowctg/JSON/nearestcenter.php", "POST",
					params);
			try {
				int success = json.getInt("success");

				if (success == 1) {

					JSONArray products = json.getJSONArray("center");
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						String id = c.getString("center_id");
						String a = c.getString("lat");
						String b = c.getString("lon");
						String n = c.getString("center_name");
						String cont = c.getString("contact");
						String branch_name = c.getString("branch_name");

						center_id.add(id);
						lat.add(a);
						lon.add(b);
						nameCenter.add(n+", "+branch_name);
						contact.add(cont);
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;

		}

		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(GetCategoryDetails.this);
			if(flag==1)
				dialog.setMessage("Calling...");
			else
				dialog.setMessage("Loading...");
			dialog.show();
		}

		protected void onPostExecute(String result) {
			
			dialog.dismiss();
			Intent intent;
			if (flag==1){ 
				flag=0;
				intent = new Intent(GetCategoryDetails.this,CallToNearest.class);
			}else				
				intent = new Intent(GetCategoryDetails.this, NearestCenter.class);

			intent.putExtra("center_id", center_id);
			intent.putExtra("center_name", nameCenter);
			intent.putExtra("center_lat", lat);
			intent.putExtra("center_lon", lon);
			intent.putExtra("contact", contact);

			startActivity(intent);
		}

	}
	
	public boolean netConnection() {

		ConnectivityManager cm = (ConnectivityManager) GetCategoryDetails.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();

		return isConnected;
	}

	@Override
	public void onBackPressed() {

		this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.right_out_back);
	}

	private void performDial(String numberString) {
		if (!numberString.equals("")) {
			Uri number = Uri.parse("tel:" + numberString);
			Intent dial = new Intent(Intent.ACTION_CALL, number);
			startActivity(dial);
		}
	}
}
