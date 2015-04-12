package com.example.finalproject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.Map.CreateMarker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tawsif.finalproject.R;

public class AddUnknownMember {

	String dwnload_file_path = "http://appseden.net/foundit/tracker/uploadimage/";  //www.boopathyraja.com/Android_UI_Design.zip";
	
	ArrayList<String> numberList; 
	SQLiteDatabase db ;
	Context context = null;
	String num,regId,lati,longi,name;
	public static String image_url;
	File dir;
	String myNumber;
	
	public AddUnknownMember(Context context,ArrayList<String> numberList){
		this.context = context;
		db = context.openOrCreateDatabase("tracker", 0, null);
		this.numberList = numberList;
		SharedPreferences settings = context.getSharedPreferences("pref", context.MODE_PRIVATE);
		myNumber = settings.getString("myNumber", "");
		File root = android.os.Environment.getExternalStorageDirectory();
		dir = new File(root.getAbsolutePath() + "/Com_tracker");
		if (dir.exists() == false) 
		{
			dir.mkdirs();
		}
		checkAndUpdate();
	}
	
	public void checkAndUpdate(){
		
		for(int i =0 ; i<numberList.size();i++){
			if(!numberList.get(i).equals(myNumber)){
		String query = "SELECT * FROM contacts WHERE phoneNumber='"+numberList.get(i)+"';";
		Cursor cursor = db.rawQuery(query, null);

		if(cursor.getCount()==0 ){

			new AsyncTaskRunner().execute(numberList.get(i));
		}
		
		}
		}
//		db.close();
	}
	
	private class AsyncTaskRunner extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();

		String res = "";
		JSONObject json;

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("number", args[0]));
			

			json = jParser.makeHttpRequest(
					"http://appseden.net/foundit/tracker/add_unknown_member.php", "POST",
					params);

			return res;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			
//			Toast.makeText(context, "Asynctask", Toast.LENGTH_LONG).show();

		}

		protected void onPostExecute(String result) {
			
			int success = 0;
			
			try {
				if(json!=null)
				success = json.getInt("success");
				if(success == 1){
				JSONArray products = json.getJSONArray("info");

				for (int i = 0; i < products.length(); i++) {

					JSONObject c = products.getJSONObject(i);
					name = c.getString("user_name");
					num = c.getString("phone");
					regId = c.getString("reg_id");
					image_url = c.getString("user_image");
					lati = c.getString("lati");
					longi = c.getString("longi");
					
				}
				
				new DownloadFileAsync().execute(image_url);
				
//				Picasso.with(context).load("http://www.tsquareltd.com/tracker/uploadimage/"+image_url).into(target);
				
				
//				Toast.makeText(context, success+"  done", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	
	private static Target target = new Target() {
		@Override
		public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					long time = System.currentTimeMillis();
					File file = new File(Environment.getExternalStorageDirectory().getPath() +"/Com_tracker/"+image_url+".jpg");
					if (file.exists ()) file.delete ();
					try
					{
						file.createNewFile();
						FileOutputStream ostream = new FileOutputStream(file);
						bitmap.compress(CompressFormat.JPEG, 75, ostream);
						ostream.close();

						
						
//						progressbar.setVisibility(View.GONE);
					} 
					catch (Exception e) 
					{
			//			Toast.makeText(context, "error ", 1000).show();
						e.printStackTrace();
					}

				}
			}).start();
		}

		@Override
		public void onBitmapFailed(Drawable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPrepareLoad(Drawable arg0) {
			// TODO Auto-generated method stub

		}	
	};
	
	
	@SuppressLint("SdCardPath")
	class DownloadFileAsync extends AsyncTask<String, String, String> 
	{
		byte[] defaultMarker;
		byte data[];
		int flag =0;
		String error;
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
//			showDialog(DIALOG_DOWNLOAD_PROGRESS);
//			Toast.makeText(context, image_url, Toast.LENGTH_LONG).show();
			
		}

		@Override
		protected String doInBackground(String... args) 
		{	
			Bitmap arrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_down);
			Bitmap defaultIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_icon);
			
			CreateMarker cmDefault = new CreateMarker(defaultIcon1,arrow);
			defaultMarker = Utility.getBytes(cmDefault.getMarker());
			
			
			String urlLink = dwnload_file_path+"/"+image_url;
			final String fileName = image_url+".jpg";
			if(!image_url.equals("")){
			try 
			{
				URL url = new URL(urlLink);
				URLConnection connection = url.openConnection();
				connection.connect();
				int fileLength = connection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(dir + "/" + fileName);

				final byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1)
				{
					total += count;
					//publishProgress(""+(int)((total*100)/fileLength));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
				
				Bitmap pic_bm = BitmapFactory.decodeFile(dir+"/"+fileName);
				CreateMarker cm = new CreateMarker(pic_bm,arrow);
				
				ContentValues cv = new ContentValues();
				cv.put("image", Utility.getBytes(pic_bm));
				cv.put("name", name);
				cv.put("phoneNumber", num);
				cv.put("reg_id", regId);
				cv.put("marker", Utility.getBytes(cm.getMarker()));
				cv.put("lati", lati);
				cv.put("longi", longi);
				db.insert("contacts", null, cv);
				flag = 1;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				error = e+"";
			}	
			}else{
				

		
			
			}
			return null;
		}

		

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String unused) 
		{
//			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);

			Toast.makeText(context, error, Toast.LENGTH_LONG).show();
			if(flag == 0){
			try{
				
				Bitmap defaultIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.man);
				byte[] n = Utility.getBytes(defaultIcon);
				ContentValues cv = new ContentValues();
				cv.put("image", n);
				cv.put("name", name);
				cv.put("phoneNumber", num);
				cv.put("reg_id", regId);
				cv.put("marker", defaultMarker);
				cv.put("lati", lati);
				cv.put("longi", longi);

				
				db.insert("contacts", null, cv);

				}catch(Exception e){
					e.printStackTrace();

					Toast.makeText(context, e+"", Toast.LENGTH_LONG).show();
				}
			}
			close();
		}
	}
	
	public void close(){
		db.close();
	}
}
