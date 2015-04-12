package com.example.Map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.finalproject.Send;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nearby.welcome.ReloadData;
import com.tawsif.finalproject.R;

public class PositionMapFragment extends SherlockFragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	static GoogleMap googleMap;
	// Context context;
	// double lati,longi;
	ArrayList<Double> lati = new ArrayList<Double>();
	ArrayList<Double> longi = new ArrayList<Double>();
	public static ArrayList<String> current_reg_members = new ArrayList<String>();
	LocationManager mLocationManager;
	static Marker m = null;
	GPSTracker gps;
	public static double latitude, longitude;
	static Double latiD = 0.0, longiD = 0.0;
	String reg;
	String group_name = "", group_id = "", group_title;
	static String my_reg_id, my_number;

	public static ArrayList<String> image_path = new ArrayList<String>();
	public static ArrayList<Bitmap> markerImage = new ArrayList<Bitmap>();
	public static ArrayList<String> groupMemberNumberList = new ArrayList<String>();
	public static HashMap<String, Marker> markershash = new HashMap<String, Marker>();
	public static HashMap<String, Float> distanceHash = new HashMap<String, Float>();
	
	Button chat;
	private LocationClient locationClient;
	static View rootView;
	private Context context = null;
	public static Button chatbtn;
	Switch sw;
	Bundle bundle;
	int markerFlag = 0;
	float[] results = new float[1];
	float distance;
	TextView title;
	
	public static CustomDistanceListAdapter distanceListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { // super.onCreate(savedInstanceState);
											// requestWindowFeature(Window.FEATURE_NO_TITLE);

		rootView = inflater.inflate(R.layout.center_in_map, container, false);
		context = container.getContext();
		this.bundle = savedInstanceState;

		googleMap = null;
		groupMemberNumberList.clear();
		my_reg_id = "";

		group_name = Send.group_name;
		group_id = Send.group_id;

		chatbtn = (Button) rootView.findViewById(R.id.chatbtn);
		sw = (Switch) rootView.findViewById(R.id.switch1);
		title = (TextView)rootView.findViewById(R.id.title);
		
		title.setText(Send.group_title);
		
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked)
					Toast.makeText(context, "Location Sharing Allowed", Toast.LENGTH_LONG)
							.show();
			}
		});

		chatbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Send.mDrawerLayout.openDrawer(Send.mdrawer);
			}
		});

		SharedPreferences settings = getActivity().getSharedPreferences(
				"prefs", 0);
		my_reg_id = settings.getString("regId", "");
		my_number = settings.getString("myNumber", "");

		SQLiteDatabase db = getActivity().openOrCreateDatabase("tracker", 0,
				null);

		String qrg = "SELECT * FROM groups WHERE group_name='" + group_name
				+ "';";
		Cursor crg = db.rawQuery(qrg, null);
		if (crg != null && crg.getCount() > 0) {
			crg.moveToFirst();
			do {
				group_id = crg.getString(crg.getColumnIndex("id"));
			} while (crg.moveToNext());
		}

		current_reg_members.add(my_reg_id);
		groupMemberNumberList.add(my_number);
		String qr = "SELECT * FROM members WHERE group_id='" + group_id + "';";
		Cursor cr = db.rawQuery(qr, null);
		if (cr != null && cr.getCount() > 0) {
			cr.moveToFirst();
			do {

				String get_id = cr.getString(cr.getColumnIndex("reg_id"));
				String memberNumber = cr.getString(cr
						.getColumnIndex("member_number"));

				if (!get_id.equals(my_reg_id)) {
					current_reg_members.add(get_id);
					groupMemberNumberList.add(memberNumber);
				}
			} while (cr.moveToNext());
		}

		mLocationManager = (LocationManager) getActivity().getSystemService(
				"location");

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, this);

		gps = new GPSTracker(context);

		// check if GPS enabled
		if (gps.canGetLocation()) {

		} else {

			gps.showSettingsAlert();
		}

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootView;
	}

	private void initilizeMap() {
		// googleMap=null;
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity().getBaseContext());
		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
					getActivity(), requestCode);
			dialog.show();
		} else {
			googleMap = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			googleMap.getMapType();
			if (googleMap == null) {
				Toast.makeText(context, "Unable to create map",
						Toast.LENGTH_LONG).show();
			} else {
				googleMap.setMyLocationEnabled(true);

				locationClient = new LocationClient(context, this, this);

			}
		}

	}

	@Override
	public void onLocationChanged(final Location location) {
		// your code here

		Criteria criteria = new Criteria();
		String provider = mLocationManager.getBestProvider(criteria, false);
		Location mlocation = mLocationManager.getLastKnownLocation(provider);

		sendLocation(mlocation.getLatitude(), mlocation.getLongitude());
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
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public void onConnected(Bundle arg0) {

		Location currentLocation = locationClient.getLastLocation();

		try {
			latitude = currentLocation.getLatitude();
			longitude = currentLocation.getLongitude();

			sendLocation(latitude, longitude);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Your present location not found, try again.",
					Toast.LENGTH_LONG).show();
			// finish();

		}

		if (latitude == 0 || longitude == 0) {
			Toast.makeText(context,
					"Your present location not found, try again.",
					Toast.LENGTH_LONG).show();
			// finish();
		} else {
			if (googleMap == null) {
				Toast.makeText(context, "Map is not created", Toast.LENGTH_LONG)
						.show();
			} else {
				if (markerFlag == 0) {

					markerFlag = 1;
					LatLng loc = new LatLng(latitude, longitude);
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,
							15.0f));

					m = googleMap.addMarker(new MarkerOptions().position(loc)
							.title("My Location")
							.snippet(Address(latitude, longitude))); // .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
																		// //
																		// BitmapFactory.decodeFile(image_path.get(0))
					m.setIcon(BitmapDescriptorFactory
							.fromBitmap(ReloadData.profilePicture));
					markershash.put(my_number, m);
					m.setVisible(true);
					for (int i = 0; i < groupMemberNumberList.size(); i++) {
						if (!groupMemberNumberList.get(i).equals(my_number)) {
							Double lat = Double.parseDouble(ReloadData.latiHash.get(groupMemberNumberList.get(i)));
							Double lon = Double.parseDouble(ReloadData.longiHash.get(groupMemberNumberList.get(i)));
							loc = new LatLng(lat,lon);
							Marker m1 = null;
							if(ReloadData.userName.get(groupMemberNumberList.get(i))==null){
								m1 = googleMap.addMarker(new MarkerOptions()
								.position(loc)
								.title(groupMemberNumberList.get(i))
								.snippet(Address(lat, lon)));
								m1.setIcon(BitmapDescriptorFactory
								.fromBitmap(ReloadData.defaultMarker));	
							}else{
							m1 = googleMap.addMarker(new MarkerOptions()
									.position(loc)
									.title(ReloadData.userName
											.get(groupMemberNumberList.get(i)))
									.snippet(Address(lat, lon)));
							m1.setIcon(BitmapDescriptorFactory
									.fromBitmap(ReloadData.userMarker
											.get(groupMemberNumberList.get(i))));
							}
							m1.setVisible(true);
							markershash.put(groupMemberNumberList.get(i), m1);
							
							Location.distanceBetween(latitude,longitude,lat, lon, results);
						    distance = results[0]/1000;
						    
						    distanceHash.put(groupMemberNumberList.get(i), distance);
						    //Toast.makeText(context, distance+"", Toast.LENGTH_LONG).show();
						}
					}
					
					distanceListAdapter = new CustomDistanceListAdapter(Send.cont, groupMemberNumberList, distanceHash);
				    Send.distanceList.setAdapter(distanceListAdapter);
				}
			}
		}
	}

	public void sendLocation(Double latitude, Double longitude) {
		if (sw.isChecked()) {
//			Toast.makeText(context, "location sent", 1000).show();
//			if(gps.canGetLocation()){
//			GPSTracker gps = new GPSTracker(context);
//			Location location = gps.getLocation();
//			this.latitude = location.getLatitude();
//			this.longitude = location.getLongitude();
//			}
			PositionSender sender = new PositionSender();
			sender.send(sender.createContent("location",current_reg_members,
					latitude + "", longitude + "", my_reg_id, my_number));

		}
	}

	public static class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// Explicitly specify that GcmMessageHandler will handle the intent.
			ComponentName comp = new ComponentName(context.getPackageName(),
					GcmMessageHandler.class.getName());

			// Start the service, keeping the device awake while it is
			// launching.
			startWakefulService(context, (intent.setComponent(comp)));
			setResultCode(Activity.RESULT_OK);
		}
	};

	public static class GcmMessageHandler extends IntentService {

		String position, mes, text, RId, type, reg, group_name_rcv,
				group_title, msg_time, msg_id, senderNumber;
		ArrayList<String> reg_list = new ArrayList<String>();
		String active;
		
		float[] results = new float[1];
		float distance;
		
		String latiS, longiS;
		private Handler handler;

		public GcmMessageHandler() {
			super("GcmMessageHandler");
		}

		@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			super.onCreate();
			handler = new Handler();
		}

		@Override
		protected void onHandleIntent(Intent intent) {
			Bundle extras = intent.getExtras();

			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
			// The getMessageType() intent parameter must be the intent you
			// received
			// in your BroadcastReceiver.
			String messageType = gcm.getMessageType(intent);

			type = extras.getString("type");
			RId = extras.getString("reg_id");
			latiS = extras.getString("lati");
			longiS = extras.getString("longi");
			senderNumber = extras.getString("number");

			if (type.equals("location")) {
				moveMarker();
			}

			Log.i("GCM",
					"Received : (" + messageType + ")  "
							+ extras.getString("title"));

			GcmBroadcastReceiver.completeWakefulIntent(intent);

		}

		public void moveMarker() {
			handler.post(new Runnable() {
				public void run() {

					int flag = -1;
					currentRunningActivity();
					if (active.equals("com.example.finalproject.Send")) {
						latiD = Double.valueOf(latiS);
						longiD = Double.valueOf(longiS);

						LatLng loc = new LatLng(latiD, longiD);

						String q = "UPDATE contacts SET lati='"+latiS+"',longi='"+longiS+"' WHERE phoneNumber='"+senderNumber+"';";			
						SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE, null);
						db.execSQL(q);
						db.close();
						

						ReloadData.latiHash.put(senderNumber, latiS);
						ReloadData.longiHash.put(senderNumber, longiS);
						
						Location.distanceBetween(latitude,longitude,latiD, longiD, results);
					    distance = results[0]/1000;
						distanceHash.put(senderNumber, distance);
						
						markershash.get(senderNumber).setSnippet(distance+"");
//						distanceListAdapter.notifyDataSetChanged();
						
						animateMarker(markershash.get(senderNumber), loc, false);

					}

				}
			});
		}

		public void animateMarker(final Marker marker, final LatLng toPosition,
				final boolean hideMarker) {
			final Handler handlers = new Handler();
			final long start = SystemClock.uptimeMillis();
			Projection proj = googleMap.getProjection();
			Point startPoint = proj.toScreenLocation(marker.getPosition());
			final LatLng startLatLng = proj.fromScreenLocation(startPoint);
			final long duration = 500;

			final LinearInterpolator interpolator = new LinearInterpolator();

			handlers.post(new Runnable() {
				@Override
				public void run() {
					long elapsed = SystemClock.uptimeMillis() - start;
					float t = interpolator.getInterpolation((float) elapsed
							/ duration);
					double lng = t * toPosition.longitude + (1 - t)
							* startLatLng.longitude;
					double lat = t * toPosition.latitude + (1 - t)
							* startLatLng.latitude;
					marker.setPosition(new LatLng(lat, lng));

					if (t < 1.0) {
						// Post again 16ms later.
						handlers.postDelayed(this, 16);
					} else {
						if (hideMarker) {
							marker.setVisible(false);
						} else {
							marker.setVisible(true);
						}
					}
				}
			});
		}

		public void currentRunningActivity() {
			ActivityManager am = (ActivityManager) this
					.getSystemService(ACTIVITY_SERVICE);

			// get the info from the currently running task
			List<ActivityManager.RunningTaskInfo> taskInfo = am
					.getRunningTasks(1);

			Log.d("topActivity", "CURRENT Activity ::"
					+ taskInfo.get(0).topActivity.getClassName());

			ComponentName componentInfo = taskInfo.get(0).topActivity;
			active = componentInfo.getClassName();
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		locationClient.connect();

	}

	public void onBackPressed() {
		//googleMap = null;
		Toast.makeText(context, "out now", Toast.LENGTH_SHORT).show();
		getActivity().finish();
	}

	public String Address(double lat, double lon) {
		String add = "";
		Geocoder geoCoder = new Geocoder(getActivity().getBaseContext(),
				Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(lat, lon, 1);
			if (addresses.size() > 0) {
				for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
					add += addresses.get(0).getAddressLine(i) + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return add;

	}

}
