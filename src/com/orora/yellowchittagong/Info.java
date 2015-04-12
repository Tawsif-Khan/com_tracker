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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class Info extends Activity{
	
	TextView name,address,call,phone_no,email,longitude,latitude,website,pro_service,description;
	String phone_no1;
	ImageButton home;
	Button h_button;
	String longitude1, latitude1;
	String app_id;
	JSONParser jParser=new JSONParser();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		home= (ImageButton) findViewById(R.id.image_home); 
		call = (TextView) findViewById(R.id.call);
		
		home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(Info.this,MainActivity.class);
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
	
	
//		h_button= (Button) findViewById(R.id.home); 
//		
//		h_button.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				 Intent intent = new Intent(Info.this,MainActivity.class);
//				 if(netConnection()){
//					 startActivity(intent);
//					 overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
//					 }else{
//						 Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
//					 }
//			}
//		});
		
		name=(TextView)findViewById(R.id.name);
		address=(TextView)findViewById(R.id.address);
		phone_no=(TextView)findViewById(R.id.phone);
		email=(TextView)findViewById(R.id.email);
		longitude=(TextView)findViewById(R.id.longitude1);
		latitude=(TextView)findViewById(R.id.latitude);
		website=(TextView)findViewById(R.id.website);
		pro_service=(TextView)findViewById(R.id.pro_service);
		description=(TextView)findViewById(R.id.description);
		SharedPreferences settings= getSharedPreferences("myprefs", 0);
		
		app_id= settings.getString("app_id","");

		Toast.makeText(getApplicationContext(), app_id, Toast.LENGTH_SHORT).show();
		new AsyncRunner().execute();
		
		call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alert();
			}
		});
		
website.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(Info.this,Webview.class);
				if(netConnection()){
					 startActivity(intent);
					 overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
					 }else{
						 Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
					 }
			}
		});
		
		
	}
	private void performDial(String numberString) {
	    if (!numberString.equals("")) {
	       Uri number = Uri.parse("tel:" + numberString);
	       Intent dial = new Intent(Intent.ACTION_CALL, number);
	       startActivity(dial);
	    }
	}
	
	public void alert(){
		
		AlertDialog show = new AlertDialog.Builder(Info.this).setTitle("Call")
			    .setMessage("Do u want to call?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
						
			        	performDial(phone_no1);
			        	
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			    .setIcon(android.R.drawable.ic_dialog_alert)
			     .show();
		
	}
	
public boolean netConnection(){
		
		ConnectivityManager cm =
		        (ConnectivityManager)Info.this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
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
	
	
	class AsyncRunner extends AsyncTask<String,String,String>
	{String res="";
	JSONObject json;
	private ProgressDialog pDialog;
	
	@Override
	
	protected String doInBackground(String...args){
		
		List <NameValuePair> params=new ArrayList<NameValuePair>();
	
	params.add(new BasicNameValuePair("app_id", app_id));
			
	json=jParser.makeHttpRequest("http://www.appseden.net/yellowctg/yellowctg/JSON/searchingdetails.php", "POST", params);
	
	
	
	return res;
	}
	
	protected void onPreExecute(){
		super.onPreExecute();
		
		pDialog=new ProgressDialog (Info.this);
		pDialog.setMessage("Please wait..");
		
		pDialog.show();
		
	}
	
	protected void onPostExecute(String file_url){
		
		pDialog.dismiss();
		//view.setText(res);
		
		try{
			int success=json.getInt("success");
			JSONArray products= json.getJSONArray("product");
					
					//Log.d("Length Of Product Array")

				for(int i=0;i<products.length();i++)
				{
					
					JSONObject c=products.getJSONObject(i);
				
					String name1=c.getString("name");
					String address1=c.getString("address");
					phone_no1=c.getString("contact");
					String email1=c.getString("email");
					longitude1=c.getString("longitude");
					latitude1=c.getString("latitude");
					String website1=c.getString("website");
					String prod_service1=c.getString("prod_service");
					String description1=c.getString("description");
					
					SharedPreferences settings= getSharedPreferences("myprefs", 0);
					SharedPreferences.Editor editor= settings.edit();
					editor.putString("website1", website1);
					editor.commit();
					
					name.setText(name1);
					address.setText(address1);
					phone_no.setText(phone_no1);
					email.setText(email1);
					website.setText(website1);
					longitude.setText(longitude1);
					latitude.setText(latitude1);
					pro_service.setText(prod_service1);
					description.setText(description1);
				}
				//Toast.makeText(getApplicationContext(), ""+longitude, Toast.LENGTH_LONG).show();

		
			}
			catch(JSONException e )
			{
				e.printStackTrace();
			} 
}
}
}
