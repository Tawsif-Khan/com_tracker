package com.iiuc.finalproject.registration;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nearby.welcome.ConnectionDetector;
import com.tawsif.finalproject.R;


public class RegisterName extends Activity {

	public static String number, regId, country_code,numberS;
	EditText numberV, codeV;
	String countryIso="";
	boolean statusOfGPS, statusOfInternet, statusOfNetwork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.register_name);
		numberV = (EditText) findViewById(R.id.number);
		codeV = (EditText) findViewById(R.id.code);
		regId = getIntent().getExtras().getString("regId");

		codeV.setText(GetCountryZipCode(getApplicationContext()));
//		numberV.setActivated(true);
//		codeV.setActivated(false);
		
		numberV.setFocusable(true);
		
		((Button) findViewById(R.id.button1))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						numberS = numberV.getText().toString();
						country_code = codeV.getText().toString();
						number = "+" + country_code + numberS;
						if (numberS.equals("") || country_code.equals(""))
							Toast.makeText(getApplicationContext(),
									"You must put country code & phone number",
									Toast.LENGTH_LONG).show();
						else {
							goToNext();
							//validatPhoneNumber(country_code+numberS);
							
						}
					}
				});
	}
	
//	public void validatPhoneNumber(String phoneNumber){
//		// Use the library’s functions
//		PhoneNumberUtil phoneUtil = PhoneNumberUtil
//				.getInstance();
//		PhoneNumber phNumberProto = null;
//
//		try {
//
//
//			// You can find your country code here
//			// http://www.iso.org/iso/country_names_and_code_elements
//			phNumberProto = phoneUtil.parse("8801856707020", "BD");
//
//		} catch (NumberParseException e) {
//			// if there’s any error
//			System.err
//					.println("NumberParseException was thrown: "
//							+ e.toString());
//		}
//
//		// check if the number is valid
//		boolean isValid = phoneUtil
//				.isValidNumber(phNumberProto);
//
//		if (isValid) {
//
////			goToNext();
//
//			// get the valid number’s international format
//			String internationalFormat = phoneUtil.format(
//					phNumberProto,
//					PhoneNumberFormat.INTERNATIONAL);
//
//			Toast.makeText(
//					getBaseContext(),
//					"Phone number VALID: "
//							+ internationalFormat,
//					Toast.LENGTH_SHORT).show();
//
//		} else {
//
//			// prompt the user when the number is invalid
//			Toast.makeText(getBaseContext(),
//					"Phone number is INVALID: " + phoneNumber,
//					Toast.LENGTH_SHORT).show();
//
//		}
//	}
	
	public void goToNext() {
		try {
			// Loading map
			LocationManager manager = (LocationManager) getSystemService(SecuritySMS.LOCATION_SERVICE);
			statusOfGPS = manager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			statusOfNetwork = manager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			ConnectionDetector cd = new ConnectionDetector(RegisterName.this);
			statusOfInternet = cd.isConnectingToInternet();

			if ((statusOfGPS == false && statusOfNetwork == false)
					|| statusOfInternet == false) {
				LayoutInflater li = LayoutInflater.from(RegisterName.this);
				View promptsView;
				AlertDialog.Builder builder;
				final AlertDialog alertDialog;
				Button btnOk = null;
				if (statusOfGPS == false && statusOfInternet == false
						&& statusOfNetwork == false) {
					promptsView = li.inflate(R.layout.int_loc_dialog, null);
					builder = new AlertDialog.Builder(RegisterName.this);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView.findViewById(R.id.btnOk);
				} else if (statusOfInternet == false
						&& (statusOfGPS == true || statusOfNetwork == true)) {
					promptsView = li.inflate(R.layout.loc_dialog, null);
					builder = new AlertDialog.Builder(RegisterName.this);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView.findViewById(R.id.btnOk);
				} else {
					promptsView = li.inflate(R.layout.loc_dialog, null);
					builder = new AlertDialog.Builder(RegisterName.this);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView.findViewById(R.id.btnOk);
				}

				btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						alertDialog.cancel();
					}
				});

			}

			else {
				if (statusOfNetwork == false) {
					Toast.makeText(getApplicationContext(),
							"Please turn Network status of your device oN",
							Toast.LENGTH_LONG).show();
				} else {
					try {
						
						Intent intent = new Intent(RegisterName.this,
								SecuritySMS.class);
						startActivity(intent);
						
					} catch (Exception e) {
						Toast.makeText(
								getApplicationContext(),
								"Sorry an error occured during the execution"
										+ e, Toast.LENGTH_LONG).show();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					"Internet status and GPS status of device can not be accesed",
					Toast.LENGTH_LONG).show();
		}
	}
	
	public String GetCountryZipCode(Context context) {
		// Get the country ISO
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		countryIso = manager.getSimCountryIso().toUpperCase();

		// Load resources containing the country codes list
		String[] countryCodes = this.getResources().getStringArray(
				R.array.CountryCodes);

		// Try to find what we need
		for (int i = 0; i < countryCodes.length; i++) {
			String[] line = countryCodes[i].split(",");
			// if (line.length != 2) throw new
			// Exception("Resource file looks like invalid.");

			// We have found something interesting
			if (line[1].trim().equals(countryIso.trim()))
				return line[0];
		}

		// Nothing was found
		return null;
	}
	

}
