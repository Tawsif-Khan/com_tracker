package com.example.finalproject;

import java.util.ArrayList;

import com.nearby.welcome.ReloadData;
import com.tawsif.finalproject.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupListCustomAdapter extends BaseAdapter {

	public ArrayList<String> groupTitle, groupNameShow;
	public Context context;

	public GroupListCustomAdapter(Context context,
			ArrayList<String> groupTitle, ArrayList<String> groupNameshow) {
		this.context = context;
		this.groupTitle = groupTitle;
		this.groupNameShow = groupNameshow;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groupTitle.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ImageView im;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.user_list_row, parent, false);
		}
		Bitmap bm;
		String numbers = groupNameShow.get(position);
		String title = groupTitle.get(position);

		TextView userName = (TextView) convertView.findViewById(R.id.name);
		TextView numberView = (TextView) convertView.findViewById(R.id.number);
		im = (ImageView) convertView.findViewById(R.id.photo);

		im.setImageResource(R.drawable.group_list_icon);
	
		numberView.setText(numbers);
		userName.setText(title);

		return convertView;
	}

}
