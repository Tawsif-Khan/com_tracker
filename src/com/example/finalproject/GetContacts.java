package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.finalproject.singlechat.ChatOnlyOne;
import com.nearby.welcome.ReloadData;
import com.nearby.welcome.StoreRoom;
import com.tawsif.finalproject.R;

public class GetContacts extends SherlockFragment {

	ListView list;
	ArrayList<byte[]> user_image;
	List<NameValuePair> params;
	Button add;
	SQLiteDatabase db;
	private Context context=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.get_contact, container, false);
		context=container.getContext();
		
		if(ReloadData.userName == null){
			ReloadData rd = new ReloadData(context);
			rd.getContactDetailsInHash();
			Toast.makeText(context, "Reloaded", Toast.LENGTH_LONG).show();
		}
//		Toast.makeText(context, ReloadData.userName.get("+01856707020"), Toast.LENGTH_LONG).show();
		list = (ListView) rootView.findViewById(R.id.listView1);
				
				CustomUserListAdapter adapter = new CustomUserListAdapter(
						getActivity(), ReloadData.final_number);
				list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent(context, ChatOnlyOne.class);
				intent.putExtra("contact_number", StoreRoom.userNumberList.get(position));
				intent.putExtra("member_reg_id", StoreRoom.userRegIdList.get(position));
				startActivity(intent);
			}
		});
		
		return rootView;
	}
	

}
