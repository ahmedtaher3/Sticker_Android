package com.sticker_android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by user on 21/5/18.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    private String TAG = "DownloadImage";
    ISaveImageToLocal iSaveImageToLocal;

   public DownloadImage(ISaveImageToLocal iSaveImageToLocal){
        this.iSaveImageToLocal=iSaveImageToLocal;
    }

    private Bitmap downloadImageBitmap(String sUrl) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
            bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
            inputStream.close();

        } catch (Exception e) {
           // Log.d(TAG, "Exception 1, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadImageBitmap(params[0]);
    }

    protected void onPostExecute(Bitmap result) {
       iSaveImageToLocal.imageResult(result);
         }


   public interface  ISaveImageToLocal{
        void imageResult(Bitmap result);
    }
}