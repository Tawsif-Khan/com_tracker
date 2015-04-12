package com.orora.yellowchittagong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class MainActivity extends Activity {
	
	Button by,current_loc;
	
	GridView gd;
	static final String[] ITEMS={"Atm",
		                        "Bank",
		                        "Education",
		                        "Hospital",
		                        "Convention",
		                         "Market",
		                         "Police Station",
		                         "Restaurant",
		                         "Saloon",
		                         "Bus Counter",
		                         "Hotel",
		                         "Filling Station",
		                         "Car Station",
								"Railway Station",
								"Beauty Parlour"
							
								};

	int flag = 0;
	public static Activity ac;
	public static int det = 0,alld = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_yc);
		
		ac = this;
		current_loc=(Button)findViewById(R.id.current_loc);
		
		
		
		current_loc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,Current_loc.class);
				if(netConnection()){
					 startActivity(intent);
					 overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
					 }else{
						 Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
					 }
			}
		});
		gd= (GridView)findViewById(R.id.gridview1);
		gd.setAdapter(new ImageAdapter(this, ITEMS));
		
		gd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long Id) {
				// TODO Auto-generated method stub
				
				String product=((TextView) v.findViewById(R.id.grid_item_label)).getText().toString();
				
				Intent intent=new Intent(MainActivity.this,GetCategoryDetails.class);
				
				//intent.putExtra("session",product);
				SharedPreferences settings= getSharedPreferences("myprefs", 0);
				SharedPreferences.Editor editor= settings.edit();
				position++;
				editor.putString("session_name", product);
				editor.putString("session",product);
				editor.commit();
				startActivity(intent);
				overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
				
				
				//Toast.makeText(getApplicationContext(),((TextView) v.findViewById(R.id.grid_item_label)).getText(), 
						//Toast.LENGTH_SHORT).show();
				
			}
		});
		
	}
	
	
	public void onBackPressed() 
	{
		if(flag>0){
	    this.finish();
	    overridePendingTransition  (R.anim.in_from_left, R.anim.right_out_back);
		}else{
			flag++;
			Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_LONG).show();
		}
		
		}
	
public boolean netConnection(){
		
		ConnectivityManager cm =
		        (ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		
		return isConnected;
	}

	
}
