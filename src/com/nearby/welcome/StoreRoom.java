package com.nearby.welcome;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.finalproject.Flash;

import android.graphics.Bitmap;

public class StoreRoom {

	public static ArrayList<String> userNumberList = ReloadData.final_number, 
									userRegIdList = ReloadData.reg_id,
									userNameList = ReloadData.final_name;
	public static ArrayList<Bitmap> userPictureList = ReloadData.bmList ,
									userMarkerList = ReloadData.markerList;
	public static Bitmap myProfilePicture = ReloadData.profilePicture,
						theDefaultMarker = ReloadData.defaultMarker;
	
	
	public static HashMap<String, String> regIdHash = ReloadData.regId,
										  userNameHash = ReloadData.userName;
	public static HashMap<String, Bitmap> userImageHash = ReloadData.userImage,
										  userMarkerHash = ReloadData.userMarker,
										  defaultMarkerHash;
}
