package com.orora.yellowchittagong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class Temp extends Activity {
	
	EditText age,b_temp,patientName,patientNumber;
	TextView ageV,bloodV,result,nameView,genderV;
	Button send,edit;
	RadioButton M,F;
	private RadioGroup radioSexGroup;
	
	String patient,G,Pnumber;
	int flag;
	int ageValue,temp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp);
		
		init();
		
		SharedPreferences setting = getSharedPreferences("DataBase", 0);
		
		String value = setting.getString("created", "no");
		
		if(value.endsWith("no")){
			String query = "CREATE TABLE numbertable (doctornumber VARCHAR);";
			
			SQLiteDatabase db = openOrCreateDatabase("doctor", MODE_PRIVATE, null);
			
			db.execSQL(query);
			
			db.close();
			
			setting = getSharedPreferences("DataBase", 0);
			
			SharedPreferences.Editor editor = setting.edit();
			
			editor.putString("created", "yes");
			editor.commit();
			
			Toast.makeText(getApplicationContext(), "Put Doctor's number first", Toast.LENGTH_LONG).show();
		}
		
		SharedPreferences settings = getSharedPreferences("Doctor", 0);
		
		 value = settings.getString("given", "no");
		
		if(value.endsWith("no")){
			
			Intent intent = new Intent(Temp.this,TempDoctor.class);
			startActivity(intent);
			
		}
		
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(Temp.this,TempDoctor.class);
				startActivity(intent);
			}
		});
		
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String ageString = age.getText().toString();
				String u = b_temp.getText().toString();
				
				patient = patientName.getText().toString();
				Pnumber = patientNumber.getText().toString();

				int selectedId = radioSexGroup.getCheckedRadioButtonId();
				

	            // find the radiobutton by returned id
	            M = (RadioButton) findViewById(selectedId);
				G = M.getText().toString();
	            
				if(!ageString.equals("")&& !u.equals("")){
				
				ageValue = Integer.parseInt(ageString);
				temp = Integer.parseInt(u);
				//diastolic = Integer.parseInt(d);
				
				
				if(temp>=95 && temp<=100){
					flag = 0;
					normal();
				}else if(temp>100 && temp<102){
					flag = 1;
					alert();
				}else if(temp>=120 && temp<=105){
					flag = 2;
					alert();
				}
			
				else{
					flag = 10;
					alert();
				}
								}	
			}
		});
		
		
	}
	
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition  (R.anim.in_from_left, R.anim.right_out_back);
	}
	
	public void normal(){
		
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Result :");
		
		alertDialog.setMessage("Your Temperature is normal.");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
		// here you can add functions
			
		}
		});
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.show();
		
	}
	
	
	public void alert(){
		
		AlertDialog show = new AlertDialog.Builder(Temp.this).setTitle("Send SMS")
			    .setMessage("Your Body Tempareture is not normal. Do you want to send an SMS to the doctor and to your relative?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
						SQLiteDatabase db = openOrCreateDatabase("doctor", MODE_PRIVATE, null);
						
						Cursor cursor = db.rawQuery("SELECT * FROM numbertable;",null);
						cursor.moveToFirst();
						String doctor = cursor.getString(cursor.getColumnIndex("doctornumber"));

						Toast.makeText(getApplicationContext(), doctor+" "+Pnumber, Toast.LENGTH_LONG).show();
			        	sendSmsByManager(doctor);
			        	sendSmsByManager(Pnumber);
			        	db.close();
			//        	sendSmsByManager("01676517021");
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
	
	
	public void sendSmsByManager(String phone) {
		 String text;
		 		if(flag == 0){
		 			text = "BP :"+temp+" \nCondition :Normal Body Temperature.\nPatient Name :"+patient+"\nSex :"+G+"\nAge :"+ageValue+"Contact :"+Pnumber;
		 		}else if(flag == 1){
		 			text = "BP :"+temp+"\nCondition :High Body Temperature.\nPatient Name :"+patient+"\nSex :"+G+"\nAge :"+ageValue+"Contact :"+Pnumber;
		 		}else if(flag == 2){
		 			text = "BP :"+temp+"\nCondition :Very High Body Temperature.\nPatient Name :"+patient+"\nSex :"+G+"\nAge :"+ageValue+"Contact :"+Pnumber;
		 		}
		 		
		 		
		 		else {
		 			text = "BP :"+temp+"/nCondition :Not Applicable.\nPatient Name :"+patient+"\nSex :"+G+"\nAge :"+ageValue+"Contact :"+Pnumber;
		 		} 
		 
		         try {
		 
		             // Get the default instance of the SmsManager
		 
		             SmsManager smsManager = SmsManager.getDefault();
		 
		             smsManager.sendTextMessage(phone.toString(),
		 
		                     null, 
		 
		                     text.toString(),
		 
		                     null,
		 
		                     null);
		 
		             Toast.makeText(getApplicationContext(), "Your sms has successfully sent!",
		 
		                     Toast.LENGTH_LONG).show();
		 
		         } catch (Exception ex) {
		 
		             Toast.makeText(getApplicationContext(),"Your sms has failed...",
		 
		                     Toast.LENGTH_LONG).show();
		 
		             ex.printStackTrace();
		 
		         }
		 
		     }

	 public void init(){
		 
		 nameView =(TextView) findViewById(R.id.nameV);
			genderV = (TextView) findViewById(R.id.gender);
			ageV = (TextView) findViewById(R.id.AgeView);
			bloodV = (TextView) findViewById(R.id.bloodView);
			
			
			radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
			age = (EditText) findViewById(R.id.age);
			b_temp  = (EditText) findViewById(R.id.pressureUp);
			
			patientName = (EditText) findViewById(R.id.name);
			patientNumber = (EditText) findViewById(R.id.patient);
			
			send = (Button) findViewById(R.id.button1);
			edit = (Button) findViewById(R.id.button2);
		 
	 }
	 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
