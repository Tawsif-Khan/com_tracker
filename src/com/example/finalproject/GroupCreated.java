package com.example.finalproject;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.nearby.welcome.Optionalpage;
import com.nearby.welcome.ReloadData;
import com.tawsif.finalproject.R;

public class GroupCreated extends SherlockFragmentActivity {

	ArrayList<String> selectedReg_id, selectedNumber, selectedMembers,
			user_image;
	String numbers, Rid, number, group_title;
	ArrayList<Bitmap> bmList;
	Fragment grouplist = new GroupList();
	TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_created);

		titleView = (TextView)findViewById(R.id.head);
		
		SharedPreferences settings = getSharedPreferences("prefs", 0);
		Rid = settings.getString("regId", "");
		number = settings.getString("myNumber", "");

		selectedMembers = new ArrayList<String>();
		selectedReg_id = new ArrayList<String>();
		selectedNumber = new ArrayList<String>();
		user_image = new ArrayList<String>();
		bmList = new ArrayList<Bitmap>();

		selectedMembers = (ArrayList<String>) getIntent().getSerializableExtra(
				"selected_members");
		selectedNumber = (ArrayList<String>) getIntent().getSerializableExtra(
				"selected_number");
		selectedReg_id = (ArrayList<String>) getIntent().getSerializableExtra(
				"selected_reg_id");
		user_image = (ArrayList<String>) getIntent().getSerializableExtra(
				"selected_user_image");
//		bmList = (ArrayList<Bitmap>) getIntent().getSerializableExtra(
//				"selected_image");
		group_title = getIntent().getExtras().getString("group_title");

		titleView.setText(group_title);
		
		for(int i=0;i<user_image.size();i++){
		bmList.add(ReloadData.bmList.get(Integer.parseInt(user_image.get(i))));
		}

		CustomUserListAdapter adapter = new CustomUserListAdapter(
				GroupCreated.this, selectedNumber);
		((ListView) findViewById(R.id.list)).setAdapter(adapter);

		String uuid = UUID.randomUUID().toString();

		SQLiteDatabase db = openOrCreateDatabase("tracker", MODE_PRIVATE, null);
		String query = "INSERT INTO groups (group_name,group_title) VALUES ('"
				+ uuid + "','" + group_title + "');";
		db.execSQL(query);

		query = "SELECT * FROM groups WHERE group_name='" + uuid + "';";
		Cursor cursor = db.rawQuery(query, null);
		String group_id = "";
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			group_id = cursor.getString(cursor.getColumnIndex("id"));
		}
		for (int i = 0; i < selectedMembers.size(); i++) {
			query = "INSERT INTO members (group_id,member_number,reg_id) VALUES "
					+ "('"
					+ group_id
					+ "','"
					+ selectedNumber.get(i)
					+ "','"
					+ selectedReg_id.get(i) + "');";
			db.execSQL(query);
			// Toast.makeText(getApplicationContext(), selectedReg_id.get(i),
			// Toast.LENGTH_SHORT).show();
		}
		query = "INSERT INTO members (group_id,member_number,reg_id) VALUES "
				+ "('" + group_id + "','" + number + "','" + Rid + "');";
		db.execSQL(query);
		db.close();

		numbers = number + "\n";
		for (int i = 0; i < selectedNumber.size(); i++) {

			numbers = numbers + selectedNumber.get(i) + "\n";

		}

		sendRequest(selectedReg_id, uuid);

		((Button) findViewById(R.id.chatroom))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(GroupCreated.this,
								Optionalpage.class);
						Optionalpage.opAc.finish();
						intent.putExtra("list_flag", "list");
						startActivity(intent);
						finish();
						// FragmentTransaction ft =
						// getSupportFragmentManager().beginTransaction();
						// setTitle("Group List");
						// ft.replace(R.id.content_frame, grouplist);
						// ft.commit();
					}
				});
	}

	public void sendRequest(ArrayList<String> reg_ids, String group_name) {

		// if you want more devices to receive this message just add their regID
		// in this arrayList
		//Toast.makeText(getApplicationContext(), numbers, Toast.LENGTH_LONG).show();
		GCMRequestSender reqsender = new GCMRequestSender();
		reqsender.send(reqsender.createContent("Added by " + number,
				"New group", reg_ids, Rid, group_name, numbers, group_title));
	}
}
