package com.orora.yellowchittagong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.tawsif.finalproject.R;

public class Hospital extends Activity {

	ImageButton b_pressure, temp, sugar, h_call;
	ImageButton home;
	Button h_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hospital);

		home = (ImageButton) findViewById(R.id.ImageButton01);

		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Hospital.this, MainActivity.class);
				startActivity(intent);
				MainActivity.ac.finish();
				GetCategoryDetails.srch.finish();
				// DetailsInfo.det.finish();
				// AllDetailsInfo.allDet.finish();
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.right_out_back);
			}
		});

		// h_button= (Button) findViewById(R.id.home);
		//
		// h_button.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(Hospital.this,MainActivity.class);
		// startActivity(intent);
		// overridePendingTransition (R.anim.right_slide_in,
		// R.anim.right_slide_out);
		//
		// }
		// });

		b_pressure = (ImageButton) findViewById(R.id.b_pressure);
		temp = (ImageButton) findViewById(R.id.temp);
		sugar = (ImageButton) findViewById(R.id.sugar);

		h_call = (ImageButton) findViewById(R.id.h_call);

		b_pressure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Hospital.this, Pressure.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_slide_in,
						R.anim.right_slide_out);

			}
		});

		temp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Hospital.this, Temp.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_slide_in,
						R.anim.right_slide_out);

			}
		});

		sugar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Hospital.this, Sugar.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_slide_in,
						R.anim.right_slide_out);

			}
		});

		h_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				performDial("01731887844");
			}
		});

	}

	public boolean netConnection() {

		ConnectivityManager cm = (ConnectivityManager) Hospital.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();

		return isConnected;
	}

	@Override
	public void onBackPressed() {

		this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.right_out_back);
	}

	private void performDial(String numberString) {
		if (!numberString.equals("")) {
			Uri number = Uri.parse("tel:" + numberString);
			Intent dial = new Intent(Intent.ACTION_CALL, number);
			startActivity(dial);
		}
	}
}
