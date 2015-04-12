package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nearby.welcome.ReloadData;
import com.nearby.welcome.StoreRoom;
import com.tawsif.finalproject.R;

public class GroupList extends SherlockFragment {

	public static ArrayList<String> user_image = new ArrayList<String>();
	public static ArrayList<String>	grp_id = new ArrayList<String>();
	public static ArrayList<String> group_name_show = new ArrayList<String>();
	public static ArrayList<String> empty = new ArrayList<String>();
	public static GroupListCustomAdapter adapter;
	public static ArrayList<String> group_title = new ArrayList<String>();
	ArrayList<Bitmap> bmList;
	static View rootView;
	private Context context = null;
	boolean statusOfGPS, statusOfInternet, statusOfNetwork;
	SQLiteDatabase db;
	TextView noGroups;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.group_list, container, false);
		context = container.getContext();
		
		noGroups = (TextView)rootView.findViewById(R.id.noGroup);
		noGroups.setVisibility(View.GONE);
		
		if(StoreRoom.userNameHash == null){
			ReloadData rd = new ReloadData(context);
			rd.getContactDetailsInHash();
//			StoreRoom sr = new StoreRoom();
		}
		
		grp_id.clear();
		group_name_show.clear();
		group_title.clear();
		user_image = new ArrayList<String>();
		bmList = new ArrayList<Bitmap>();
		user_image.clear();

		db = getActivity().openOrCreateDatabase("tracker", 0,
				null);
//		int i = 0;
		String s = "";
		String query = "SELECT * FROM groups";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String group_id = cursor.getString(cursor.getColumnIndex("id"));
				final String grp_title = cursor.getString(cursor
						.getColumnIndex("group_title"));

				group_title.add(grp_title);
				grp_id.add(group_id);
				user_image.add("");
				Cursor c = db.rawQuery("SELECT * FROM members WHERE group_id='"
						+ group_id + "';", null);
				if (c != null && c.getCount() > 0) {
					c.moveToFirst();
					s = "";
					do {
						String member_number = c.getString(c
								.getColumnIndex("member_number"));
						s = s + getName(member_number) + ", ";
					} while (c.moveToNext());
					
//					i++;
					group_name_show.add(s);
					empty.add("");

					
				}
			} while (cursor.moveToNext());
			
		}else{
			Toast.makeText(context, "no groups found", Toast.LENGTH_LONG).show();
			
			noGroups.setVisibility(View.VISIBLE);
		}
		db.close();
