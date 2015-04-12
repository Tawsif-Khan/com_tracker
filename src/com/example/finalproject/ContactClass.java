package com.example.finalproject;

import android.graphics.Bitmap;

public class ContactClass {
	  
	 String code = null;
	 String name = null;
	 boolean selected = false;
	 int pos = 0;
	 Bitmap bm; 
	 public ContactClass(String code, String name,int pos, boolean selected,Bitmap bm) {
	  super();
	  this.code = code;
	  this.name = name;
	  this.selected = selected;
	  this.pos = pos;
	  this.bm = bm;
	 }
	 public void setImage(Bitmap bm){
		 this.bm = bm;
	 }
	 
	 public Bitmap getImage(){
		 return bm;
	 }
	 
	 public String getCode() {
	  return code;
	 }
	 public void setCode(String code) {
	  this.code = code;
	 }
	 public int getPos(){
		 return pos;
	 }
//	 public void setPos(){
//		 this.pos = pos;
//	 }
	 public String getName() {
	  return name;
	 }
	 public void setName(String name) {
	  this.name = name;
	 }
	 
	 public boolean isSelected() {
	  return selected;
	 }
	 public void setSelected(boolean selected) {
	  this.selected = selected;
	 }
	  
	}
