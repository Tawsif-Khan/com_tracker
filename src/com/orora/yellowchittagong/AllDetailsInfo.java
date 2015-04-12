package com.orora.yellowchittagong;

import java.util.ArrayList;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class AllDetailsInfo extends Activity{
	

	ProgressBar prog;
	TextView category;
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
		public static Activity allDet;
		
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alldetails);
		
		allDet = this;
		MainActivity.alld = 1;
		
		home= (ImageButton) findViewById(R.id.image_home); 
		prog= (ProgressBar) findViewById(R.id.progressBar1);
		prog.setVisibility(View.GONE);
		
		home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(AllDetailsInfo.this,MainActivity.class);
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
//				 Intent intent = new Intent(AllDetailsInfo.this,MainActivity.class);
//					startActivity(intent);
//					overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
//					
//			}
//		});
	
		category=(TextView) findViewById(R.id.category);
		
		SharedPreferences setting= getSharedPreferences("myprefs", 0);
		category1= setting.getString("session","");

		String category1_name = setting.getString("session_name", "");
		category.setText(category1_name);
		
		
		lv = (ListView) findViewById(R.id.list_view);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
	

		SharedPreferences settings= getSharedPreferences("myprefs", 0);
		
		s= settings.getString("session","");
		ArrayList<String> detail = new ArrayList<String>();
		
		
	
		
		adapter=new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name,item);
		lv.setAdapter(adapter);
		
		if(netConnection())
		new AsyncRunner().execute();
    
		
	}
	
	public boolean netConnection(){
		
		ConnectivityManager cm =
		        (ConnectivityManager)AllDetailsInfo.this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
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
	String lati,longi;
	private ProgressDialog pDialog;
	
	@Override
	
	protected String doInBackground(String...args){
		
	List <NameValuePair> params=new ArrayList<NameValuePair>();
	
	params.add(new BasicNameValuePair("catagory", s));

	json=jParser.makeHttpRequest("http://www.appseden.net/yellowctg/yellowctg/JSON/searching.php", "POST", params);
		
	return res;
	}
	
	protected void onPreExecute(){
		super.onPreExecute();
		
//		pDialog=new ProgressDialog (AllDetailsInfo.this);
//		pDialog.setMessage("Please wait..");
//		
//		pDialog.show();
		lv.setVisibility(View.GONE);
		prog.setVisibility(View.VISIBLE);
		
	}
	
	protected void onPostExecute(String file_url){
		
//		pDialog.dismiss();
		//view.setText(res);
		
		try{

			item.clear();
			iditem.clear();
			imagelist.clear();
			lat.clear();
			lon.clear();
			int success=json.getInt("success");
			JSONArray products= json.getJSONArray("product");
					
					//Log.d("Length Of Product Array")

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
			catch(JSONException e )
			{
				e.printStackTrace();
			} 
		prog.setVisibility(View.GONE);
		lv.setVisibility(View.VISIBLE);

		adapter.notifyDataSetChanged(); 
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int itemPosition, long arg3) {
				// TODO Auto-generated method stub
				String product = item.get(itemPosition).toString(); 
				String get_app_id=iditem.get(itemPosition).toString(); 
				lati=lat.get(itemPosition).toString(); 
				longi=lon.get(itemPosition).toString(); 
				String imageclick=imagelist.get(itemPosition).toString();
				Toast.makeText(getApplicationContext(), get_app_id, Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(AllDetailsInfo.this,Item_del.class); 
				SharedPreferences settings= getSharedPreferences("myprefs", 0);
				SharedPreferences.Editor editor= settings.edit();
				
				editor.putString("name", product);
				editor.commit();
				editor.putString("app_id", get_app_id);
				editor.putString("lat", lati);
				editor.putString("lon", longi);
				editor.putString("image", imageclick);
				editor.commit();
				startActivity(intent); 
				overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
				
			}
			
			
		});
		
	}
}
}
