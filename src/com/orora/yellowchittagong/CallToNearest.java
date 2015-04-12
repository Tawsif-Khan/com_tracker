package com.orora.yellowchittagong;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tawsif.finalproject.R;
public class CallToNearest extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{
	
	JSONParser jParser=new JSONParser();
	ArrayList<String> lat, lon,name,center_id,contact;
	ProgressDialog dialog;

	private LocationClient locationClient;
	private GoogleMap googleMap;
	Context context;
	boolean draw;
	
	Marker m1,m2;
	float[] results = new float[1];
	float distance;
	float minDistance;
	int nearest_id = -1;
	double lat1,lon1;
	double nearest_lat,nearest_lon;
	String n_cont;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nearest_center);
		dialog = new ProgressDialog(CallToNearest.this);
			dialog.setMessage("Calling...");
		
		dialog.show();
		
		center_id=(ArrayList<String>) getIntent().getSerializableExtra("center_id");
		name=(ArrayList<String>) getIntent().getSerializableExtra("center_name");
		lat=(ArrayList<String>) getIntent().getSerializableExtra("center_lat");
		lon=(ArrayList<String>) getIntent().getSerializableExtra("center_lon");
		contact = (ArrayList<String>) getIntent().getSerializableExtra("contact");

		
		if (isMapAvailable())
		{
			context=this;
			googleMap=null;
			lat1=0;
			lon1=0;
			nearest_lat=0;
			nearest_lon=0;
			m1=null;
			m2=null;
			draw=false;
			
	        try {
	            // Loading map
	    		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    		
	    		if(googleMap==null)
	    		{
	    			Toast.makeText(getApplicationContext(), "Unable to create map",Toast.LENGTH_LONG).show();
	    		}
	    		else
	    		{
	    			
//	    			googleMap.setMyLocationEnabled(true);	    			
	       			locationClient = new LocationClient(this, this, this);
	    		}
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        	Toast.makeText(getApplicationContext(), e+"", Toast.LENGTH_LONG).show();
	        }
	        
	 
			
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Map is not available in your device",Toast.LENGTH_LONG).show();
			finish();
		}   
		
		
	}
	
	
	
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationClient.connect();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationClient.disconnect();
	}
	
	@Override
	public void onBackPressed()
	{
		googleMap=null;
		finish();		
	}
	
	public boolean isMapAvailable() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// resultCode=ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;
		if (ConnectionResult.SUCCESS == resultCode) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
			// Service Missing
			// Service needs update
			// Service disabled
			Dialog d = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					1);
			d.show();

		} else {
			Toast.makeText(getApplicationContext(), "Google Maps API V2 is not supported in your device!",Toast.LENGTH_LONG).show();
			finish();
		}

		return false;
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
		// TODO Auto-generated method stub
		
		Location currentLocation = locationClient.getLastLocation();
		
		try{
			lat1 = currentLocation.getLatitude();
			lon1 = currentLocation.getLongitude();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Your present location not found, try again.",Toast.LENGTH_LONG).show();
			finish();
			
		}
		
		if(lat1==0||lon1==0)
		{
			Toast.makeText(getApplicationContext(), "Your present location not found, try again.",Toast.LENGTH_LONG).show();
			finish();
		}
		else
		{  	
			if(googleMap==null)
			{
				Toast.makeText(getApplicationContext(),"Map is not created",Toast.LENGTH_LONG).show();
			}
			else
			{
				LatLng loc1 = new LatLng(lat1, lon1);
//				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc1, 14.0f));
//				 m1 =googleMap.addMarker(new MarkerOptions().position(loc1).title("Your current location").snippet("Click for direction"));
////				 m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.i_m_here));
//				float m=(float)0.5;
//				float n=(float)0.3;
//				m1.setAnchor(m, n);
				minDistance=100000000;
				for(int i=0;i<lat.size();i++)
				{
					String xx=lat.get(i);
					String yy=lon.get(i);
					String iidd = center_id.get(i);
					
					double lat2=Double.valueOf(xx);
					double lon2=Double.valueOf(yy);
				
					Location.distanceBetween(lat1,lon1,lat2, lon2, results);
				    distance=results[0]/1000;
				    if(distance < minDistance)
				    {
				    	minDistance=distance;
				    	nearest_lat=lat2;
				    	nearest_lon=lon2;
				    	n_cont = contact.get(i);
				    	nearest_id=i;
				    }
				}
				
				
//				LatLng loc2=new LatLng(nearest_lat,nearest_lon);
//       		    m2 =googleMap.addMarker(new MarkerOptions().position(loc2).title(name.get(nearest_id)+"\nDistance : "+minDistance+" km").snippet("Click to see detail"));
//			    m2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.center));
//				m2.showInfoWindow();   
				
			//	m1.showInfoWindow();
			}
			dialog.dismiss();
			performDial(n_cont);
			finish();
		}
		
	}
	
	private void performDial(String numberString) {
		if (!numberString.equals("")) {
			Uri number = Uri.parse("tel:" + numberString);
			Intent dial = new Intent(Intent.ACTION_CALL, number);
			startActivity(dial);
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	
	 private class DownloadTask extends AsyncTask<String, Void, String>{

	        // Downloading data in non-ui thread
	        @Override
	        protected String doInBackground(String... url) {

	            // For storing data from web service
	            String data = "";

	            try{
	                // Fetching the data from web service
	                data = downloadUrl(url[0]);
	            }catch(Exception e){
	                Log.d("Background Task",e.toString());
	            }
	            return data;
	        }

	        // Executes in UI thread, after the execution of
	        // doInBackground()
	        @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);

	            ParserTask parserTask = new ParserTask();

	            // Invokes the thread for parsing the JSON data
	            parserTask.execute(result);
	        }
	    }
	    
	    
	    /** A class to parse the Google Places in JSON format */
	    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

	        // Parsing the data in non-ui thread
	        @Override
	        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

	            JSONObject jObject;
	            List<List<HashMap<String, String>>> routes = null;

	            try{
	                jObject = new JSONObject(jsonData[0]);
	                DirectionsJSONParser parser = new DirectionsJSONParser();

	                // Starts parsing data
	                routes = parser.parse(jObject);
	            }catch(Exception e){
	                e.printStackTrace();
	            }
	            return routes;
	        }

	        // Executes in UI thread, after the parsing process
	        @Override
	        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
	            ArrayList<LatLng> points = null;
	            PolylineOptions lineOptions = null;
	            MarkerOptions markerOptions = new MarkerOptions();

	            // Traversing through all the routes
	            for(int i=0;i<result.size();i++){
	                points = new ArrayList<LatLng>();
	                lineOptions = new PolylineOptions();

	                // Fetching i-th route
	                List<HashMap<String, String>> path = result.get(i);

	                // Fetching all the points in i-th route
	                for(int j=0;j<path.size();j++){
	                    HashMap<String,String> point = path.get(j);

	                    double lat = Double.parseDouble(point.get("lat"));
	                    double lng = Double.parseDouble(point.get("lng"));
	                    LatLng position = new LatLng(lat, lng);

	                    points.add(position);
	                }

	                // Adding all the points in the route to LineOptions
	                lineOptions.addAll(points);
	                lineOptions.width(8);
	                lineOptions.color(Color.DKGRAY);
	            }

	            // Drawing polyline in the Google Map for the i-th route
	            googleMap.addPolyline(lineOptions);
	        }

	    }
	
	
	

	

}
