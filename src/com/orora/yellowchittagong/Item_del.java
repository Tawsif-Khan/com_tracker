package com.orora.yellowchittagong;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tawsif.finalproject.R;

public class Item_del extends Activity {
	
	ImageView logo;
	TextView name;
	Button info,ext_map;
	String nametitle;

	Button current_location,police_station,search,all,nearest;
	ImageButton home;
	String selected;
	 ListView lv;
	 String s,image;
	EditText inputSearch;
	ImageView imageviewing;
	// Listview Adapter
		ArrayAdapter<String> adapter;
		JSONParser jParser=new JSONParser();
		ArrayList<String> item=new ArrayList<String>();
		ArrayList<String> iditem=new ArrayList<String>();
		ArrayList<String> lat=new ArrayList<String>();
		ArrayList<String> lon=new ArrayList<String>();
		
		boolean statusOfGPS,statusOfInternet,statusOfNetwork;	
		Context context;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cat_details);
//		logo=(ImageView)findViewById(R.id.logo);
		
		name=(TextView)findViewById(R.id.item_name);
		info=(Button)findViewById(R.id.info);
		ext_map=(Button)findViewById(R.id.ext_map);
		imageviewing=(ImageView)findViewById(R.id.imageviewing);
		
		SharedPreferences settings= getSharedPreferences("myprefs", 0);
		nametitle= settings.getString("name","");
		selected= settings.getString("selected","");
		image=settings.getString("image", "");
		s= settings.getString("session","");
		int textL=image.length();
	String str = image.substring(3, textL);
		name.setText(nametitle);
		home = (ImageButton) findViewById(R.id.image_home);
		
		Picasso.with(this).load("http://www.appseden.net/yellowctg/yellowctg/"+str).
		into(imageviewing);
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
		home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Item_del.this,MainActivity.class);
				startActivity(intent);
				MainActivity.ac.finish();
				GetCategoryDetails.srch.finish();
				if(MainActivity.alld == 1)
				AllDetailsInfo.allDet.finish();
				else
				DetailsInfo.det.finish();
				finish();
				overridePendingTransition  (R.anim.in_from_left, R.anim.right_out_back);
				
			}
		});
		
		info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(Item_del.this,Info.class);
				 if(netConnection()){
					 startActivity(intent);
					 overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
					 }else{
						 Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
					 }
				
				
				
			}
		});
		
		ext_map.setOnClickListener(new OnClickListener() {
			int flag=0;
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				new AsyncRunner().execute();
				 
				 try {
			            // Loading map
						LocationManager manager = (LocationManager) getSystemService(context.LOCATION_SERVICE );
						statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
						statusOfNetwork=manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
						statusOfInternet=netConnection();
						
						if((statusOfGPS==false&&statusOfNetwork==false)||statusOfInternet==false)
						{
							LayoutInflater li = LayoutInflater.from(context);
							View promptsView;
							AlertDialog.Builder builder;
							final AlertDialog alertDialog;
							Button btnOk=null;
							if(statusOfGPS==false&&statusOfInternet==false&&statusOfNetwork==false)
							{
								promptsView = li.inflate(R.layout.int_loc_dialog, null);
								builder = new AlertDialog.Builder(context);
								builder.setView(promptsView);
								builder.setCancelable(false);
								alertDialog = builder.create();
								alertDialog.show();
								btnOk=(Button)promptsView.findViewById(R.id.btnOk);
							}
							else if(statusOfInternet==false&&(statusOfGPS==true||statusOfNetwork==true))
							{
								promptsView = li.inflate(R.layout.int_dialog, null);
								builder = new AlertDialog.Builder(context);
								builder.setView(promptsView);
								builder.setCancelable(false);
								alertDialog = builder.create();
								alertDialog.show();
								btnOk=(Button)promptsView.findViewById(R.id.btnOk);
							}
							else
							{
								promptsView = li.inflate(R.layout.loc_dialog, null);
								builder = new AlertDialog.Builder(context);
								builder.setView(promptsView);
								builder.setCancelable(false);
								alertDialog = builder.create();
								alertDialog.show();
								btnOk=(Button)promptsView.findViewById(R.id.btnOk);
							}
							
							btnOk.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub	    
									alertDialog.cancel();
								}
							});	
							
						}
					
						else
						{
							if(statusOfNetwork==false)
							{
								Toast.makeText(getApplicationContext(), "Please turn Network status of your device oN", Toast.LENGTH_LONG).show();
							}
							else
							{
								try
								{
									
									go();
									
								}
								catch(Exception e)
								{
									Toast.makeText(getApplicationContext(), "Sorry an error occured duting the execution", Toast.LENGTH_LONG).show();
								}
							}
						}
			 
			        } catch (Exception e) {
			            e.printStackTrace();
			            Toast.makeText(getApplicationContext(), "Internet status and GPS status of device can not be accesed",Toast.LENGTH_LONG).show();
			        }
			
				 
			}
		});
		
	
	}
	public void go(){
		 Intent intent = new Intent(Item_del.this,PositionWithDirection.class);
			
			startActivity(intent);
			overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
			
	}
	
public boolean netConnection(){
		
		ConnectivityManager cm =
		        (ConnectivityManager)Item_del.this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		
		return isConnected;
	}
	
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition  (R.anim.in_from_left, R.anim.right_out_back);
	}
	
	
	


		

}
