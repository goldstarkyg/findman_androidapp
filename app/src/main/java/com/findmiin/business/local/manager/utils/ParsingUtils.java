package com.findmiin.business.local.manager.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ParsingUtils {
	public static String getStringFromAsset(Context context, String path) 
	{
	    String string = "";
	    if( context == null || path == null || CheckUtils.isEmpty(path) )
	    	return string;
	    
	    try {

	        InputStream is = context.getAssets().open(path);
	        int size = is.available();

	        byte[] buffer = new byte[size];

	        is.read(buffer);

	        is.close();

	        string = new String(buffer, "UTF-8");


	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return string;
	    }
	    return string;
	}	
	
	public static JSONObject loadJSONObjectFromAsset(Context context, String jsonPath)
	{
		JSONObject json = null;
		
	    if( context == null || jsonPath == null || CheckUtils.isEmpty(jsonPath) )
	    	return new JSONObject();
	    		
		String buffer = getStringFromAsset(context, jsonPath);
		
		try {
			json = new JSONObject(buffer);
		} catch (JSONException e) {
			json = new JSONObject();
		}
		
		return json;
	}
	
}
