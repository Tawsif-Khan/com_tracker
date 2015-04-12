package com.example.Map;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nearby.welcome.ReloadData;
import com.tawsif.finalproject.R;

public class CustomDistanceListAdapter extends BaseAdapter{
	
	ArrayList<String> nameList;
	HashMap<String, Float> distanceHash;
	Context context;
	
	
	public CustomDistanceListAdapter(Context context,ArrayList<String> nameList,HashMap<String, Float> distanceHash){
		
		this.context = context;
		this.nameList = nameList;
		this.distanceHash = distanceHash;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nameList.size();
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
	public View getView(int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ImageView image;
		TextView nameView,distanceView;
		
		convertView =  LayoutInflater.from(context).inflate(
				R.layout.distance_row, parent, false);
		image = (ImageView)convertView.findViewById(R.id.image);
		nameView = (TextView)convertView.findViewById(R.id.name);
		distanceView = (TextView)convertView.findViewById(R.id.distance);
		
		image.setImageBitmap(ReloadData.userImage.get(nameList.get(pos)));
		nameView.setText(ReloadData.userName.get(nameList.get(pos)));
		distanceView.setText(distanceHash.get(nameList.get(pos))+"");
		
		return convertView;
	}

}
