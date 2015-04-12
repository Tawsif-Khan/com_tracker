package com.example.finalproject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nearby.welcome.ReloadData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tawsif.finalproject.R;

public class CustomAdapter extends ArrayAdapter<MsgHolder> {

	ArrayList<String> msg;
	ArrayList<String> date;
	ArrayList<Image> image;
	Activity context;
	ImageView imageView;
	static ProgressBar progressbar;
	
	public CustomAdapter(Activity context, ArrayList<MsgHolder> msg) {
		super(context,0,msg);
		// TODO Auto-generated constructor stub
		this.context = context;
	}


	public void setSend(int pos){
		MsgHolder holder = getItem(pos);
		holder.setSent(1);
	}
	
	
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		

		MsgHolder holder = getItem(position);
		TextView msgView,timeView,sentView;
		ImageView image;
		if(holder.type.equals("image")){
			if(!holder.member_reg_id.equals(holder.my_reg)){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.image_row, parent, false);
			
			imageView = (ImageView) convertView
					.findViewById(R.id.image);
			if(holder.flag==0){
				Picasso.with(context).load("http://www.tsquareltd.com/tracker/uploadimage/"+holder.msg).into(target);
				
			}else{
			imageView.setImageBitmap(holder.bm);
				
			}
			
			}else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.my_image_row, parent, false);
				imageView = (ImageView) convertView
						.findViewById(R.id.image);
				if(holder.bm == null)
					imageView.setBackgroundResource(R.drawable.no_photo);
				else
					imageView.setImageBitmap(holder.bm);
				
//				Picasso.with(context).load(R.drawable.itsme).into(imageView);
			
			}
			
		}else{
			
			if(!holder.member_reg_id.equals(holder.my_reg)){
				convertView = LayoutInflater.from(context).inflate(
						R.layout.msg_list_row, parent, false);
				image = (ImageView)convertView.findViewById(R.id.userImage);
				msgView = (TextView) convertView
						.findViewById(R.id.msgTextV);
				if(ReloadData.userImage.get(holder.member_number)==null){
					image.setImageBitmap(ReloadData.defaultMarker);
				}else{
				image.setImageBitmap(ReloadData.userImage.get(holder.member_number));
				}
				timeView = (TextView)convertView.findViewById(R.id.time);
				sentView = (TextView)convertView.findViewById(R.id.sent);
			}
			else{
				convertView = LayoutInflater.from(context).inflate(
						R.layout.row, parent, false);
				msgView = (TextView) convertView
						.findViewById(R.id.msgView);
				timeView = (TextView)convertView.findViewById(R.id.time);
				sentView = (TextView)convertView.findViewById(R.id.sent);
			}

			msgView.setText(holder.msg);
			timeView.setText(holder.time);
			
			if(holder.getSent()==1)
			    sentView.setText("Sent");
			else
				sentView.setText("     ");
			
		}
		return convertView;
	}
	
	private static Target target = new Target() {
		@Override
		public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					long time = System.currentTimeMillis();
					File file = new File(Environment.getExternalStorageDirectory().getPath() +"/Com_tracker/"+time+".jpg");
					if (file.exists ()) file.delete ();
					try
					{
						file.createNewFile();
						FileOutputStream ostream = new FileOutputStream(file);
						bitmap.compress(CompressFormat.JPEG, 75, ostream);
						ostream.close();

//						progressbar.setVisibility(View.GONE);
					} 
					catch (Exception e) 
					{
			//			Toast.makeText(context, "error ", 1000).show();
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

}
