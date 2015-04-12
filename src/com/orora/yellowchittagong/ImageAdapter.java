package com.orora.yellowchittagong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tawsif.finalproject.R;

public class ImageAdapter extends BaseAdapter{
	
	private Context context;
	
	private final String[] values;
	
	
	public ImageAdapter(Context context,String[] values)
	{
		this.context=context;
		this.values= values;
		
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.length;
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
	public View getView(int positon, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater=
				(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View gd;
		if(convertView==null)
		{
			gd=new GridView(context);
			
			gd = inflater.inflate(R.layout.name, null);
			
			TextView tv =(TextView)gd.findViewById(R.id.grid_item_label);
			
			tv.setText(values[positon]);
			
			ImageView img =(ImageView)gd.findViewById(R.id.grid_image);
			
			String str = values[positon];
		
			
			if(str.equals("Restaurant"))
				
			{
				img.setImageResource(R.drawable.res);
				
			}else  if(str.equals("Bank"))
			
			{
				img.setImageResource(R.drawable.bank);
				  
			
			}else  if(str.equals("Filling Station"))
			
			{
				img.setImageResource(R.drawable.fill);
				  
			
			}else  if(str.equals("Saloon"))
				
				{
					img.setImageResource(R.drawable.saloon);
					  
				
				}else  if(str.equals("Education"))
					
				{
					img.setImageResource(R.drawable.school);
					  
				
				}else  if(str.equals("Police Station"))
					
				{
					img.setImageResource(R.drawable.police);
					  
				
				}else  if(str.equals("Convention"))
					
				{
					img.setImageResource(R.drawable.house);
					  
				
				}else  if(str.equals("Market"))
					
				{
					img.setImageResource(R.drawable.mart);
					  
				
				}else  if(str.equals("Bus Counter"))
					
				{
					img.setImageResource(R.drawable.bus);
					  
					
				}else  if(str.equals("Hotel"))
					
				{
					img.setImageResource(R.drawable.hotel);
					  
					
				}else  if(str.equals("Hospital"))
					
				{
					img.setImageResource(R.drawable.hospital);
					  
					
				}else  if(str.equals("Car Station"))
			
				{
					img.setImageResource(R.drawable.abcd);
					  
					
				}else  if(str.equals("Railway Station"))
				{
					img.setImageResource(R.drawable.ef);
					  
					
				}
					
				else  if(str.equals("Beauty Parlour"))
				{
					img.setImageResource(R.drawable.c);
					  
					
				
				}else  
			
			{
				img.setImageResource(R.drawable.atm);
			}  
			
			}else
		{
			gd =(View)convertView;
		}
		
		return gd;
		
		
	}
}