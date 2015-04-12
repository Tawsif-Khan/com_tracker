package com.nearby.welcome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class MenuListAdapter extends BaseAdapter {
	Context context;
	LayoutInflater inflater;
	String[] mTitle, mSubTitle;
	int[] mIcon;
	Bitmap profileImage ;

	public MenuListAdapter(Context context, String[] title, String[] subtitle,
			int[] icon,Bitmap profileImage) {
		this.context = context;
		this.mTitle = title;
		this.mSubTitle = subtitle;
		this.mIcon = icon;
		this.profileImage = profileImage;
	}

	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView txtTitle, txtSubTitle;
		ImageView imgIcon;
		View itemView;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (position == 0) {
			itemView = inflater.inflate(R.layout.profile_in_drawer, parent,
					false);

			
			txtTitle = (TextView) itemView.findViewById(R.id.name);
			imgIcon = (ImageView) itemView.findViewById(R.id.profileImage);
			
			if(!mTitle[position].equals("") && !mSubTitle[position].equals("")){
			txtTitle.setText(mTitle[position]);

			RoundedTransformation round = new RoundedTransformation(70,1);
			imgIcon.setImageBitmap(round.roundIt(profileImage));
			}
		} else {
			itemView = inflater.inflate(R.layout.drawer_list_item, parent,
					false);
			txtTitle = (TextView) itemView.findViewById(R.id.title);
			txtSubTitle = (TextView) itemView.findViewById(R.id.subtitle);
			imgIcon = (ImageView) itemView.findViewById(R.id.icon);

			txtTitle.setText(mTitle[position]);
			txtSubTitle.setText(mSubTitle[position]);
//			Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
//                    mIcon[position]);
//			imgIcon.setImageBitmap(getRoundedShape(icon));
			imgIcon.setImageResource((mIcon[position]));

		}

		return itemView;
	}

	
	
	
}