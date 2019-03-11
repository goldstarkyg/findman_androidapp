package com.findmiin.business.local.manager.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {
	
	public static boolean isEmpty(Object obj)
    {
        if(obj == null)
            return true;
        if(obj.toString().trim().length() == 0)
            return true;
        if((obj instanceof Collection) && ((Collection)obj).size() == 0)
            return true;
        if((obj instanceof Map) && ((Map)obj).size() == 0)
            return true;
        try
        {
            if((obj instanceof Object[]) || obj.getClass().getName().startsWith("["))
            {
                int length = Array.getLength(obj);
                if(length == 0)
                    return true;
            }
        }
        catch(Exception e) { }
        return false;
    }
    
    public static boolean isNotEmpty( Object obj )
    {
    	return !isEmpty(obj);
    }
    
    public static boolean hasEmpty(Object objects[])
    {
        if(objects == null || objects.length == 0)
            return true;
        Object arr$[] = objects;
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            Object obj = arr$[i$];
            if(isEmpty(obj))
                return true;
        }

        return false;
    }    
    
	public static boolean checkEmail(String email){
		if( email == null || email.length() < 1 )
			return false;
		
		 String regex = RegularMatcher.EMAIL_EXPRESS;
		 Pattern p = Pattern.compile(regex);
		 Matcher m = p.matcher(email);
		 boolean isNormal = m.matches();
		 return isNormal;
	}
	
	public static boolean checkURL(String url){
		if( url == null || url.length() < 1 )
			return false;
		
		 String regex = RegularMatcher.URL_EXPRESS;
		 Pattern p = Pattern.compile(regex);
		 Matcher m = p.matcher(url);
		 boolean isNormal = m.matches();
		 return isNormal;
	}
}
