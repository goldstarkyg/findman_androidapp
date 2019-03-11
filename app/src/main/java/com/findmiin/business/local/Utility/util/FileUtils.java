package com.findmiin.business.local.Utility.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.findmiin.business.local.Utility.util.ImageResizer.ImageResizer;
import com.findmiin.business.local.Utility.util.ImageResizer.operations.ImageRotation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static void copyAssetFileToSDCard(Context context, String assetFilePath, String sdCardFilePath) {
        if (new File(sdCardFilePath).exists() == true)
            return;

        AssetManager assetMng = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            is = assetMng.open(assetFilePath);

            if (is == null)
                return;
            fos = new FileOutputStream(sdCardFilePath);

            byte buf[] = new byte[2048];

            int nLen = 0;
            while ((nLen = is.read(buf)) > -1) {
                fos.write(buf, 0, nLen);
            }
        } catch (Exception e) {
            return;
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
            }
        }
    }

    public static final boolean checkExistFile(String path) {
        if (path == null || path.length() < 1)
            return false;

        return new File(path).exists();
    }

    // Correct orientation and resize to 100 X 100
    public static void preprocesPhoto(String selectedImagePath){
        // start progress
        String orient = "90";
        try {
            ExifInterface ei = new ExifInterface(selectedImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orient = "90";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orient = "180";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orient = "270";
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File photo = new File(selectedImagePath);
        Bitmap save = ImageResizer.crop(photo, 100, 100);
        ImageResizer.saveToFile(save, photo);

//        Bitmap save ;// = BitmapFactory.decodeFile(photo.getAbsolutePath());
//        ImageResizer.saveToFile(save, photo);

        if (orient.equals("90")) {
            save = ImageResizer.rotate(photo, ImageRotation.CW_90);
            ImageResizer.saveToFile(save, photo);
        }
        if (orient.equals("270")) {
            save = ImageResizer.rotate(photo, ImageRotation.CW_270);
            save = flipImage(save, FLIP_HORIZONTAL_PHOTO);
            ImageResizer.saveToFile(save, photo);
        }

    }

    // correct only the orientation of photo
    public static void savePhoto(String selectedImagePath){
        // start progress
        String orient = "90";
        try {
            ExifInterface ei = new ExifInterface(selectedImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orient = "90";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orient = "180";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orient = "270";
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File photo = new File(selectedImagePath);

        Bitmap save = BitmapFactory.decodeFile(photo.getAbsolutePath());

        if (orient.equals("90")) {
            save = ImageResizer.rotate(photo, ImageRotation.CW_90);
            ImageResizer.saveToFile(save, photo);
        }
        if (orient.equals("270")) {
            save = ImageResizer.rotate(photo, ImageRotation.CW_270);
            save = flipImage(save, FLIP_HORIZONTAL_PHOTO);
            ImageResizer.saveToFile(save, photo);
        }

    }
     static int FLIP_HORIZONTAL_PHOTO = 0;
     static int FLIP_VERTICAL_PHOTO = 1;

    public static Bitmap flipImage(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if(type == FLIP_VERTICAL_PHOTO) {
            // y = y * -1
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if(type == FLIP_HORIZONTAL_PHOTO) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


}
