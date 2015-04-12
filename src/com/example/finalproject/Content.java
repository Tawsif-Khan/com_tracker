package com.example.finalproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Content implements Serializable {

	private List<String> registration_ids;
	private Map<String, String> data;

	public void addRegId(ArrayList<String> arrRegId) {
		if (registration_ids == null)
			registration_ids = new LinkedList<String>();
		for (String s : arrRegId)
			registration_ids.add(s);
	}

	public void createData(String title, String message,String Rid,String group_name,String group_title,String time,int id,int listPosition) {
		if (data == null)
			data = new HashMap<String, String>();
		data.put("type", "message");
		data.put("title", title);
		data.put("message", message);
		data.put("Rid", Rid);
		data.put("group_name", group_name);
		data.put("group_title", group_title);
		data.put("msg_time", time);
		data.put("msg_id", id+"");
		data.put("list_position", listPosition+"");
	}

}
