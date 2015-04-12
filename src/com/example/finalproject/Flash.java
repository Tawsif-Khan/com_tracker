package com.example.finalproject;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iiuc.finalproject.registration.RegisterName;
import com.nearby.welcome.Optionalpage;
import com.nearby.welcome.ReloadData;
import com.tawsif.finalproject.R;

public class Flash extends Activity {

	ProgressBar progress;
	GoogleCloudMessaging gcm;
	String PROJECT_NUMBER = "412972523253";
	String regid;
	int flag = 0;
	
	Context context = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.flash);
		
		context = this;
		CreateFolder();
		
		progress = (ProgressBar) findViewById(R.id.progressBar1);

		progress.setVisibility(View.VISIBLE);
		
		SharedPreferences settings = getSharedPreferences("prefs", 0);
		String id = settings.getString("regId", "");

//		if(ChatOnlyOne.SINGLE_MSG == 1){
//			String member_reg_id = getIntent().getExtras().getString("member_reg_id");
//			String contact_number = getIntent().getExtras().getString("contact_number");
//			StoreRoom sr = new StoreRoom();
//			Intent intent = new Intent(Flash.this,ChatOnlyOne.class);
//			intent.putExtra("member_reg_id", member_reg_id);
//			intent.putExtra("contact_number", contact_number);
//			startActivity(intent);
//		}else{
		
		if (!id.equals("")) {
			ReloadData rd = new ReloadData(Flash.this);
			rd.getContactDetailsInHash();

			Intent intent = new Intent(Flash.this, Optionalpage.class);
			intent.putExtra("list_flag", "not");
			startActivity(intent);
			
			this.finish();
		} else{
			SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE, null);
			db.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER  PRIMARY KEY AUTOINCREMENT, name VARCHAR, phoneNumber VARCHAR UNIQUE,reg_id VARCHAR UNIQUE,image BLOB NOT NULL,marker BLOB NOT NULL,lati VARCHAR, longi VARCHAR);");
			db.execSQL("CREATE TABLE IF NOT EXISTS groups (id INTEGER PRIMARY KEY AUTOINCREMENT, group_name  VARCHAR,group_title VARCHAR);");
			db.execSQL("CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY AUTOINCREMENT, group_id VARCHAR, member_number VARCHAR,reg_id VARCHAR);");
			db.execSQL("CREATE TABLE IF NOT EXISTS chat (id INTEGER PRIMARY KEY AUTOINCREMENT, group_id VARCHAR, member_id VARCHAR,msg VARCHAR,msg_time VARCHAR,sent VARCHAR,msg_type VARCHAR,image_in_phone VARCHAR);");
			db.execSQL("CREATE TABLE IF NOT EXISTS profile (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image_path BLOB NOT NULL);");
			db.close();
						
			getRegId();
		}
//		}
	}
	

	public void getRegId() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;
					Log.i("GCM", msg);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					//getRegId();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// etRegId.setText(msg + "\n");
//				Toast.makeText(getApplicationContext(), regid,
//						Toast.LENGTH_LONG).show();
				try{
				if (regid.equals("")){
					getRegId();
				}
				else{
				Intent intent = new Intent(Flash.this, RegisterName.class);
				intent.putExtra("regId", regid);
				startActivity(intent);
				finish();
				}
				}catch(NullPointerException e){
//					Toast.makeText(getApplicationContext(), "Connection Problem , Try again...", Toast.LENGTH_SHORT).show();
					getRegId();
				}
			}
		}.execute(null, null, null);
	}
	
	public void CreateFolder(){
		File file;

	// Check for SD Card
	if (!Environment.getExternalStorageState().equals(
			Environment.MEDIA_MOUNTED)) {
		Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
				.show();
	} else {
		// Locate the image folder in your SD Card
		file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "Com_tracker");
		// Create a new folder if no folder named Com_tracker exist
		file.mkdirs();
		//Toast.makeText(getApplicationContext(), "file created", Toast.LENGTH_LONG).show();
	}
	}
	
	

}
