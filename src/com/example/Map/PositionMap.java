//package com.example.Map;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.app.Dialog;
//import android.app.IntentService;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Point;
//import android.location.Address;
//import android.location.Criteria;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.SystemClock;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.content.WakefulBroadcastReceiver;
//import android.util.Log;
//import android.view.animation.LinearInterpolator;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.Projection;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.tawsif.finalproject.R;
//
//public class PositionMap extends FragmentActivity implements LocationListener {
//	static GoogleMap googleMap;
//	Context context;
//	// double lati,longi;
//	ArrayList<Double> lati = new ArrayList<Double>();
//	ArrayList<Double> longi = new ArrayList<Double>();
//	ArrayList<String> current_reg_members = new ArrayList<String>();
//	LocationManager mLocationManager;
//	static Marker m = null;
//	GPSTracker gps;
//	double latitude, longitude;
//	static Double latiD = 22.3799272, longiD = 91.8543157;
//	String reg;
//	String group_name = "", group_id = "", group_title;
//	static String my_reg_id, my_name;
//	static ArrayList<String> haveMarker = new ArrayList<String>();
//	static ArrayList<Marker> markerArray = new ArrayList<Marker>();
//	Button gotomsg;
//	
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.center_in_map);
//		context = this;
//		googleMap = null;
//
//		
////		gotomsg = (Button)findViewById(R.id.message);
//		
//		group_name = getIntent().getExtras().getString("group_name");
//		group_id = getIntent().getExtras().getString("group_id");
//
//		
////		gotomsg.setOnClickListener(new OnClickListener() {
////			
////			@Override
////			public void onClick(View arg0) {
////				// TODO Auto-generated method stub
////				Intent intent = new Intent(context,SendTemp.class);
////				intent.putExtra("group_name", group_name);
////				intent.putExtra("group_id", group_id);
////				startActivity(intent);
////			}
////		});
//		
//
//		SharedPreferences settings = getSharedPreferences("prefs", 0);
//		my_reg_id = settings.getString("regId", "");
//		my_name = settings.getString("user_name", "");
//
//
//		SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE, null);
//
//		String qrg = "SELECT * FROM groups WHERE group_name='" + group_name
//				+ "';";
//		Cursor crg = db.rawQuery(qrg, null);
//		if (crg != null && crg.getCount() > 0) {
//			crg.moveToFirst();
//			do {
//				group_id = crg.getString(crg.getColumnIndex("id"));
//			} while (crg.moveToNext());
//		}
//
//		current_reg_members.add(my_reg_id);
//		
//		String qr = "SELECT * FROM members WHERE group_id='" + group_id + "';";
//		Cursor cr = db.rawQuery(qr, null);
//		if (cr != null && cr.getCount() > 0) {
//			 cr.moveToFirst();
//			 do {
//					
//			 String get_id = cr.getString(cr
//			 .getColumnIndex("reg_id"));
//			// Toast.makeText(getApplicationContext(), get_id, Toast.LENGTH_SHORT).show();
//				
//			 if(!get_id.equals(my_reg_id))
//			 current_reg_members.add(get_id);
//			 } while (cr.moveToNext());
//		}
//
//		// ///// On Map ////////////////////////
//
//		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//				1000, 0, mLocationListener);
//
//		gps = new GPSTracker(PositionMap.this);
//
//		// check if GPS enabled
//		if (gps.canGetLocation()) {
//			//Location loc = gps.getLocation();
//			
////			Criteria criteria = new Criteria();
////			String provider = mLocationManager.getBestProvider(criteria, false);
////			Location mlocation = mLocationManager
////					.getLastKnownLocation(provider);
////			
//			
//			latitude = gps.getLatitude();
//			longitude = gps.getLongitude();
//
//			// \n is for new line
//		} else {
//			// can't get location
//			// GPS or Network is not enabled
//			// Ask user to enable GPS/network in settings
//			gps.showSettingsAlert();
//		}
//
//		try {
//			// Loading map
//			initilizeMap();
//
//			LatLng loc = new LatLng(latitude, longitude);
//			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	private void initilizeMap() {
//		// googleMap=null;
//		int status = GooglePlayServicesUtil
//				.isGooglePlayServicesAvailable(getBaseContext());
//		// Showing status
//		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
//													// not available
//			int requestCode = 10;
//			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
//					requestCode);
//			dialog.show();
//		} else {
////			googleMap = ((SupportMapFragment) getSupportFragmentManager()
////					.findFragmentById(R.id.map)).getMap();
//			googleMap.getMapType();
//			if (googleMap == null) {
//				Toast.makeText(getApplicationContext(), "Unable to create map",
//						Toast.LENGTH_LONG).show();
//			} else {
//				googleMap.setMyLocationEnabled(true);
//				for (int i = 0; i < 1; i++) {
//					LatLng loc = new LatLng(latitude, longitude);
//					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,
//							15.0f));
//					m = googleMap.addMarker(new MarkerOptions().position(loc).title("My Location").snippet(Address(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location)));
//					markerArray.add(m);
//					m.setVisible(true);
//					haveMarker.add(my_reg_id);
//					 Toast.makeText(getApplicationContext(), m+"",
//					 Toast.LENGTH_SHORT).show();
//				}
//			}
//
//		}
//
//	}
//	
//	
//	
//	private final LocationListener mLocationListener = new LocationListener() {
//		@Override
//		public void onLocationChanged(final Location location) {
//			// your code here
//			// Toast.makeText(getApplicationContext(), "\nhey , got it",
//			// Toast.LENGTH_SHORT).show();
//
//			gps = new GPSTracker(PositionMap.this);
//			Criteria criteria = new Criteria();
//			String provider = mLocationManager.getBestProvider(criteria, false);
//			Location mlocation = mLocationManager
//					.getLastKnownLocation(provider);
//			String lat = String.valueOf(mlocation.getLatitude());
//			String longi = String.valueOf(mlocation.getLongitude());
//			PositionSender sender = new PositionSender();
//			sender.send(sender.createContent(current_reg_members, lat, longi,
//					my_reg_id));
//			Toast.makeText(getApplicationContext(), "notify", 1000);
//		}
//
//		@Override
//		public void onProviderDisabled(String arg0) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onProviderEnabled(String arg0) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//			// TODO Auto-generated method stub
//			// Toast.makeText(getApplicationContext(), arg0,
//			// Toast.LENGTH_SHORT).show();
////			String lat = String.valueOf(gps.getLatitude());
////			String longi = String.valueOf(gps.getLongitude());
////
////			PositionSender sender = new PositionSender();
////			sender.send(sender.createContent(current_reg_members, lat, longi,
////					my_reg_id));
//
//		}
//	};
//
//	public static class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			// Explicitly specify that GcmMessageHandler will handle the intent.
//			ComponentName comp = new ComponentName(context.getPackageName(),
//					GcmMessageHandler.class.getName());
//
//			// Start the service, keeping the device awake while it is
//			// launching.
//			startWakefulService(context, (intent.setComponent(comp)));
//			setResultCode(Activity.RESULT_OK);
//		}
//	};
//
//	public static class GcmMessageHandler extends IntentService {
//
//		String position, mes, text, RId, type, reg, group_name_rcv,
//				group_title, msg_time, msg_id;
//		ArrayList<String> reg_list = new ArrayList<String>();
//		String active;
//		
//		String latiS, longiS;
//		private Handler handler;
//
//		public GcmMessageHandler() {
//			super("GcmMessageHandler");
//		}
//
//		@Override
//		public void onCreate() {
//			// TODO Auto-generated method stub
//			super.onCreate();
//			handler = new Handler();
//		}
//
//		@Override
//		protected void onHandleIntent(Intent intent) {
//			Bundle extras = intent.getExtras();
//
//			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//			// The getMessageType() intent parameter must be the intent you
//			// received
//			// in your BroadcastReceiver.
//			String messageType = gcm.getMessageType(intent);
//
//			type = extras.getString("type");
//			RId = extras.getString("reg_id");
//			latiS = extras.getString("lati");
//			longiS = extras.getString("longi");
//
//			if (type.equals("location")) {
//				moveMarker();
//			}
//
//			Log.i("GCM",
//					"Received : (" + messageType + ")  "
//							+ extras.getString("title"));
//
//			GcmBroadcastReceiver.completeWakefulIntent(intent);
//
//		}
//
//		public void moveMarker() {
//			handler.post(new Runnable() {
//				public void run() {
//					// Toast.makeText(getApplicationContext(),
//					// latiS+"\n"+longiS, Toast.LENGTH_SHORT).show();
//					int flag=-1;
//					currentRunningActivity();
//					if (active.equals("com.example.Map.PositionMap")) {
//						latiD = Double.valueOf(latiS);
//						longiD = Double.valueOf(longiS);
//						
//						LatLng loc = new LatLng(latiD, longiD);
//						
//						for(int i=0;i<haveMarker.size();i++){
//							Toast.makeText(getApplicationContext(), flag+"", Toast.LENGTH_SHORT).show();
//							
//							if(haveMarker.get(i).equals(RId)){
//								flag= i;
//								
//								break;
//							}
//						}
//						if(flag > -1){
//							Toast.makeText(getApplicationContext(), markerArray.get(flag)+"", Toast.LENGTH_SHORT).show();
//							
//							animateMarker(markerArray.get(flag), loc, false);
//						}else{
//							Toast.makeText(getApplicationContext(), "got new Member", Toast.LENGTH_SHORT).show();
//							googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,
//									15.0f));
//							Marker new_marker = googleMap.addMarker(new MarkerOptions().position(loc));
//							new_marker.setIcon(BitmapDescriptorFactory.defaultMarker());
//							markerArray.add(new_marker);
//							new_marker.setVisible(true);
//							haveMarker.add(RId);
//						}
//					}//else
////						Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_SHORT).show();
//					
//				}
//			});
//		}
//
//		public void animateMarker(final Marker marker, final LatLng toPosition,
//				final boolean hideMarker) {
//			final Handler handlers = new Handler();
//			final long start = SystemClock.uptimeMillis();
//			Projection proj = googleMap.getProjection();
//			Point startPoint = proj.toScreenLocation(marker.getPosition());
//			final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//			final long duration = 500;
//
//			final LinearInterpolator interpolator = new LinearInterpolator();
//
//			handlers.post(new Runnable() {
//				@Override
//				public void run() {
//					long elapsed = SystemClock.uptimeMillis() - start;
//					float t = interpolator.getInterpolation((float) elapsed
//							/ duration);
//					double lng = t * toPosition.longitude + (1 - t)
//							* startLatLng.longitude;
//					double lat = t * toPosition.latitude + (1 - t)
//							* startLatLng.latitude;
//					marker.setPosition(new LatLng(lat, lng));
//
//					if (t < 1.0) {
//						// Post again 16ms later.
//						handlers.postDelayed(this, 16);
//					} else {
//						if (hideMarker) {
//							marker.setVisible(false);
//						} else {
//							marker.setVisible(true);
//						}
//					}
//				}
//			});
//		}
//
//		public void currentRunningActivity() {
//			ActivityManager am = (ActivityManager) this
//					.getSystemService(ACTIVITY_SERVICE);
//
//			// get the info from the currently running task
//			List<ActivityManager.RunningTaskInfo> taskInfo = am
//					.getRunningTasks(1);
//
//			Log.d("topActivity", "CURRENT Activity ::"
//					+ taskInfo.get(0).topActivity.getClassName());
//
//			ComponentName componentInfo = taskInfo.get(0).topActivity;
//			active = componentInfo.getClassName();
//		}
//
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		try {
//			// Loading map
//			// initilizeMap();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//		// 30000, 3, this);
//	}
//
//	@Override
//	public void onBackPressed() {
//		googleMap = null;
//		finish();
//	}
//
//	@Override
//	public void onLocationChanged(Location location) {
//		// TODO Auto-generated method stub
//		// String lat = String.valueOf(gps.getLatitude());
//		// String longi = String.valueOf(gps.getLongitude());
//		// PositionSender sender = new PositionSender();
//		// sender.send(sender.createContent(current_reg_members,lat,longi));
//		//
//		// Toast.makeText(getApplicationContext(),
//		// gps.getLatitude()+"\nhey , got it", Toast.LENGTH_SHORT).show();
//
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//
//	}
//	
//	public String Address(double lat, double lon)
//	{
//		String add = "";
//		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
//		try 
//		{
//			List<Address> addresses = geoCoder.getFromLocation(lat, lon, 1);
//			if (addresses.size() > 0) 
//			{
//				for (int i = 0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
//				{
//					add += addresses.get(0).getAddressLine(i) + "\n";
//				}
//			}
//		}
//		catch (IOException e) 
//		{                
//			e.printStackTrace();
//		}
//		return add;   
//	
//	}
//}
