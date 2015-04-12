package com.finalproject.popup;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.finalproject.popup.master.menu.ActionItem;
import com.finalproject.popup.master.menu.QuickAction;
import com.tawsif.finalproject.R;

public class Menu_PopUp extends SherlockActivity
{
	private static final int MENU1 = 1;
	private static final int MENU2 = 2;
//	private static final int MENU3 = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setTitle("PopUp Menu");
		setContentView(R.layout.popup_menu_layout);
		
		ActionItem menu1 = new ActionItem(MENU1, "Camera", getResources().getDrawable(R.drawable.play));
		ActionItem menu2 = new ActionItem(MENU2, "Gallery", getResources().getDrawable(R.drawable.addtoplaylist));
//		ActionItem menu3 = new ActionItem(MENU3, "Search", getResources().getDrawable(R.drawable.search));
		
		menu1.setSticky(true);
		menu2.setSticky(true);
//		menu3.setSticky(true);
		
		final QuickAction quickAction = new QuickAction(this, QuickAction.VERTICAL);
        quickAction.addActionItem(menu1);
        quickAction.addActionItem(menu2);
//        quickAction.addActionItem(menu3);
        
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() 
		{			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) 
			{					
				if (actionId == MENU1) 
				{
					Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
				} 
				else if (actionId == MENU2) 
				{
					Toast.makeText(getApplicationContext(), "Add to Playlist", Toast.LENGTH_SHORT).show();
				}
//				else if (actionId == MENU3) 
//				{
//					Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
//				}
			}
		});
		
		Button pop = (Button) findViewById(R.id.PopUp_MenuFull);
		pop.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				quickAction.show(v);	
			}
		});		
	}
}
