package com.orora.yellowchittagong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.tawsif.finalproject.R;


public class AboutUs extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);

	}
	
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition  (R.anim.in_from_left, R.anim.right_out_back);
	}
	
	public void One(View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false)
				.setPositiveButton("NO", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						finish();

					}
				}).setNegativeButton("YES", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						performDial("01856707020");
					}
				});

		AlertDialog diag = builder.create();

		diag.setIcon(R.drawable.ic_launcher);
		diag.setTitle("Do you want to Call ME ?");
	//	diag.setMessage("This alert has Call Option.");
		diag.show();

	}




	public void Two(View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false)
				.setPositiveButton("NO", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						finish();

					}
				}).setNegativeButton("YES", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						performDial("01866666666");
					}
				});

		AlertDialog diag = builder.create();

		diag.setIcon(R.drawable.ic_launcher);
		diag.setTitle("Do you want to Call ME ?");
	//	diag.setMessage("This alert has Call Option.");
		diag.show();

	}


	

	
	private void performDial(String numberString) {
	    if (!numberString.equals("")) {
	       Uri number = Uri.parse("tel:" + numberString);
	       Intent dial = new Intent(Intent.ACTION_CALL, number);
	       startActivity(dial);
	    }
	}

}
