package com.orora.yellowchittagong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class DetailsInfo extends Activity implements OnItemSelectedListener{
	
	ProgressBar prog;
	LinearLayout lid;
	 TextView category,load;
     String category1;
     ImageButton home;
     Button h_button;
		//Button current_location,police_station,search,all,nearest;
	String selecteditem;
	 ListView lv;
	 String s;
	EditText inputSearch;
	// Listview Adapter
		ArrayAdapter<String> adapter;
		JSONParser jParser=new JSONParser();
		ArrayList<String> item=new ArrayList<String>();
		ArrayList<String> iditem=new ArrayList<String>();
		ArrayList<String> lat=new ArrayList<String>();
		ArrayList<String> lon=new ArrayList<String>();
		ArrayList<String> imagelist=new ArrayList<String>();
		
		public static Activity det;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		det = this;
		MainActivity.det = 1;
		
		lv = (ListView) findViewById(R.id.list_view);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		category=(TextView) findViewById(R.id.category);
		load=(TextView) findViewById(R.id.loading);
//		lid = (LinearLayout)findViewById(R.id.display);
		prog= (ProgressBar) findViewById(R.id.progressBar1);
		prog.setVisibility(View.GONE);
		load.setVisibility(View.GONE);
		
		home= (ImageButton) findViewById(R.id.image_home); 
	
		home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(DetailsInfo.this,MainActivity.class);
					startActivity(intent);
					MainActivity.ac.finish();
					GetCategoryDetails.srch.finish();
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
//				 Intent intent = new Intent(DetailsInfo.this,MainActivity.class);
//					startActivity(intent);
//					overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
//					
//			}
//		});
	
		
		SharedPreferences setting= getSharedPreferences("myprefs", 0);
		category1= setting.getString("session","");

		String category1_name = setting.getString("session_name", "");
		category.setText(category1_name);
		
	
		SharedPreferences settings= getSharedPreferences("myprefs", 0);
	
		s= settings.getString("session","");
		//put recive shre preference
		
		
		
	       Spinner spinner = (Spinner) findViewById(R.id.spinner);
	        
	        // Spinner click listener
	        spinner.setOnItemSelectedListener(this);
	     
		ArrayList<String> detail = new ArrayList<String>();
		
		
	
		
		adapter=new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name,item);
		lv.setAdapter(adapter);

    
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("SELECT");
        categories.add("Boddarhat");
        categories.add("Muradpur");
        categories.add("2 no.Gate");
        categories.add("G.E.C");
        categories.add("O.R.Nizam Road");
        categories.add("Dampara");
        categories.add("Wasa Moor");
        categories.add("Lalkhan Bazar");
        categories.add("Probottok moor");
        categories.add("Chawkbazar");
        categories.add("Tigerpass");
        categories.add("DewanHaat");
        categories.add("Agrabad");
        categories.add("Halishahar");
        categories.add("Panchlaish");
        categories.add("Sugondha");
        categories.add("Chatteswari Road");
        categories.add("Khulshi");
        categories.add("Andorkilla");
        categories.add("Chandanpura");
        categories.add("Jamalkhan");
        categories.add("Kotowali");
        categories.add("Laldighi");
        categories.add("K.B.Fazlul Kader Road");
        categories.add("Ice Factory Road");
        categories.add("Station road");
        categories.add("Goal Pahar Mor");
        // Creating adapter for spinner
        
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// attaching data adapter to spinner
		spinner.setAdapter(dataAdapter);

		

		
		//text.setText(s);
		
	}
	
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition  (R.anim.in_from_left, R.anim.right_out_back);
	}
	
    @Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// On selecting a spinner item
		selecteditem = parent.getItemAtPosition(position).toString();
		
		// Showing selected spinner item
		Toast.makeText(parent.getContext(), "Selected: " + selecteditem, Toast.LENGTH_LONG).show();
		//Intent intent=new Intent(DetailsInfo.this,DetailsInfo.class);
		if(selecteditem.endsWith("SELECT"))
			Toast.makeText(getApplicationContext(), "Select " , Toast.LENGTH_LONG).show();
		else
			new AsyncRunner().execute();
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
public boolean netConnection(){
		
		ConnectivityManager cm =
		        (ConnectivityManager)DetailsInfo.this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		
		return isConnected;
	}
	


	class AsyncRunner extends AsyncTask<String,String,String>
	{
		String res="";
	JSONObject json;
	private ProgressDialog pDialog;
	
	@Override
	
	protected String doInBackground(String...args){
		
		List <NameValuePair> params=new ArrayList<NameValuePair>();
		SharedPreferences settings= getSharedPreferences("myprefs", 0);
		SharedPreferences.Editor editor= settings.edit();
		
		editor.putString("selected", selecteditem);
		editor.commit();
	params.add(new BasicNameValuePair("location", selecteditem));
	params.add(new BasicNameValuePair("catagory", s));
			
	json=jParser.makeHttpRequest("http://www.appseden.net/yellowctg/yellowctg/JSON/searchingwithitem.php", "POST", params);
	
	
	
	return res;
	}
	
	protected void onPreExecute(){
		super.onPreExecute();
		
//		pDialog=new ProgressDialog (DetailsInfo.this);
//		pDialog.setMessage("Please wait..");
//		pDialog.show();
		lv.setVisibility(View.GONE);
		prog.setVisibility(View.VISIBLE);
		load.setVisibility(View.VISIBLE);
//		Toast.makeText(getApplicationContext(), "got", Toast.LENGTH_LONG).show();
		
	}
	
	

	protected void onPostExecute(String file_url){
		
//		pDialog.dismiss();
		//view.setText(res);
		Toast.makeText(getApplicationContext(), "here", Toast.LENGTH_LONG).show();
		try{

			item.clear();
			iditem.clear();
			imagelist.clear();
			lat.clear();
			lon.clear();
			int success=json.getInt("success");
			if(success == 1){
			JSONArray products= json.getJSONArray("product");
					

				for(int i=0;i<products.length();i++)
				{
					
					JSONObject c=products.getJSONObject(i);
				
					String name=c.getString("name");
					String id=c.getString("id");
					String lati = c.getString("lat");
					String longi = c.getString("lon");
					String image = c.getString("image");
					String branch_name = c.getString("branch_name");

					imagelist.add(image);
					item.add(name+", "+branch_name);	
					iditem.add(id);	
					lat.add(lati);
					lon.add(longi);
			
				}
				
			}
			prog.setVisibility(View.GONE);
			load.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
				
			}
			catch(JSONException e )
			{
				e.printStackTrace();
			} 

		adapter.notifyDataSetChanged(); 
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int itemPosition, long arg3) {
				// TODO Auto-generated method stub
				String product = item.get(itemPosition).toString(); 
				String get_app_id=iditem.get(itemPosition).toString(); 
				String lati=lat.get(itemPosition).toString(); 
				String longi=lon.get(itemPosition).toString(); 
				String imageclick=imagelist.get(itemPosition).toString();
				
				
				Intent intent = new Intent(DetailsInfo.this,Item_del.class); 
				SharedPreferences settings= getSharedPreferences("myprefs", 0);
				SharedPreferences.Editor editor= settings.edit();
				
				editor.putString("name", product);
				editor.commit();
				editor.putString("app_id", get_app_id);
				editor.putString("lat", lati);
				editor.putString("lon", longi);
				editor.putString("image", imageclick);
				editor.commit();
				if(netConnection()){
					 startActivity(intent);
					 overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
					 }else{
						 Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
					 }
			}
			
			
		});
		
	}
}
}
