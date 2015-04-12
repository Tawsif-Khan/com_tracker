package com.finalproject.luggageguard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tawsif.finalproject.R;

public class AlarmBox extends Activity{

	Button btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_box);
		getActionBar().hide();
		
		btn = (Button)findViewById(R.id.alarmOffBtn);
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				BluetoothStatereceiver.mp.stop();
//				BluetoothStatereceiver.vib.cancel();
				finish();
			}
		});
	}
}
