package com.findmiin.business.local.Utility;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by JonIC on 2017-06-23.
 */

public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean e) {

    }
}