package com.findmiin.business.local.Utility;

import android.text.format.Time;

/**
 * Created by JonIC on 2017-03-17.
 */
public interface OnClockTickListner {
    public void OnSecondTick(Time currentTime);
    public void OnMinuteTick(Time currentTime);
}