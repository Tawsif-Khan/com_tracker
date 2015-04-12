package com.iiuc.finalproject.registration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.Map.CreateMarker;
import com.example.finalproject.JSONParser;
import com.example.finalproject.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.nearby.welcome.ConnectionDetector;
import com.tawsif.finalproject.R;

public class SecuritySMS extends SherlockActivity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,LocationListener{

	TextView percent,registerTextView;
	public static int loadFlag = 0;
	String dwnload_file_path = "http://appseden.net/foundit/tracker/uploadimage/";//www.boopathyraja.com/Android_UI_Design.zip";
//	private ProgressDialog mProgressDialog;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public static String msgSent;
	TextView title;
	public static EditText sec_code_view;
	public static Button done,inactiveBtn;
	String image_url,num,regID;
	ArrayList<String> number, name, reg_id, final_name, final_number,user_image,latiList,longiList;
	ArrayList<Bitmap> bmList;
	List<NameValuePair> params;
	SQLiteDatabase db;
	LinearLayout progLL,nextProgress;
	ProgressBar pb,regProgressbar;
	File dir;
	LocationManager mLocationManager;
	LocationClient locationClient;
	Double latitude,longitude;
	int numberOfImage = 0;
	LinearLayout smsBoxLL;
	public static Activity sec_sms;
	ImageView arrowImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.security_sms);
		
		sec_sms = this;
		
		File root = android.os.Environment.getExternalStorageDirectory();
		dir = new File(root.getAbsolutePath() + "/Com_tracker/");
		if (dir.exists() == false) 
		{
			dir.mkdirs();
		}
		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
		
		msgSent = (int)(Math.random()*10000+1)+"";

