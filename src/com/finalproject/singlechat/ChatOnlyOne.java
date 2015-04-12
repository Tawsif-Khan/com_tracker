package com.finalproject.singlechat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.finalproject.AddUnknownMember;
import com.example.finalproject.Flash;
import com.example.finalproject.GroupList;
import com.example.finalproject.MsgHolder;
import com.example.finalproject.MultipartEntity;
import com.finalproject.popup.master.menu.ActionItem;
import com.finalproject.popup.master.menu.QuickAction;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nearby.welcome.MenuListAdapter;
import com.nearby.welcome.ReloadData;
import com.nearby.welcome.StoreRoom;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tawsif.finalproject.R;

public class ChatOnlyOne extends SherlockFragmentActivity {
	public static DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	public static ActionBarDrawerToggle mDrawerToggle;
	MenuListAdapter mMenuAdapter;

	OnlyOneInMap onlyOneInMap = new OnlyOneInMap();
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	String[] title, subtitle;
	int[] icon;
	long time;
	NotificationManager mNotifyManager;
	NotificationCompat.Builder mBuilder;
	Boolean isInternetPresent = false;
	int total = 0, count;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	public static int SINGLE_MSG = 0;
	private float lastTranslate = 0.0f;

	private static int IMAGE_PICKER_SELECT = 1, TAKE_PICTURE = 0;
	private ImageButton mTakePhoto, mSelectPhoto;
	private Bitmap cameraBitmap, scaled1;
	long timename;

	static ListView msgList;
	Button  chat;
	ImageButton send;
	EditText typeText;
	public static String group_name = "", group_id = "",member_reg_id="", group_title;
	public static String tmp_grp_name = "";
	static String my_reg, my_number;
	static ArrayList<MsgHolder> msg = new ArrayList<MsgHolder>();
	static ArrayList<MsgHolder> sender_reg = new ArrayList<MsgHolder>();
	public ArrayList<String> current_members_reg_id = new ArrayList<String>();
	Bitmap bm = null;
	
	static OnlyChatCustomeAdapter adapter;
	Image image;
	Context context;
	boolean statusOfGPS, statusOfInternet, statusOfNetwork;
	static String picturePath;
	public static LinearLayout mdrawer;
	private static final int GALLERY = 1;
	private static final int CAMERA = 2;
	public static Activity chatOnlyOne = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);
		getSupportActionBar().hide();

		mBuilder = new NotificationCompat.Builder(this);
		mdrawer = (LinearLayout) findViewById(R.id.mdrawer);
		
		chatOnlyOne = this;
		if(StoreRoom.userNameHash == null){
			ReloadData rd = new ReloadData(ChatOnlyOne.this);
			rd.getContactDetailsInHash();
			StoreRoom sr = new StoreRoom();
		}
		
		mTitle = mDrawerTitle = getTitle();

		my_reg = "";
		my_number = "";
		group_id = "";
		group_name = "";
		group_title = "";
		msg.clear();
		sender_reg.clear();

		SharedPreferences settings = getSharedPreferences("prefs", 0);
		my_reg = settings.getString("regId", "");
		my_number = settings.getString("myNumber", "");

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		msgList = (ListView) findViewById(R.id.listView1);
		typeText = (EditText) findViewById(R.id.msg);

		group_name = getIntent().getExtras().getString("contact_number");
		member_reg_id = getIntent().getExtras().getString("member_reg_id");

