package com.example.finalproject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.finalproject.popup.MenuItem;
import com.finalproject.popup.QuickActionMenu;
import com.finalproject.popup.QuickActionMenu.OnItemSelectedListener;
import com.tawsif.finalproject.R;

public class EditProfile extends Activity implements OnItemSelectedListener {

	private static int IMAGE_PICKER_SELECT = 1, TAKE_PICTURE = 0;
//	private Button mTakePhoto, mSelectPhoto;
	public Bitmap cameraBitmap, scaled1;
	long timename = 0;
	LinearLayout progress;
	ImageView im;
	EditText nameV;
	Button update;
	static String imagePath;
	String nameS,my_reg,myNumber;
	private static final int GALLERY = 0;
	private static final int CAMERA = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_profile);

		im = (ImageView) findViewById(R.id.ivImage);
		progress = (LinearLayout) findViewById(R.id.progress);
		nameV = (EditText) findViewById(R.id.name);
		update = (Button) findViewById(R.id.update);
		progress.setVisibility(View.GONE);
		
		SharedPreferences settings = getSharedPreferences("prefs", 0);
		my_reg = settings.getString("regId", "");
		myNumber = settings.getString("myNumber", "");
		
		SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE, null);
		byte[] image_path_blob = null ;
		String name = "";
		String qrg = "SELECT * FROM profile;";
		Cursor crg = db.rawQuery(qrg, null);
		if (crg != null && crg.getCount() > 0) {
			crg.moveToFirst();
			do {
				image_path_blob = crg.getBlob(crg.getColumnIndex("image_path"));
				name = crg.getString(crg.getColumnIndex("name"));

			} while (crg.moveToNext());
		}
		if (image_path_blob != null)
			im.setImageBitmap(Utility.getPhoto(image_path_blob));

		// /// Take image and upload ///////////
		nameV.setText(name);
		
		final QuickActionMenu menu = new QuickActionMenu(this);
        
		menu.setHeaderTitle("Select Method");
		menu.setOnItemSelectedListener(this);		
		menu.add(GALLERY, R.string.gallery).setIcon(getResources().getDrawable(R.drawable.gellery));
		menu.add(CAMERA, R.string.camera).setIcon(getResources().getDrawable(R.drawable.camera));
//		menu.show(v);
		
		ImageView add = (ImageView)findViewById(R.id.btnSelectPhoto);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			menu.show(v);	
			}
		});
		
		
		update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				nameS = nameV.getText().toString();
				//Toast.makeText(getApplicationContext(), nameS, 1000).show();
				if(!nameS.equals("")){
					
					if(scaled1 != null){
						new UploadTask().execute(scaled1);
						new AsyncTaskRunner().execute();
					}
				}else{
					Toast.makeText(getApplicationContext(), "You must select image and give a name", Toast.LENGTH_LONG).show();
				}
			}
		});
		

	}
	
	public void onItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case GALLERY:
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i,
						IMAGE_PICKER_SELECT);
			
//				Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
				break;
	
			case CAMERA:
				takePhoto();
	        	
//				Toast.makeText(getApplicationContext(), "Add to Playlist", Toast.LENGTH_SHORT).show();
				break;
	
		}
	}


	public boolean hasImageCaptureBug() {

		// list of known devices that have the bug
		ArrayList<String> devices = new ArrayList<String>();
		devices.add("android-devphone1/dream_devphone/dream");
		devices.add("generic/sdk/generic");
		devices.add("vodafone/vfpioneer/sapphire");
		devices.add("tmobile/kila/dream");
		devices.add("verizon/voles/sholes");
		devices.add("Sony/C2305/C2305");

		return devices.contains(android.os.Build.BRAND + "/"
				+ android.os.Build.PRODUCT + "/" + android.os.Build.DEVICE);

	}

	
	private void takePhoto() {
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (hasImageCaptureBug()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(getTempFile(EditProfile.this)));
			startActivityForResult(intent, TAKE_PICTURE);

		} else {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
			startActivityForResult(intent, TAKE_PICTURE);
		}
	}

	private File getTempFile(Context context) {
		// it will return /sdcard/image.tmp
		final File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, "image.tmp");
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {



		if (requestCode == IMAGE_PICKER_SELECT
				&& resultCode == Activity.RESULT_OK) {
			Bitmap bitmap1 = getBitmapFromCameraData(data, EditProfile.this);

			// this line will resize your image
			// Bitmap bitmap=getScaledBitmap(bitmap1, 1100, 1100);

			int nh = (int) (bitmap1.getHeight() * (512.0 / bitmap1.getWidth()));
			scaled1 = Bitmap.createScaledBitmap(bitmap1, 512, nh, true);


			im.setImageBitmap(scaled1);
			
			

		}
		if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK
				&& data != null) {
			// get bundle
			Bundle extras = data.getExtras();

			// get bitmap
			cameraBitmap = (Bitmap) extras.get("data");
			int nh = (int) (cameraBitmap.getHeight() * (512.0 / cameraBitmap
					.getWidth()));
			scaled1 = Bitmap.createScaledBitmap(cameraBitmap, 512, nh, true);


			im.setImageBitmap(scaled1);

		}
	}

	public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		imagePath = picturePath;
		cursor.close();
		return BitmapFactory.decodeFile(picturePath);
	}

	private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

		protected Void doInBackground(Bitmap... bitmaps) {
			if (bitmaps[0] == null)
				return null;

			Bitmap bitmap = bitmaps[0];
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			InputStream in = new ByteArrayInputStream(stream.toByteArray());

			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				timename = System.currentTimeMillis();
				HttpPost httppost = new HttpPost(
						"http://appseden.net/foundit/tracker/allpost.php"); // server

				MultipartEntity reqEntity = new MultipartEntity();
				reqEntity.addPart("myFile", timename + ".jpg", in);
			
				httppost.setEntity(reqEntity);

				HttpResponse response = null;
				try {
					response = httpclient.execute(httppost);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if (response != null) {

					}
				} finally {

				}
			} finally {

			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}

		protected void onPreExecute() {

//			im.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);

		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progress.setVisibility(View.GONE);
			im.setVisibility(View.VISIBLE);
			
			SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE,
					null);
			
			byte[] blob = Utility.getBytes(scaled1);
			ContentValues cv = new ContentValues();
			
			cv.put("name", nameS);
			cv.put("image_path", blob);
			db.insert("profile", null, cv);
			
//			String qry = "INSERT INTO profile (name,image_path) VALUES ('"+nameS+"','"
//					+ imagePath + "')";
//			db.execSQL(qry);
			db.close();
			
			

			Toast.makeText(EditProfile.this, "Image uploaded",
					Toast.LENGTH_SHORT).show();

		}
	}
	
	private class AsyncTaskRunner extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();
		ProgressDialog dialog;
		String res = "";
		JSONObject json;

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("name", nameS));
			params.add(new BasicNameValuePair("phone", myNumber));
			params.add(new BasicNameValuePair("image_url", timename+".jpg"));
			

			json = jParser.makeHttpRequest(
					"http://appseden.net/foundit/tracker/allpost.php", "POST",
					params);

			return res;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(EditProfile.this);
			dialog.setMessage("Please wait...");

		}

		protected void onPostExecute(String result) {


			Toast.makeText(getApplicationContext(), "name updated", Toast.LENGTH_LONG).show();

		}

	}

	
}
