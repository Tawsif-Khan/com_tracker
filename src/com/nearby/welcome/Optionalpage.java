package com.nearby.welcome;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.example.Map.CreateMarker;
import com.example.finalproject.EditProfile;
import com.example.finalproject.GetContacts;
import com.example.finalproject.GroupList;
import com.example.finalproject.SelectMembers;
import com.example.finalproject.Utility;
import com.tawsif.finalproject.R;

public class Optionalpage extends SherlockFragmentActivity {
	
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	public static MenuListAdapter mMenuAdapter;
	
	
	
	Fragment getcontacts = new GetContacts();
	Fragment grouplist = new GroupList();
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	String[] title, subtitle;
	int[] icon;
	SQLiteDatabase db;
	
	NotificationManager mNotifyManager;
	NotificationCompat.Builder mBuilder;
	Boolean isInternetPresent = false;
	int total = 0, count;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	public static String name="";
	private float lastTranslate = 0.0f;
	byte[] image_path;
	ImageView profileImage;
	public static Activity opAc;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_main);
		 getSupportActionBar().setIcon(R.drawable.menu_icon_3);
		 
		// Toast.makeText(getApplicationContext(), StoreRoom.number.get(0), Toast.LENGTH_LONG).show();
		

		opAc = this;
		mBuilder = new NotificationCompat.Builder(this);
		
		SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE, null);
		
		String qrg = "SELECT * FROM profile;";
		Cursor crg = db.rawQuery(qrg, null);
		if (crg != null && crg.getCount() > 0) {
			crg.moveToFirst();
			do {
				image_path = crg.getBlob(crg
						.getColumnIndex("image_path"));
				name = crg.getString(crg
						.getColumnIndex("name"));
			} while (crg.moveToNext());
		}
		Bitmap profileImage = Utility.getPhoto(image_path);
		
		mTitle = mDrawerTitle = getTitle();
		title = new String[] { name,"Traffic Share","Nearby places","Contacts", "Create group", "Groups",
				"Profile","Luggage Guard" };
		subtitle = new String[] { "a","Share traffic condition","Shows nearby places","User Using app", "Create new group",
				"View all the groups", "Veiw and edit your acount","Helps to secure your luggage" };
		icon = new int[] { 0,R.drawable.pin_2,R.drawable.ic_launcher,R.drawable.contact1, R.drawable.add_new,
				R.drawable.group_list_icon, R.drawable.profile,R.drawable.ic_launcher };

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.listview_drawer);
		

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		
		mMenuAdapter = new MenuListAdapter(Optionalpage.this, title, subtitle,
				icon,profileImage);
		mDrawerList.setAdapter(mMenuAdapter);
		
		
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getSupportActionBar().setHomeButtonEnabled(true);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
//		getActionBar().setIcon(
//				new ColorDrawable(getResources().getColor(
//						android.R.color.transparent)));

		//////////////////////
		
		
		Field mDragger;
		try {
			mDragger = mDrawerLayout.getClass()
					.getDeclaredField("mLeftDragger");

			mDragger.setAccessible(true);
			ViewDragHelper draggerObj = (ViewDragHelper) mDragger
					.get(mDrawerLayout);

			Field mEdgeSize = draggerObj.getClass().getDeclaredField(
					"mEdgeSize");
			mEdgeSize.setAccessible(true);
			int edge = mEdgeSize.getInt(draggerObj);

			mEdgeSize.setInt(draggerObj, edge * 10);
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
				getSupportActionBar().setIcon(R.drawable.menu_icon_3);
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setIcon(R.drawable.menu_icon_2);
			}

			// ///////////also Slide the fragment//////////////
			FrameLayout frame = (FrameLayout) findViewById(R.id.content_frame);

			@SuppressLint("NewApi")
			public void onDrawerSlide(View drawerView, float slideOffset) {
				float moveFactor = (mDrawerList.getWidth() * slideOffset);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					frame.setTranslationX(moveFactor);
				} else {
					TranslateAnimation anim = new TranslateAnimation(
							lastTranslate, moveFactor, 0.0f, 0.0f);
					anim.setDuration(0);
					anim.setFillAfter(true);
					frame.startAnimation(anim);

					lastTranslate = moveFactor;
				}
			}

			// ////////////////////////////

		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			setTitle("Contacts");
			ft.replace(R.id.content_frame, grouplist);
			ft.commit();
			 ft = getSupportFragmentManager()
					.beginTransaction();
			setTitle("Contacts");
			ft.replace(R.id.content_frame, getcontacts);
			ft.commit();
		}
		String flag = getIntent().getExtras().getString("list_flag");
		if(flag.equals("list")){
			//Toast.makeText(getApplicationContext(), "list", 1000).show();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			setTitle(title[2]);
			Fragment grouplist = new GroupList();
			ft.replace(R.id.content_frame, grouplist);
			ft.commit();
			mDrawerList.setItemChecked(2, true);
			setTitle(title[2]);
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("Options");
		sub.add(0, 1, 0, "Refresh");
		sub.add(0, 2, 0, "Exit");
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Toast.makeText(getApplicationContext(), item.getItemId(), Toast.LENGTH_LONG).show();
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		switch (item.getItemId()) {
		case 1:
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());
			isInternetPresent = cd.isConnectingToInternet();

			if (isInternetPresent) {
//				FragmentTransaction ft = getSupportFragmentManager()
//						.beginTransaction();
//				setTitle(title[1]);
//				ft.replace(R.id.content_frame, getcontacts);
//				ft.commit();
//				mDrawerList.setItemChecked(1, true);
//				 setTitle(title[position]);
//				 mDrawerLayout.closeDrawer(mDrawerList);
				//new GetContacts().getNumbers();
				
			} else {
				Toast.makeText(getApplicationContext(),
						"Enable Data Connection", Toast.LENGTH_LONG).show();
			}
			break;

		case 2:

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (position) {
		case 1:
			Intent i = new Intent(Optionalpage.this,com.project.trafficshare.MainPostActivity.class);
			startActivity(i);
			break;
		case 2:
			Intent intentToYC = new Intent(Optionalpage.this,com.orora.yellowchittagong.MainActivity.class);
			startActivity(intentToYC);
			break;
		case 3:
			setTitle(title[position]);
			ft.replace(R.id.content_frame, getcontacts);

			break;
		case 4:
//			setTitle(title[position]);
//			ft.replace(R.id.content_frame, creategroup);
			Intent intent_to_add = new Intent(Optionalpage.this,SelectMembers.class);
			startActivity(intent_to_add);
			break;
		case 5:
			setTitle(title[position]);
			ft.replace(R.id.content_frame, grouplist);
			break;
		case 6:
//			setTitle(title[position]);
//			ft.replace(R.id.content_frame, );
			Intent intent = new Intent(Optionalpage.this,EditProfile.class);
			startActivity(intent);
			break;
		case 7:
			 
			Intent in = new Intent(Optionalpage.this,com.finalproject.luggageguard.SetBlueTooth.class);
			startActivity(in);
			
			break;
		
		}
		ft.commit();
		mDrawerList.setItemChecked(position, true);
		setTitle(title[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
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
		Toast.makeText(getApplicationContext(), "Use Options->Exit",
				Toast.LENGTH_LONG).show();
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