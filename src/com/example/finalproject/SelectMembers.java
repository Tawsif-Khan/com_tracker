package com.example.finalproject;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.nearby.welcome.ReloadData;
import com.tawsif.finalproject.R;

public class SelectMembers extends SherlockActivity {

	MyCustomAdapter dataAdapter = null;
	EditText group_title;
	Button done, cancel;
	FrameLayout buttons;
	ArrayList<String> selectedUser_image, user_image, name, number,
			selectedMembers, reg_id, selectedReg_id, selectedNumber;
//	private Context context = null;
	View rootView;
	SQLiteDatabase db;
	ArrayList<Bitmap> bmList;
	
	public ArrayList<Bitmap> selectedBmList;
//	ArrayList<Boolean> check = new ArrayList<Boolean>();
	int[] check ;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		rootView = inflater.inflate(R.layout.select_members, container, false);
//		context = container.getContext();
		setContentView(R.layout.select_members);
		group_title = (EditText) findViewById(R.id.group_title);
		buttons = (FrameLayout) findViewById(R.id.buttons);
		buttons.setVisibility(View.GONE);
		
		name = new ArrayList<String>();
		number = new ArrayList<String>();
		reg_id = new ArrayList<String>();
		user_image = new ArrayList<String>();
		bmList = new ArrayList<Bitmap>();
		selectedBmList = new ArrayList<Bitmap>();

		selectedMembers = new ArrayList<String>();
		selectedReg_id = new ArrayList<String>();
		selectedNumber = new ArrayList<String>();
		selectedUser_image = new ArrayList<String>();

		String query = "SELECT * FROM contacts";

		db = openOrCreateDatabase("tracker", 0, null);
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				String names = cursor.getString(cursor.getColumnIndex("name"));
				String Number = cursor.getString(cursor
						.getColumnIndex("phoneNumber"));
				String reg_ID = cursor.getString(cursor
						.getColumnIndex("reg_id"));
				
//				byte[] imageBlob = null;
//				imageBlob  = cursor.getBlob(cursor.getColumnIndex("image"));
				//bmList.add(Utility.getPhoto(imageBlob));
				name.add(names);
				number.add(Number);
				reg_id.add(reg_ID);
//				user_image.add(image_url);
			} while (cursor.moveToNext());
			db.close();
		}

		done = (Button) findViewById(R.id.donebtn);
		cancel = (Button) findViewById(R.id.cancelbtn);
		
//		done.setActivated(false);
		
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String g_title = group_title.getText().toString();
				// if(g_title.equals(""))
				Intent intent = new Intent(SelectMembers.this, GroupCreated.class);
				intent.putExtra("selected_members", selectedMembers);
				intent.putExtra("selected_number", selectedNumber);
				intent.putExtra("selected_reg_id", selectedReg_id);
				intent.putExtra("selected_user_image", selectedUser_image);
				//intent.putExtra("selected_image", selectedBmList);
				intent.putExtra("group_title", g_title);
				startActivity(intent);
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// Generate list View from ArrayList
		displayListView();

	}

	private void displayListView() {
		
		check = new int[number.size()];
		
		for (int i = 0; i < number.size(); i++) {
			check[i] = 0;
		}
		
		// Array list of contacts
		ArrayList<ContactClass> contactList = new ArrayList<ContactClass>();
		for (int i = 0; i < name.size(); i++) {
			ContactClass contact = new ContactClass(name.get(i), number.get(i),
					i, false, ReloadData.bmList.get(i));
			contactList.add(contact);
		}

		// create an ArrayAdaptar from the String Array
		dataAdapter = new MyCustomAdapter(SelectMembers.this, R.layout.row,
				contactList);
		ListView listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
	
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox cb = (CheckBox)view;
				if(cb.isChecked())
				check[position] = 1;
				else
					check[position] = 0;
				Toast.makeText(getApplicationContext(),name.get(position) , Toast.LENGTH_LONG).show();
			
			}
		});

	}

	private class MyCustomAdapter extends ArrayAdapter<ContactClass> {

		private ArrayList<ContactClass> countryList;
		

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<ContactClass> countryList) {
			super(context, textViewResourceId, countryList);
			this.countryList = new ArrayList<ContactClass>();
			this.countryList.addAll(countryList);

			
		}

		private class ViewHolder {
			TextView userName;
			TextView number;
			CheckBox checkBox;
			ImageView im;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.select_contact_row, null);

				holder = new ViewHolder();
				holder.userName = (TextView) convertView
						.findViewById(R.id.user_name);
				holder.number = (TextView) convertView
						.findViewById(R.id.number);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				holder.im = (ImageView) convertView.findViewById(R.id.photo);
				convertView.setTag(holder);

				holder.checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						ContactClass contact = (ContactClass) cb.getTag();

						if (cb.isChecked()) {
							check[contact.getPos()] = 1;
						} else {
									 check[contact.getPos()] = 0;
						}

						 
						
						selectedMembers.clear();
						selectedNumber.clear();
						selectedReg_id.clear();
						selectedBmList.clear();
						selectedUser_image.clear();
						int f = 0;
						for (int i = 0; i < number.size(); i++) {
							if (check[i] == 1) {
								selectedMembers.add(name.get(i));
								selectedReg_id.add(reg_id.get(i));
								selectedNumber.add(number.get(i));
								selectedUser_image.add(i+"");
								f=1;
							}
						}
						if(f==1){
							buttons.setVisibility(View.VISIBLE);
						}else
							buttons.setVisibility(View.GONE);
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ContactClass contact = countryList.get(position);
			holder.userName.setText(contact.getCode());
			holder.number.setText(contact.getName());
			holder.checkBox.setChecked((check[contact.getPos()]== 1)?true:false);
			holder.checkBox.setTag(contact);
			holder.im.setImageBitmap(contact.getImage());

				return convertView;

		}

	}

}