//		Toast.makeText(getApplicationContext(), StoreRoom.regIdHash.get(group_name), Toast.LENGTH_LONG).show();
		tmp_grp_name = group_name;

		String last_msg = getIntent().getExtras().getString("txt");
		final String regisId = getIntent().getExtras().getString("regId");

		SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE, null);

		current_members_reg_id.add(my_reg);
		current_members_reg_id.add(member_reg_id);
		
		
		String qry = "SELECT * FROM chat WHERE group_id='" + group_name + "';";
		Cursor c = db.rawQuery(qry, null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			do {
				String msgText = c.getString(c.getColumnIndex("msg"));
				String member_id = c.getString(c.getColumnIndex("member_id"));
				String msg_time = c.getString(c.getColumnIndex("msg_time"));
				String sent = c.getString(c.getColumnIndex("sent"));
				String type = c.getString(c.getColumnIndex("msg_type"));
				String image_in_phone = "";
				image_in_phone = c.getString(c.getColumnIndex("image_in_phone"));
				
				bm = null;
					
//				Toast.makeText(getApplicationContext(), msgText, 1).show();
					try{
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						
						
						if (!my_number.equals(member_id))
							bm = BitmapFactory.decodeFile(image_in_phone);
						else
							bm = BitmapFactory.decodeFile(msgText);
						
//						bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
						
					}catch(Exception e){
						Toast.makeText(getApplicationContext(), "Something is wrong", 1).show();
					}
				
						
						MsgHolder holder = new MsgHolder(group_name,msgText,
								member_id, my_number, msg_time, type, bm, 1);
						if (sent.equals("1"))
							holder.setSent(1);
						else
							holder.setSent(0);
						msg.add(holder);
				
			} while (c.moveToNext());
		}

		adapter = new OnlyChatCustomeAdapter(ChatOnlyOne.this, msg);
		msgList.setAdapter(adapter);
		msgList.setSelection(msg.size() - 1);

		((ImageButton) findViewById(R.id.send))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ArrayList<String> regIds = new ArrayList<String>();
						// regIds.add(regisId);
						String text = typeText.getText().toString();
						typeText.setText("");
						Calendar now = Calendar.getInstance();
						int H = now.get(now.HOUR);
						int M = now.get(now.MINUTE);
						int AM_PM = now.get(now.AM_PM);
						String msg_time = H + ":" + M + " "
								+ ((AM_PM == 0) ? "AM" : "PM");

						MsgHolder holder = new MsgHolder("",text, my_number, my_number,
								msg_time, "message", null, 1);
						holder.setSent(0);
						msg.add(holder);
						adapter.notifyDataSetChanged();
						msgList.setSelection(msg.size() - 1);

						SQLiteDatabase db = openOrCreateDatabase("tracker",
								MODE_PRIVATE, null);
						
							String member_id = "";
						
						String query = "INSERT INTO chat (group_id,member_id,msg,msg_time,sent,msg_type) VALUES ('"
								+ group_name
								+ "','"
								+ my_number
								+ "','"
								+ text
								+ "','" + msg_time + "','0','message');";
						db.execSQL(query);
						query = "SELECT id FROM chat";
						Cursor cr = db.rawQuery(query, null);
						int id = cr.getCount();

						db.close();
						// GcmMessageHandler.storeInDatabase(Rid,text,group_name);
						// if you want more devices to receive this message just
						// add their regID
						// in this arrayList
						GCMOnlyOneMessageSender sender = new GCMOnlyOneMessageSender();
						sender.send(sender.createContent(my_number, text,
								current_members_reg_id, my_reg, image,
								group_name, group_title, msg_time, id,
								msg.size() - 1));
					}
				});

		ActionItem menu1 = new ActionItem(GALLERY, "Gallery", getResources()
				.getDrawable(R.drawable.gellery));
		ActionItem menu2 = new ActionItem(CAMERA, "Camera", getResources()
				.getDrawable(R.drawable.camera));

		menu1.setSticky(true);
		menu2.setSticky(true);

		final QuickAction quickAction = new QuickAction(this,
				QuickAction.VERTICAL);
		quickAction.addActionItem(menu1);
		quickAction.addActionItem(menu2);

		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (actionId == GALLERY) {
							Intent i = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(i, IMAGE_PICKER_SELECT);

						} else if (actionId == CAMERA) {
							takePhoto();

							
						}

					}
				});

		ImageButton add = (ImageButton) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				quickAction.show(v);

			}
		});

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.END);

		// mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		

		// ///////////////////////////////////

		Field mDragger, bDragger;
		try {
			mDragger = mDrawerLayout.getClass().getDeclaredField(
					"mRightDragger");
			// bDragger = positionMap.chatbtn.getClass()
			// .getDeclaredField("mLeftDragger");

			mDragger.setAccessible(true);
			// bDragger.setAccessible(true);

			ViewDragHelper draggerObj = (ViewDragHelper) mDragger
					.get(mDrawerLayout);
			// ViewDragHelper btndraggerObj = (ViewDragHelper) bDragger
			// .get(positionMap.chatbtn);

			Field mEdgeSize = draggerObj.getClass().getDeclaredField(
					"mEdgeSize");
			// Field btnEdgeSize = btndraggerObj.getClass().getDeclaredField(
			// "mEdgeSize");

			mEdgeSize.setAccessible(true);
			// btnEdgeSize.setAccessible(true);
			int edge = mEdgeSize.getInt(draggerObj);
			// int btnedge = btnEdgeSize.getInt(btndraggerObj);

			mEdgeSize.setInt(draggerObj, edge * 2);
			// btnEdgeSize.setInt(draggerObj, edge * 2);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// mRightDragger for right obviously
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// optimal value as for me, you may set any constant in

		// ////////////////////////////////

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawers, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				// getSupportActionBar().show();

			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				super.onDrawerOpened(drawerView);
			}

			public boolean onOptionsItemSelected(MenuItem item) {
				if (item != null && item.getItemId() == android.R.id.home) {
					if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
						mDrawerLayout.closeDrawer(Gravity.RIGHT);
					} else {
						mDrawerLayout.openDrawer(Gravity.RIGHT);
					}
				}
				return false;
			}

			// Button btn = (Button) findViewById(R.id.chatbtn);

			@SuppressLint("NewApi")
			public void onDrawerSlide(View drawerView, float slideOffset) {
				float moveFactor = (-1) * (mdrawer.getWidth() * slideOffset);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

					onlyOneInMap.chatbtn.setTranslationX(moveFactor);
					// Toast.makeText(getApplicationContext(), moveFactor+"",
					// 1000).show();

				} else {
					TranslateAnimation anim = new TranslateAnimation(
							moveFactor, lastTranslate, 0.0f, 0.0f);
					anim.setDuration(2000);
					anim.setFillAfter(true);
					onlyOneInMap.chatbtn.startAnimation(anim);
					Toast.makeText(getApplicationContext(), moveFactor + "",
							1000).show();
					moveFactor = lastTranslate;
				}
			}

		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			setTitle("Map");
			ft.replace(R.id.content_frame, onlyOneInMap);
			ft.commit();
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

		String position, mes, text, RId, type, reg, image_url, group_name_rcv,
				group_title, msg_time, msg_id;
		ArrayList<String> reg_list = new ArrayList<String>();
		String active, image_path = "";
		static long time;
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
			// The getMessageType() intent parameter must be the intent you
			// received
			// in your BroadcastReceiver.
			String messageType = gcm.getMessageType(intent);

			type = extras.getString("type");
			mes = extras.getString("title");
			text = extras.getString("message");
			RId = extras.getString("Rid");
			group_name_rcv = extras.getString("group_name");
			group_title = extras.getString("group_title");
			msg_time = extras.getString("msg_time");
			msg_id = extras.getString("msg_id");
			position = extras.getString("list_position");

			if (type.equals("messageSingle"))
				notifyMessage();
			else if (type.equals("imageSingle")) {
				image_url = extras.getString("image_url");
				notifyMessage();
			}

			Log.i("GCM",
					"Received : (" + messageType + ")  "
							+ extras.getString("title"));

			GcmBroadcastReceiver.completeWakefulIntent(intent);

		}

		public void insertToList(String text, String member_id) {

			if (!RId.equals(my_reg)) {
				// Bitmap bm = BitmapFactory.decodeFile(image_path);
				msg.add(new MsgHolder(mes,text, member_id, my_reg, msg_time, type,
						null, 0));
				adapter.notifyDataSetChanged();
				msgList.setSelection(msg.size() - 1);
			}
			storeInDatabase(member_id, text, mes);
		}

		public void storeInDatabase(String Rid, String text, String group_name) {

			SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE,
					null);
