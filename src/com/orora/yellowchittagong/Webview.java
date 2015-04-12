package com.orora.yellowchittagong;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;

import com.tawsif.finalproject.R;

public class Webview extends Activity{
	
	WebView webview;
	String website1;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		webview=(WebView)findViewById(R.id.webView1);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);

		SharedPreferences settings= getSharedPreferences("myprefs", 0);
		website1= settings.getString("website1","");
//		webView.loadUrl("file:///android_asset/webPage.html");
		webview.loadUrl(website1);
		
		
	}
	
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition  (R.anim.in_from_left, R.anim.right_out_back);
	}
}
