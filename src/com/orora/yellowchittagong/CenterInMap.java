package com.orora.yellowchittagong;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tawsif.finalproject.R;
import com.tawsif.finalproject.R;


public class CenterInMap extends FragmentActivity{
	private GoogleMap googleMap;
	Context context;
	double lat,lon;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.center_in_map_yc);
		
		context=this;
		googleMap=null;
//		Intent in=getIntent();
//		String xx=in.getExtras().getString("lat");
//		String yy=in.getExtras().getString("lon");
		lat=22.213361;
		lon=91.49276;
		
	//lat=22.33826;
		//lon=91.8117;
		
        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	
	
	private void initilizeMap() { 
		googleMap=null;
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        else
        {
        	googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        	if(googleMap==null)
        	{
        	     Toast.makeText(getApplicationContext(), "Unable to create map",Toast.LENGTH_LONG).show();
        	}
        	else
        	{
        		googleMap.setMyLocationEnabled(true);
        		LatLng loc=new LatLng(lat,lon);
        		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
        		Marker m =googleMap.addMarker(new MarkerOptions().position(loc));
				m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.center));
				m.setVisible(true);
    			
        	}
        	
        }
            
	}
	
	
    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
       // locationManager.requestLocationUpdates(provider, 30000, 3, this);
    } 
    
	@Override
	public void onBackPressed()
	{
		googleMap=null;
		finish();
	}
	

}
