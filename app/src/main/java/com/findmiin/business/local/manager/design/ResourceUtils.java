package com.findmiin.business.local.manager.design;

import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by JonIC on 2017-01-23.
 */
public class ResourceUtils {
    public static void addClickEffect(View view)
    {
        if( view == null )
            return;

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        View view = (View) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        // Your action here on button click

                    case MotionEvent.ACTION_CANCEL: {
                        View view = (View) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
