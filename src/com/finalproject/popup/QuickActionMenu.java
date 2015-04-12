package com.finalproject.popup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tawsif.finalproject.R;

public class QuickActionMenu 
{
	private Context mContext;
	private LayoutInflater mInflater;
	private WindowManager mWindowManager;
	private PopupWindow mPopupWindow;
	private View mContentView;
	private ListView mItemsView;
	private TextView mHeaderTitleView;
	private OnItemSelectedListener mListener;
	private List<MenuItem> mItems;
	private int mWidth = 240;
	private float mScale;

	public QuickActionMenu(Context context) 
	{
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(metrics);
		mScale = metrics.scaledDensity;

		mItems = new ArrayList<MenuItem>();

		mPopupWindow = new PopupWindow(context);
		mPopupWindow.setTouchInterceptor(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) 
				{
					mPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		setContentView(mInflater.inflate(R.layout.quickaction_menu, null));
	}

	private void setContentView(View contentView) 
	{
		mContentView = contentView;
		mItemsView = (ListView) contentView.findViewById(R.id.items);
		mHeaderTitleView = (TextView) contentView.findViewById(R.id.header_title);
		mPopupWindow.setContentView(contentView);
	}

	public MenuItem add(int itemId, int titleRes) 
	{
		MenuItem item = new MenuItem();
		item.setItemId(itemId);
		item.setTitle(mContext.getString(titleRes));
		mItems.add(item);
		return item;
	}

	public void show() 
	{
		show(null);
	}

	@SuppressWarnings("deprecation")
	public void show(View anchor) 
	{
		if (mItems.size() == 0) 
		{
			throw new IllegalStateException("PopupMenu#add was not called with a menu item to display.");
		}
		preShow();

		MenuItemAdapter adapter = new MenuItemAdapter(mContext, mItems);
		mItemsView.setAdapter(adapter);
		mItemsView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				if (mListener != null) 
				{
					mListener.onItemSelected(mItems.get(position));
				}
				mPopupWindow.dismiss();
			}
		});

		if (anchor == null) 
		{
			View parent = ((Activity) mContext).getWindow().getDecorView();
			mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
			return;
		}

		int xPos, yPos;
		int[] location = new int[2];
		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[0] + anchor.getHeight());

		mContentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mContentView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mContentView.getMeasuredHeight();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
		
		// Set x-coordinate to display the popup menu
		xPos = anchorRect.centerX() - mPopupWindow.getWidth() / 2;
		int dyTop = anchorRect.top;
		int dyBottom = screenHeight + rootHeight;
		boolean onTop = dyTop > dyBottom;

		// Set y-coordinate to display the popup menu
		if (onTop) 
		{
			yPos = anchorRect.top - rootHeight;
		}
		else 
		{
			if (anchorRect.bottom > dyTop) 
			{
				yPos = anchorRect.bottom - 20;
			}
			else 
			{
				yPos = anchorRect.top - anchorRect.bottom + 50;
			}
		}
		mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void preShow() 
	{
		int width = (int) (mWidth * mScale);
		mPopupWindow.setWidth(width);
		mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.panel_background));
	}

	public void dismiss() 
	{
		if (mPopupWindow != null && mPopupWindow.isShowing()) 
		{
			mPopupWindow.dismiss();
		}
	}

	public void setHeaderTitle(CharSequence title) 
	{
		mHeaderTitleView.setText(title);
		mHeaderTitleView.setVisibility(View.VISIBLE);
		mHeaderTitleView.requestFocus();
	}

	public void setWidth(int width) 
	{
		mWidth = width;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener) 
	{
		mListener = listener;
	}

	public interface OnItemSelectedListener 
	{
		public void onItemSelected(MenuItem item);
	}

	static class ViewHolder 
	{
		ImageView icon;
		TextView title;
	}

	private class MenuItemAdapter extends ArrayAdapter<MenuItem> 
	{
		public MenuItemAdapter(Context context, List<MenuItem> objects) 
		{
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ViewHolder holder;
			if (convertView == null) 
			{
				convertView = mInflater.inflate(R.layout.quickaction_menu_list_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			} 
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}

			MenuItem item = getItem(position);
			if (item.getIcon() != null) 
			{
				holder.icon.setImageDrawable(item.getIcon());
				holder.icon.setVisibility(View.VISIBLE);
			} 
			else 
			{
				holder.icon.setVisibility(View.GONE);
			}
			holder.title.setText(item.getTitle());
			return convertView;
		}
	}
}
