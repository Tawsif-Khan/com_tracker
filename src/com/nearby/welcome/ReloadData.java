package com.nearby.welcome;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.Map.CreateMarker;
import com.example.finalproject.Utility;
import com.tawsif.finalproject.R;

public class ReloadData {

	public static ArrayList<String> number, reg_id, final_name, final_number;
	public static ArrayList<Bitmap> bmList, markerList;
	public static Bitmap profilePicture, defaultMarker;

	public static HashMap<String, String> regId, userName,latiHash,longiHash;
	public static HashMap<String, Bitmap> userImage, userMarker,
			defaultMarkerHash;
	SQLiteDatabase db;
	Context context = null;
	
	public ReloadData(Context context){
		
		db = context.openOrCreateDatabase("tracker", 0, null);
		this.context= context;
	}
	public void getContactDetailsInHash() {

		number = new ArrayList<String>();
		number.clear();
		final_number = new ArrayList<String>();
		final_number.clear();
		final_name = new ArrayList<String>();
		final_name.clear();
		reg_id = new ArrayList<String>();
		reg_id.clear();
		bmList = new ArrayList<Bitmap>();
		bmList.clear();
		markerList = new ArrayList<Bitmap>();
		markerList.clear();

		userName = new HashMap<String, String>();
		userName.clear();
		userImage = new HashMap<String, Bitmap>();
		userImage.clear();
		userMarker = new HashMap<String, Bitmap>();
		userMarker.clear();
		regId = new HashMap<String, String>();
		regId.clear();
		latiHash = new HashMap<String, String>();
		latiHash.clear();
		longiHash = new HashMap<String, String>();
		longiHash.clear();

		String query = "SELECT * FROM contacts";
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String Number = cursor.getString(cursor
						.getColumnIndex("phoneNumber"));
				String reg_ID = cursor.getString(cursor
						.getColumnIndex("reg_id"));
				String image_url = "";
				byte[] imageBlob = null;
				byte[] marker = null;
				imageBlob = cursor.getBlob(cursor.getColumnIndex("image"));
				marker = cursor.getBlob(cursor.getColumnIndex("marker"));
				String lati = cursor.getString(cursor.getColumnIndex("lati"));
				String longi = cursor.getString(cursor.getColumnIndex("longi"));
				
				final_name.add(name);
				final_number.add(Number);
				reg_id.add(reg_ID);
				Bitmap mk = Utility.getPhoto(marker);
				markerList.add(mk);
				RoundedTransformation round = new RoundedTransformation(30, 1);
				Bitmap bm = round.transform(Utility.getPhoto(imageBlob));
				bmList.add(bm);

				userName.put(Number, name);
				regId.put(Number, reg_ID);
				userMarker.put(Number, mk);
				round = new RoundedTransformation(30, 1);
				userImage.put(Number, bm);
				latiHash.put(Number, lati);
				longiHash.put(Number, longi);
			} while (cursor.moveToNext());

		}
		byte[] my_pic = null;
		String get_pro_pic = "SELECT * FROM profile;";
		Cursor pp_cr = db.rawQuery(get_pro_pic, null);
		if (pp_cr != null && pp_cr.getCount() > 0) {
			pp_cr.moveToFirst();
			do {
				my_pic = pp_cr.getBlob(pp_cr.getColumnIndex("image_path"));
				// Toast.makeText(context, my_pic, Toast.LENGTH_LONG).show();
			} while (pp_cr.moveToNext());
		}
		CreateMarker cm = new CreateMarker(Utility.getPhoto(my_pic),
				BitmapFactory.decodeResource(context.getResources(),
						R.drawable.arrow_down));
		profilePicture = cm.getMarker();
		db.close();
		CreateMarker defaultCM = new CreateMarker(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.photo_icon),
				BitmapFactory.decodeResource(context.getResources(),
						R.drawable.arrow_down));
		defaultMarker = defaultCM.getMarker();
	}
	
	
	public String getName(String number){

		String qry = "SELECT name FROM contacts WHERE phoneNumber='"+number+"';";
		Cursor cursor = db.rawQuery(qry, null);
		if(cursor != null && cursor.getCount()>0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			if(!name.equals(""))
			return name;
		}

		return number;
	}
}
