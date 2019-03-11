package com.findmiin.business.local.Utility.util.ImageResizer.utils;

/**
 * Created by JonIC on 2016-12-08.
 */
public enum ImageOrientation {

    PORTRAIT,
    LANDSCAPE;

    public static ImageOrientation getOrientation(int width, int height) {
        if (width >= height) {
            return ImageOrientation.LANDSCAPE;
        } else {
            return ImageOrientation.PORTRAIT;
        }
    }

}