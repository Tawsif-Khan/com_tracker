package com.example.Map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContentOfPosition implements Serializable {

	private List<String> registration_ids;
	private Map<String, String> data;
	private Map<String,Double>data_loc;

	public void addRegId(ArrayList<String> arrRegId) {
		if (registration_ids == null)
			registration_ids = new LinkedList<String>();
		for (String s : arrRegId)
			registration_ids.add(s);
	}

	public void createData(String type,String lati,String longi,String reg_id,String my_number) {
		if (data == null)
			data = new HashMap<String, String>();
		
		data.put("type", type);
		data.put("lati", lati);
		data.put("longi", longi);
		data.put("reg_id", reg_id);
		data.put("number", my_number);
	}

}