//			Toast.makeText(getApplicationContext(), group_name+" ,"+mes+" "+group_title, 1000).show();
			String query = "";
			
			if (Rid.equals(my_reg)) {
				query = "UPDATE chat SET sent='1' WHERE id='" + msg_id + "';";

				adapter.setSend(Integer.parseInt(position));
				adapter.notifyDataSetChanged();
				msgList.setSelection(msg.size() - 1);
			} else {
				time = 0;
				time = System.currentTimeMillis();
				image_path = "";
				if (type.equals("imageSingle")) {
					image_path = Environment.getExternalStorageDirectory()
							.getPath() + "/Com_tracker/" + time + ".jpg";
					Picasso.with(getApplicationContext())
							.load("http://appseden.net/foundit/tracker/uploadimage/"
									+ image_url).into(target);
				}
//				Toast.makeText(getApplicationContext(), image_path, 1).show();
				query = "INSERT INTO chat (group_id,member_id,msg,msg_time,sent,msg_type,image_in_phone) VALUES ('"
						+ group_name
						+ "','"
						+ mes
						+ "','"
						+ text
						+ "','"
						+ msg_time
						+ "','0','"
						+ type
						+ "','"
						+ image_path
						+ "');";
			}

			db.execSQL(query);
			db.close();
		}

		public static Target target = new Target() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap,
					Picasso.LoadedFrom from) {
				new Thread(new Runnable() {
					@Override
					public void run() {

						File file = new File(Environment
								.getExternalStorageDirectory().getPath()
								+ "/Com_tracker/" + time + ".jpg");
						if (file.exists())
							file.delete();
						try {
							file.createNewFile();
							FileOutputStream ostream = new FileOutputStream(
									file);
							bitmap.compress(CompressFormat.JPEG, 100, ostream);
							ostream.close();
							// Toast.makeText(, "image saved", 1000).show();

						} catch (Exception e) {
							// Toast.makeText(context, "error ", 1000).show();
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

		public void notifyMessage() {
			handler.post(new Runnable() {
				public void run() {
					
					ArrayList<String> number = new ArrayList<String>();
					number.add(mes);
					AddUnknownMember add = new AddUnknownMember(getApplicationContext(), number);
					
					
					currentRunningActivity();
					if (active.equals("com.finalproject.singlechat.ChatOnlyOne")
							&& mes.equals(group_name)) {
						if (type.equals("imageSingle")) {

							insertToList(image_url, RId);

						} else
							insertToList(text, RId);
					} else {

						storeInDatabase(RId, text, mes);

						if (!RId.equals(my_reg)) {
							if(StoreRoom.userNameHash == null){
								ReloadData rd = new ReloadData(getApplicationContext());
								rd.getContactDetailsInHash();
//								StoreRoom sr = new StoreRoom();
//								Toast.makeText(getApplicationContext(), "in it", Toast.LENGTH_LONG).show();
							}
							ReloadData rd = new ReloadData(getApplicationContext());
							String name = rd.getName(mes);
							SINGLE_MSG = 1;
							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
									GcmMessageHandler.this)
									.setSmallIcon(R.drawable.comment)
									.setLargeIcon(ReloadData.userImage.get(mes))
									.setContentTitle(name)
									.setContentText(text)
									.setSound(
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
									.setAutoCancel(true);

							// // Creates an explicit intent for an Activity in
							// your
							// app
							Intent resultIntent = new Intent(
									GcmMessageHandler.this, ChatOnlyOne.class);
							resultIntent.putExtra("txt", text);
							resultIntent.putExtra("member_reg_id", RId);
							resultIntent.putExtra("contact_number", mes);
							resultIntent.putExtra("flag", "single");
							resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_SINGLE_TOP);

							TaskStackBuilder stackBuilder = TaskStackBuilder
									.create(GcmMessageHandler.this);

							stackBuilder.addParentStack(ChatOnlyOne.class);

							stackBuilder.addNextIntent(resultIntent);

							PendingIntent pendingintent = PendingIntent
									.getActivity(GcmMessageHandler.this, 0,
											resultIntent, 0);

							PendingIntent resultPendingIntent = stackBuilder
									.getPendingIntent(0,
											PendingIntent.FLAG_UPDATE_CURRENT);

							mBuilder.setContentIntent(resultPendingIntent);
							NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
							// // mId allows you to update the notification
							// later
							// on.
							mNotificationManager.notify(0, mBuilder.build());
						}
					}

				}
			});

		}

		public void currentRunningActivity() {
			ActivityManager am = (ActivityManager) this
					.getSystemService(ACTIVITY_SERVICE);

			// get the info from the currently running task
			List<ActivityManager.RunningTaskInfo> taskInfo = am
					.getRunningTasks(1);

			Log.d("topActivity", "CURRENT Activity ::"
					+ taskInfo.get(0).topActivity.getClassName());

			ComponentName componentInfo = taskInfo.get(0).topActivity;
			active = componentInfo.getClassName();
		}

	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

	// image upload code//

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
					Uri.fromFile(getTempFile(ChatOnlyOne.this)));
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
			
			
			Bitmap bitmap1 = getBitmapFromCameraData(data, ChatOnlyOne.this);

			// this line will resize your image
			// Bitmap bitmap=getScaledBitmap(bitmap1, 1100, 1100);

			int nh = (int) (bitmap1.getHeight() * (512.0 / bitmap1.getWidth()));
			scaled1 = Bitmap.createScaledBitmap(bitmap1, 512, nh, true);

			timename = System.currentTimeMillis();
			new UploadTask().execute(scaled1);

		}
		if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK
				&& data != null) {
			// get bundle
			Bundle extras = data.getExtras();
//			((Bitmap)data.getExtras().get("data")).compress(Bitmap.CompressFormat.PNG, 100, stream);
			
			// get bitmap
			cameraBitmap = getBitmapFromCameraData(data, ChatOnlyOne.this); //(Bitmap) extras.get("data");
			int nh = (int) (cameraBitmap.getHeight() * (512.0 / cameraBitmap
					.getWidth()));
			scaled1 = Bitmap.createScaledBitmap(cameraBitmap, 512, nh, true);

			new UploadTask().execute(scaled1);

		}
	}

	public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
		
		Uri selectedImage = data.getData();
		
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		picturePath = cursor.getString(columnIndex);
		cursor.close();
		return BitmapFactory.decodeFile(picturePath);
	}

	private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

		String url, msg_time;

		protected Void doInBackground(Bitmap... bitmaps) {
			if (bitmaps[0] == null)
				return null;

			Bitmap bitmap = bitmaps[0];
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			InputStream in = new ByteArrayInputStream(stream.toByteArray());

			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {

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
			// timename=System.currentTimeMillis();
			url = timename + ".jpg";

			Calendar now = Calendar.getInstance();
			int H = now.get(now.HOUR);
			int M = now.get(now.MINUTE);
			int AM_PM = now.get(now.AM_PM);
			msg_time = H + ":" + M + " " + ((AM_PM == 0) ? "AM" : "PM");
//			Bitmap bm = BitmapFactory.decodeFile(picturePath);
			MsgHolder holder = new MsgHolder("",picturePath, my_number, my_number,
					msg_time, "imageSingle", scaled1, 1);
			holder.setSent(0);
			msg.add(holder);

			adapter.notifyDataSetChanged();
			msgList.setSelection(msg.size() - 1);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE,
					null);
//			Toast.makeText(getApplicationContext(), picturePath, 1).show();
			String query = "INSERT INTO chat (group_id,member_id,msg,msg_time,sent,msg_type) VALUES ('"
					+ group_name
					+ "','"
					+ my_number
					+ "','"
					+ picturePath
					+ "','" + msg_time + "','0','imageSingle');";
			db.execSQL(query);
			query = "SELECT id FROM chat";
			Cursor cr = db.rawQuery(query, null);
			int id = cr.getCount();

			db.close();

			ImageSenderOnly sender = new ImageSenderOnly();
			sender.send(sender.createContent(my_number, url, current_members_reg_id,
					my_reg, image, group_name, group_title, msg_time, id,
					msg.size() - 1));

			Toast.makeText(ChatOnlyOne.this, "Image uploaded", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	public void onBackPressed() {
		// turnOffGPS();
		// adapter.clear();
//		bm.recycle();
//		bm = null;
//		System.gc();
		
//		onlyOneInMap.getActivity().finish();
		this.finish();
	}

	public void turnOnGPS() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		sendBroadcast(intent);

	}

	public void turnOffGPS() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", false);
		sendBroadcast(intent);
	}

	public void turnGPSOn() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		sendBroadcast(intent);

		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);

		}
	}

	// automatic turn off the gps
	public void turnGPSOff() {
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("HAve fun");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;

		default:
			return null;
		}
	}

}