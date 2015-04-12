package com.example.finalproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RequestContent implements Serializable {

	private List<String> registration_ids;
	private Map<String, String> data;
	private Map<String, ArrayList<String>> reg;
	
	public void addRegId(ArrayList<String> arrRegId) {
		if (registration_ids == null)
			registration_ids = new LinkedList<String>();
		for (String s : arrRegId)
			registration_ids.add(s);
	}

	public void createData(String numbers,String title, String message,String Rid,ArrayList<String> arrRegId,String group_name,String group_title) {
		if (data == null)
			data = new HashMap<String, String>();
		if(reg == null)
			reg = new HashMap<String, ArrayList<String>>();
		data.put("type", "request");
		data.put("title", title);
		data.put("message", message);
		data.put("Rid", Rid);
		data.put("group_name", group_name);
		String s = Rid + "\n";
		for(int i=0;i<arrRegId.size();i++){
			s = s + arrRegId.get(i)+"\n";
		}
		data.put("regList", s);
		data.put("numbers", numbers);
		data.put("group_title", group_title);
	}

}
