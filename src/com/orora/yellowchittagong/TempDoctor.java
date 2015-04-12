package com.orora.yellowchittagong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tawsif.finalproject.R;

public class TempDoctor extends Activity{
	
	EditText editview;
	Button save;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tempdoctor);
		
		editview = (EditText) findViewById(R.id.doctorNumber);
		save = (Button) findViewById(R.id.button1);
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String number = editview.getText().toString();
				
				SQLiteDatabase db = openOrCreateDatabase("doctor", MODE_PRIVATE, null);
				
				SharedPreferences setting = getSharedPreferences("Doctor", 0);
				
				String value = setting.getString("given", "no");

				if(value.endsWith("no")){
					db.execSQL("INSERT INTO numbertable VALUES('"+number+"');");
				}else{
					db.execSQL("UPDATE numbertable SET doctornumber='"+number+"';");					
				}
				db.close();
				
//				SharedPreferences setting = getSharedPreferences("Doctor", 0);
				
				SharedPreferences.Editor editor = setting.edit();
				
				editor.putString("given", "yes");
				editor.commit();
				
				Intent intent = new Intent(TempDoctor.this,Temp.class);
				startActivity(intent);
				
			}
		});
		
	}

}