//		SmsManager sms = SmsManager.getDefault();
//	    sms.sendTextMessage(RegisterName.number, null, msgSent, null, null);
	    
	    Toast.makeText(getApplicationContext(), msgSent, Toast.LENGTH_LONG).show();
	    
		smsBoxLL = (LinearLayout)findViewById(R.id.smsBox);
		title = (TextView) findViewById(R.id.title);
		sec_code_view = (EditText) findViewById(R.id.code);
		done = (Button)findViewById(R.id.done);
		inactiveBtn = (Button)findViewById(R.id.inactivebtn);
		
		done.setVisibility(View.GONE);
		
		number = new ArrayList<String>();
		name = new ArrayList<String>();
		final_number = new ArrayList<String>();
		final_name = new ArrayList<String>();
		reg_id = new ArrayList<String>();
		user_image = new ArrayList<String>();
		latiList = new ArrayList<String>();
		longiList = new ArrayList<String>();
		params = new ArrayList<NameValuePair>();
		bmList = new ArrayList<Bitmap>();
		
		sec_code_view.addTextChangedListener(new TextWatcher()
		{
		    public void afterTextChanged(Editable s) 
		    {
		    	String temp = sec_code_view.getText().toString();
				
				if(temp.equals(msgSent)){
					done.setVisibility(View.VISIBLE);
					inactiveBtn.setVisibility(View.GONE);
				}else{
					done.setVisibility(View.GONE);
					inactiveBtn.setVisibility(View.VISIBLE);
				}

		    }
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		    {
		        /*This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after. It is an error to attempt to make changes to s from this callback.*/ 
		    }
		    public void onTextChanged(CharSequence s, int start, int before, int count) 
		    {
		    }
		});
		
		
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String code = sec_code_view.getText().toString();
				if(!code.equals("")){
				setContentView(R.layout.progress_view);
				
				progLL = (LinearLayout)findViewById(R.id.progressView);
				pb = (ProgressBar)findViewById(R.id.progressBar1);
				percent = (TextView)findViewById(R.id.percent);
				registerTextView = (TextView)findViewById(R.id.registerTextView);
				nextProgress = (LinearLayout)findViewById(R.id.nextprogress);
				arrowImage = (ImageView)findViewById(R.id.rightArrow);
				regProgressbar = (ProgressBar)findViewById(R.id.progressRegistering);
				
				arrowImage.setVisibility(View.INVISIBLE);
				nextProgress.setVisibility(View.INVISIBLE);
				registerTextView.setText("Registering...");
				
				loadNow();
				}else{
					Toast.makeText(getApplicationContext(), "Insert varification code", Toast.LENGTH_LONG).show();
				}
			}
		});

	}
	
	public void getNumbers() {

		Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, Phone.DISPLAY_NAME + " ASC");
		//String phoneNumber = "";

		int i = 0;
		String num = "";
		while (phones.moveToNext()) {
			name.add(phones.getString(phones
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

			num = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			number.add(num);

			params.add(new BasicNameValuePair("number[]", num));
			params.add(new BasicNameValuePair("ind[]", i + ""));
			i++;
		}
		phones.close();

		Uri simUri = Uri.parse("content://icc/adn");

		Cursor cursorSim = getContentResolver().query(simUri, null, null,
				null, null);

		while (cursorSim.moveToNext()) {
			name.add(cursorSim.getString(cursorSim.getColumnIndex("name")));
			// listContactId.add(cursorSim.getString(cursorSim.getColumnIndex("_id")));
			num = cursorSim.getString(cursorSim.getColumnIndex("number"));
			number.add(num);
			params.add(new BasicNameValuePair("number[]", num));
			params.add(new BasicNameValuePair("ind[]", i + ""));
			i++;

		}
	}
	
	private class GetUserAsyncTaskRunner extends AsyncTask<String, String, String> {

//		ProgressDialog dialog;
		String res = "";
		JSONParser jParser = new JSONParser();
		JSONObject json;

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			json = jParser.makeHttpRequest(
					"http://appseden.net/foundit/tracker/getUsers.php", "POST",
					params);

			return res;
		}

		protected void onPreExecute() {
			super.onPreExecute();

		}

		protected void onPostExecute(String result) {
//			dialog.dismiss();
//			progLL.setVisibility(View.GONE);
			String lati,longi;
			try {
				int success = json.getInt("success");

				if (success == 1) {
					String ind = "";
					JSONArray products = json.getJSONArray("details");

					for (int i = 0; i < products.length(); i++) {

						JSONObject c = products.getJSONObject(i);
						num = c.getString("phone");
						regID = c.getString("reg_id");
						image_url = c.getString("user_image");
						lati = c.getString("lati");
						longi = c.getString("longi");
						
						final_number.add(num);
						reg_id.add(regID);
						ind = c.getString("index");
						String nameS = name.get(Integer.parseInt(ind));
						final_name.add(nameS);
						user_image.add(image_url);
						latiList.add(lati);
						longiList.add(longi);
						if(image_url.equals("")){
							numberOfImage++;
						}
					}
					
					regProgressbar.setVisibility(View.GONE);
					nextProgress.setVisibility(View.VISIBLE);
					arrowImage.setVisibility(View.VISIBLE);
					registerTextView.setText("Registered.");
					
					
					new DownloadFileAsync().execute(image_url);
	

				} else{
					new DownloadFileAsync().execute(image_url);
					Toast.makeText(getApplicationContext(), "No friends are using this app",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Error",
						Toast.LENGTH_LONG).show();
			}

		}

	}
	
	
	private class RegisterAsyncTaskRunner extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();
//		ProgressDialog dialog;
		String res = "";
		JSONObject json;

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			
			params.add(new BasicNameValuePair("lati", latitude+""));
			params.add(new BasicNameValuePair("longi", longitude+""));
			params.add(new BasicNameValuePair("number", RegisterName.number));
			params.add(new BasicNameValuePair("country_code", RegisterName.country_code));
			params.add(new BasicNameValuePair("regId", RegisterName.regId));

			json = jParser.makeHttpRequest(
					"http://appseden.net/foundit/tracker/register.php", "POST",
					params);

			return res;
		}

		protected void onPreExecute() {
			super.onPreExecute();

		}

		protected void onPostExecute(String result) {
//			dialog.dismiss();

			int success = 0;
			try {
				if(json != null)
				success = json.getInt("success");
				if (success == 1) {
					getNumbers();
					new GetUserAsyncTaskRunner().execute();
					
				}
//				Toast.makeText(getApplicationContext(), success + "",
//						Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_SHORT).show();
			}

		}

	}

	@SuppressLint("SdCardPath")
	class DownloadFileAsync extends AsyncTask<String, String, String> 
	{
		
		byte data[];
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			
		}

		@Override
		protected String doInBackground(String... args) 
		{	
			Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_down);
			Bitmap defaultIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.man);
			
			CreateMarker cmDefault = new CreateMarker(defaultIcon1,arrow);
			byte[] defaultMarker = Utility.getBytes(cmDefault.getMarker());
			
			
			db = openOrCreateDatabase("tracker", 0, null);
			int totalFileLenght = 0;
			for(int i=0 ; i<final_number.size() ; i++){
				String urlLink = dwnload_file_path+"/"+user_image.get(i);
				URL url;
				try {
					url = new URL(urlLink);
					URLConnection connection = url.openConnection();
					connection.connect();
					int fileLength = connection.getContentLength();
					totalFileLenght = totalFileLenght + fileLength;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			long total = 0;
			for(int i=0 ; i<final_number.size() ; i++){
			String urlLink = dwnload_file_path+"/"+user_image.get(i);
			final String fileName = user_image.get(i)+".jpg";
			if(!user_image.get(i).equals("")){
			try 
			{
				URL url = new URL(urlLink);
				URLConnection connection = url.openConnection();
				connection.connect();
//				int fileLength = connection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(dir + "/" + fileName);

				final byte data[] = new byte[1024];
				
				int count;
				while ((count = input.read(data)) != -1)
				{
					total += count;
					publishProgress(""+(int)((total*100)/totalFileLenght));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();	
				
				Bitmap pic_bm = BitmapFactory.decodeFile(dir+"/"+fileName);
				CreateMarker cm = new CreateMarker(pic_bm,arrow);
				
				ContentValues cv = new ContentValues();
				cv.put("image", Utility.getBytes(pic_bm));
				cv.put("name", final_name.get(i));
				cv.put("phoneNumber", final_number.get(i));
				cv.put("reg_id", reg_id.get(i));
				cv.put("marker", Utility.getBytes(cm.getMarker()));
				cv.put("lati", latiList.get(i));
				cv.put("longi", longiList.get(i));
				db.insert("contacts", null, cv);
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				try{
					Bitmap defaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.man);
					byte[] n = Utility.getBytes(defaultIcon);
					ContentValues cv = new ContentValues();
					cv.put("image", n);
					cv.put("name", final_name.get(i));
					cv.put("phoneNumber", final_number.get(i));
					cv.put("reg_id", reg_id.get(i));
					cv.put("marker", defaultMarker);
					cv.put("lati", latiList.get(i));
					cv.put("longi", longiList.get(i));
					db.insert("contacts", null, cv);
					}catch(Exception ex){
						ex.printStackTrace();
					}
			}	
			}else{
				try{
				Bitmap defaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.man);
				byte[] n = Utility.getBytes(defaultIcon);
				ContentValues cv = new ContentValues();
				cv.put("image", n);
				cv.put("name", final_name.get(i));
				cv.put("phoneNumber", final_number.get(i));
				cv.put("reg_id", reg_id.get(i));
				cv.put("marker", defaultMarker);
				cv.put("lati", latiList.get(i));
				cv.put("longi", longiList.get(i));
				db.insert("contacts", null, cv);
				}catch(Exception e){
					e.printStackTrace();
				}

			}
			
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) 
		{
			pb.setProgress(Integer.parseInt(progress[0]));
			percent.setText(progress[0]+"%");
		}


		@Override
		protected void onPostExecute(String unused) 
		{

			db.close();
			
			

			Toast.makeText(getApplicationContext(),
					"Registration successfull", Toast.LENGTH_LONG)
					.show();

			Intent intent = new Intent(SecuritySMS.this,
					MakeProfile.class);
			intent.putExtra("list_flag", "not");
			startActivity(intent);
			finish();
		}
	}
	
	public Location getLocation(){
		
		
		Location location = locationClient.getLastLocation();
		
		return location;
	}
	
	public void loadNow(){
		ConnectionDetector cd = new ConnectionDetector(SecuritySMS.this);
		if(cd.isConnectingToInternet()){
			try{
			Location location = getLocation();
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			
			new RegisterAsyncTaskRunner().execute();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Your present location not found, try again.",Toast.LENGTH_LONG).show();
//			finish();
		
		}
			
		}else{
			Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
		}
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

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		
//		done.setVisibility(View.VISIBLE);
//		inactiveBtn.setVisibility(View.GONE);

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try {
			// Loading map
			// initilizeMap();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
