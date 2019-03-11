package com.findmiin.business.local.manager.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlgorithmUtils {
	 public static String invert(String s) {
	        String temp = "";
	        for (int i = s.length() - 1; i >= 0; i--)
	            temp += s.charAt(i);
	        return temp;
     }		 
	 
	 public static void bindJSONObject(JSONObject to, JSONObject from)
	 {
		 if( to == null || from == null )
			 return;
		 
		 Iterator<String> iter = from.keys();
		 while (iter.hasNext()) {
		    String key = iter.next();		    
		    try {
		    	String value = from.optString(key, "");
		    	if( to.has(key) == false )
		    		to.put(key, value);		    	
		    } catch (Exception e) {
		        // Something went wrong!
		    }
		}
	 }
	 
	 public static List<JSONObject> jsonarrayToList(JSONArray array)
	 {
		 List<JSONObject> list = new ArrayList<JSONObject>();
		 if( array == null )
			 return list;
		 
		 for(int i = 0; i < array.length(); i++ )
			 list.add(array.optJSONObject(i));
		 
		 return list;		 
	 }
	 
	 public static JSONArray listTojsonarray(List<JSONObject> list)
	 {
		 JSONArray array = new JSONArray();
		 if( list == null )
			 return array;
		 
		 for(int i = 0; i < list.size(); i++ )
			 array.put(list.get(i));
		 
		 return array;		 
	 }
	 
}
