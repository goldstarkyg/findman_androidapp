package com.findmiin.business.local.manager.design;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JonIC on 2017-01-23.
 */
public class LayoutUtils {
    public static void setMargin(View view, int left, int top, int right, int bottom, boolean scale )
    {
        if( view == null )
            return;
        try{
            if(view.getLayoutParams()!=null) {
                if( scale == true )
                {
                    left = ScreenAdapter.computeWidth(left);
                    right = ScreenAdapter.computeWidth(right);
                    top = ScreenAdapter.computeHeight(top);
                    bottom = ScreenAdapter.computeHeight(bottom);
                }
                ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).leftMargin = left;
                ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).topMargin = top;
                ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).rightMargin = right;
                ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).bottomMargin = bottom;
            }
        } catch( Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void setSize(View view, int width, int height, boolean scale )
    {
        if( view == null )
            return;
        try{
            if(view.getLayoutParams()!=null) {
                if( scale == true )
                {
                    if( width >= 0 )
                        width = ScreenAdapter.computeWidth(width);
                    if( height >= 0 )
                        height = ScreenAdapter.computeWidth(height);
                }

                view.getLayoutParams().width = width;
                view.getLayoutParams().height = height;
            }
        } catch( Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void setPadding(View view, int left, int top, int right, int bottom, boolean scale )
    {
        if( view == null )
            return;
        try{
            if(view.getLayoutParams()!=null) {
                if( scale == true )
                {
                    left = ScreenAdapter.computeWidth(left);
                    right = ScreenAdapter.computeWidth(right);
                    top = ScreenAdapter.computeHeight(top);
                    bottom = ScreenAdapter.computeHeight(bottom);
                }
                view.setPadding(left, top, right, bottom);
            }
        } catch( Exception e)
        {
            e.printStackTrace();
        }
    }

    public static int getWidth(int originalWidth){
        int width =ScreenAdapter.computeWidth(originalWidth) ;
        return width;
    }
    public static int getHeight(int originalHeight){
        int height =ScreenAdapter.computeWidth(originalHeight) ;
        return height;
    }

}

