package com.sistematias.relevadordispositivos.clases;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.sistematias.relevadordispositivos.activity.MyApp;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by samuel on 07/12/2015.
 */
public class Util {
    public static int getLastImageId() {
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = MyApp.getAppContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        try {
            if (imageCursor.moveToFirst()) {
                int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                imageCursor.close();
                return id;
            } else {
                imageCursor.close();
                return 0;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static void removeImage(int id) {
        ContentResolver cr = MyApp.getAppContext().getContentResolver();
        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{Long.toString(id)});
    }

    public static Uri getImageFileUri(String codigo) {
        // Create a storage directory for the images
        // To be safe(er), you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this
        String tag = "getImageFileUri";
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Data.DIRECTORIO_FOTO);
        Log.d(tag, "Find " + mediaStorageDir.getAbsolutePath());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(tag, "Error al intentar crear el directorio: " + Data.DIRECTORIO_FOTO);
                return null;
            } else {
                Log.d(tag, "Creado el directorio: " + Data.DIRECTORIO_FOTO);
            }
        }
        // Create an image file name
        String timeStamp = Data.FECHA_FILE_NAME.format(new Date());
        File image = new File(mediaStorageDir, codigo + "_" + Data.getImei() + "_" + timeStamp + ".jpg");

        if (!image.exists()) {
            try {
                image.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //return image;
        // Create an File Uri
        return Uri.fromFile(image);
    }

    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
}
