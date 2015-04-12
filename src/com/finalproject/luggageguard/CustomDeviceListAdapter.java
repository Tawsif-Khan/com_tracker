package com.finalproject.luggageguard;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tawsif.finalproject.R;

public class CustomDeviceListAdapter extends BaseAdapter {

	ArrayList<String> deviceName = new ArrayList<String>();

	Activity context;

	public CustomDeviceListAdapter(Activity context, ArrayList<String> deviceName) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.deviceName = deviceName;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		TextView nameView;
		convertView = LayoutInflater.from(context).inflate(
				R.layout.bluetooth_device_list_row, parent, false);

		nameView = (TextView) convertView.findViewById(R.id.deviceName);
		nameView.setText(deviceName.get(position));

		return convertView;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.deviceName.size();
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

}
