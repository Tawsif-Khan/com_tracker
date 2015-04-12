package com.iiuc.finalproject.registration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;


public class SecuritySMSReciever extends BroadcastReceiver {

	String recMsgString = "";
    String fromAddress = "";
	String message ="";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			
			String num = getMobileNumber(intent).toString();
			
			Intent kk = new Intent(context,SecuritySMS.class);
			kk.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			kk.putExtra("msg", num);
			kk.putExtra("number", fromAddress);
			
			if(RegisterName.number != null)
			if(fromAddress.equals(RegisterName.number) && num.equals(SecuritySMS.msgSent)){

				SecuritySMS.sec_code_view.setText(num);
				SecuritySMS.done.setVisibility(View.VISIBLE);
				SecuritySMS.inactiveBtn.setVisibility(View.GONE);
			}
			
		}
		
	}

	public String getMobileNumber(Intent intent){
		
		Bundle bundle = intent.getExtras();

		Object[] pdus;
		 message = "";
	    SmsMessage recMsg = null;
//	    byte[] data = null;

	    if (bundle != null)
	    {
	      //---retrieve the SMS message received---
	      pdus = (Object[]) bundle.get("pdus");

	      recMsg = SmsMessage.createFromPdu((byte[]) pdus[0]);
	        message = recMsg.getMessageBody();
	        fromAddress = recMsg.getOriginatingAddress();
	        while (message.contains("FLAG"))
	            message = message.replace("FLAG", "");
	    }
	    
		return message;
	    
	}
	
}