//		Toast.makeText(context, , Toast.LENGTH_LONG).show();
		bmList = null;
		adapter = new GroupListCustomAdapter(getActivity(), group_title,
				group_name_show);

		((ListView) rootView.findViewById(R.id.listView1)).setAdapter(adapter);

		((ListView) rootView.findViewById(R.id.listView1))
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String group_Id = grp_id.get(position);
						SQLiteDatabase db = getActivity().openOrCreateDatabase(
								"tracker", 0, null);
						String query = "SELECT * FROM groups WHERE id='"
								+ group_Id + "';";
						Cursor cursor = db.rawQuery(query, null);
						cursor.moveToFirst();
						String group_name = cursor.getString(cursor
								.getColumnIndex("group_name"));
						// String group_title =
						// cursor.getString(cursor.getColumnIndex("group_title"));

						goToMap(group_Id, group_name, position);

					}
				});
		return rootView;
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

	public void goToMap(String group_id, String group_name, int position) {
		try {
			// Loading map
			LocationManager manager = (LocationManager) getActivity()
					.getSystemService(context.LOCATION_SERVICE);
			statusOfGPS = manager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			statusOfNetwork = manager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			statusOfInternet = isConnectingToInternet();

			if ((statusOfGPS == false && statusOfNetwork == false)
					|| statusOfInternet == false) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView;
				AlertDialog.Builder builder;
				final AlertDialog alertDialog;
				Button btnOk = null;
				if (statusOfGPS == false && statusOfInternet == false
						&& statusOfNetwork == false) {
					promptsView = li.inflate(R.layout.int_loc_dialog, null);
					builder = new AlertDialog.Builder(context);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView.findViewById(R.id.btnOk);
				} else if (statusOfInternet == false
						&& (statusOfGPS == true || statusOfNetwork == true)) {
					promptsView = li.inflate(R.layout.loc_dialog, null);
					builder = new AlertDialog.Builder(context);
					builder.setView(promptsView);
					builder.setCancelable(false);
					alertDialog = builder.create();
					alertDialog.show();
					btnOk = (Button) promptsView.findViewById(R.id.btnOk);
				} else {
					promptsView = li.inflate(R.layout.loc_dialog, null);
					builder = new AlertDialog.Builder(context);
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
					Toast.makeText(getActivity().getApplicationContext(),
							"Please turn Network status of your device oN",
							Toast.LENGTH_LONG).show();
				} else {
					try {
						Intent intent = new Intent(context, Send.class);
						intent.putExtra("group_name", group_name);
						intent.putExtra("group_id", group_id);
						intent.putExtra("group_title",
								group_title.get(position));
						startActivity(intent);

					} catch (Exception e) {
						Toast.makeText(
								getActivity().getApplicationContext(),
								"Sorry an error occured duting the execution"
										+ e, Toast.LENGTH_LONG).show();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(
					getActivity().getApplicationContext(),
					"Internet status and GPS status of device can not be accesed",
					Toast.LENGTH_LONG).show();
		}
	}

	public static class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// Explicitly specify that GcmMessageHandler will handle the intent.
			ComponentName comp = new ComponentName(context.getPackageName(),
					GcmMessageHandler.class.getName());

			// Start the service, keeping the device awake while it is
			// launching.
			startWakefulService(context, (intent.setComponent(comp)));
			setResultCode(Activity.RESULT_OK);
		}
	};

	public static class GcmMessageHandler extends IntentService {

		String mes, text, RId, type, reg, group_name, n, numbers,
				group_title_rcv;
		ArrayList<String> reg_list = new ArrayList<String>();
		ArrayList<String> num_list = new ArrayList<String>();
		String active;
		private Handler handler;

		public GcmMessageHandler() {
			super("GcmMessageHandler");
		}

		@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			super.onCreate();
			handler = new Handler();
		}

		@Override
		protected void onHandleIntent(Intent intent) {
			Bundle extras = intent.getExtras();

			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

			String messageType = gcm.getMessageType(intent);

			type = extras.getString("type");
			mes = extras.getString("title");
			text = extras.getString("message");
			RId = extras.getString("Rid");

			if (type.equals("request")) {
				reg = extras.getString("regList");
				group_name = extras.getString("group_name");
				numbers = extras.getString("numbers");
				group_title_rcv = extras.getString("group_title");

				String s = "";
				for (int i = 0; i < reg.length(); i++) {
					if (reg.charAt(i) == '\n') {
						reg_list.add(s);
						s = "";
					} else
						s = s + reg.charAt(i);
				}
				n = "";
				s = "";
				for (int i = 0; i < numbers.length(); i++) {
					if (numbers.charAt(i) == '\n') {
						num_list.add(s);
						s = "";
						n = n + ", ";
					} else {
						s = s + numbers.charAt(i);
						n = n + numbers.charAt(i);
					}
				}
				
				notifyRequest();
			}
			
			Log.i("GCM",
					"Received : (" + messageType + ")  "
							+ extras.getString("title"));

			GcmBroadcastReceiver.completeWakefulIntent(intent);

		}

		public void notifyRequest() {
			handler.post(new Runnable() {
				public void run() {

					  new AddUnknownMember(getApplicationContext(), num_list);
					
					 SQLiteDatabase db = openOrCreateDatabase("tracker",
							MODE_PRIVATE, null);
					String query = "INSERT INTO groups (group_name,group_title) VALUES ('"
							+ group_name + "','" + group_title_rcv + "');";
					db.execSQL(query);

					query = "SELECT * FROM groups WHERE group_name='"
							+ group_name + "';";
					Cursor cursor = db.rawQuery(query, null);
					String group_id = "";
					if (cursor != null && cursor.getCount() > 0) {
						cursor.moveToFirst();
						group_id = cursor.getString(cursor.getColumnIndex("id"));
					}

					grp_id.add(group_id);
					group_name_show.add(n);
					group_title.add(group_title_rcv);
					user_image.add("");
					

					for (int i = 0; i < num_list.size(); i++) {
						query = "INSERT INTO members (group_id,member_number,reg_id) VALUES "
								+ "('"
								+ group_id
								+ "','"
								+ num_list.get(i)
								+ "','" + reg_list.get(i) + "');";
						db.execSQL(query);
						// Toast.makeText(getApplicationContext(),
						// num_list.get(i), Toast.LENGTH_SHORT).show();
					}
					db.close();

					if (getCurrentActivity().equals(
							"com.nearby.welcome.Optionalpage")) {
						adapter.notifyDataSetChanged();
						// ((ListView)
						// rootView.findViewById(R.id.listView1)).invalidateViews();
						// Toast.makeText(getApplicationContext(),
						// "new request", Toast.LENGTH_LONG).show();
					} else {
						
						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
								GcmMessageHandler.this)
								.setSmallIcon(R.drawable.group_list_icon)
								.setContentTitle(mes)
								.setContentText(text)
								.setSound(
										RingtoneManager
												.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
								.setAutoCancel(true);

						// Creates an explicit intent for an Activity in your
						// app
						Intent resultIntent = new Intent(
								GcmMessageHandler.this, Flash.class);
						resultIntent.putExtra("txt", text);
						resultIntent.putExtra("reg_id", RId);
						resultIntent.putExtra("group_name", group_name);
						resultIntent.putExtra("reg_list", reg_list);
						resultIntent.putExtra("list_flag", "list");
						resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_SINGLE_TOP);

						TaskStackBuilder stackBuilder = TaskStackBuilder
								.create(GcmMessageHandler.this);

						stackBuilder.addParentStack(Flash.class);

						stackBuilder.addNextIntent(resultIntent);
						PendingIntent resultPendingIntent = stackBuilder
								.getPendingIntent(0,
										PendingIntent.FLAG_CANCEL_CURRENT);
						mBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						// mId allows you to update the notification later on.
						mNotificationManager.notify(1, mBuilder.build());
					}
				}
			});

		}

		public String getCurrentActivity() {

			ActivityManager am = (ActivityManager) this
					.getSystemService(ACTIVITY_SERVICE);

			// get the info from the currently running task
			List<ActivityManager.RunningTaskInfo> taskInfo = am
					.getRunningTasks(1);

			Log.d("topActivity", "CURRENT Activity ::"
					+ taskInfo.get(0).topActivity.getClassName());

			ComponentName componentInfo = taskInfo.get(0).topActivity;
			return componentInfo.getClassName();
		}

	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}
}
