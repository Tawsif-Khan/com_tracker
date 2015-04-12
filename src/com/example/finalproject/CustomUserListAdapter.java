package com.example.finalproject;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nearby.welcome.ReloadData;
import com.tawsif.finalproject.R;

public class CustomUserListAdapter extends BaseAdapter {

	ArrayList<String> name, num; 
	ArrayList<byte[]> user_image;
	ArrayList<String> date;
	ArrayList<Bitmap> bmList;
	ArrayList<Image> image;
	ImageView im;
	static String image_url = "";
	private Activity context;
	int flag;

	public CustomUserListAdapter(Activity context, ArrayList<String> num) {

		// TODO Auto-generated constructor stub
		this.context = context;
		this.num = num;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return num.size();
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

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.user_list_row, parent, false);
		}

		String numberS = num.get(position);


		TextView userName = (TextView) convertView.findViewById(R.id.name);
		TextView number = (TextView) convertView.findViewById(R.id.number);
		im = (ImageView) convertView.findViewById(R.id.photo);
		

			im.setImageBitmap(ReloadData.userImage.get(numberS));

			number.setText(numberS);
			userName.setText(ReloadData.userName.get(numberS));

		return convertView;
	}



}
