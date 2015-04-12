package com.example.finalproject;

import android.graphics.Bitmap;
import android.widget.TextView;

public class MsgHolder {
	public String msg,member_reg_id,my_reg,time,imageLink,type,image_in_phone,member_number;
	public int flag;
	public Bitmap bm = null;
	public int sent;
	TextView sendView;
	public MsgHolder(String member_number,String msg,String member_red_id,String my_reg,String time,String type,Bitmap bm,int flag){
		this.member_number = member_number;
		this.msg = msg;
		this.member_reg_id = member_red_id;
		this.my_reg = my_reg;
		this.time = time;
		this.type = type;
		this.bm = bm;
		this.flag = flag;
//		this.image_in_phone = image_in_phone;		
	}
	public void setSent(int st){
		sent = st;
	}
	
	public int getSent(){
		return sent;
	}
}
