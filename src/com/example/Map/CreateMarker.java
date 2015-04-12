package com.example.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.nearby.welcome.RoundedTransformation;

public class CreateMarker {

	Bitmap bitmap1,bitmap2;
	private int intArray[];
	
	public CreateMarker(Bitmap bm1,Bitmap bm2){
		this.bitmap1 = bm1;
		this.bitmap2 = bm2;
	}
	
	public Bitmap getMarker(){
		Bitmap bitmap = null;
	    try {
	    	
	        bitmap = Bitmap.createBitmap(120, 120, Config.ARGB_8888);

	        Canvas c = new Canvas(bitmap);
	       
	        RoundedTransformation rd = new RoundedTransformation(100, 0);
	        bitmap1 = rd.transform(bitmap1);
	        
	        Drawable drawable1 = new BitmapDrawable(bitmap1);
	        Drawable drawable2 = new BitmapDrawable(bitmap2);

	        
	        drawable1.setBounds(0, 0, 120, 100); 
	        drawable2.setBounds(10, 102, 120, 120);
	        drawable1.draw(c);
	        drawable2.draw(c);

	    } catch (Exception e) {
	    }
	    return bitmap;
	}
	
}
