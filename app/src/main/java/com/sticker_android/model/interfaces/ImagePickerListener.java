package com.sticker_android.model.interfaces;

/**
 * Created by satyendra on 06-Feb-18.
 */

public interface ImagePickerListener {

    void pickFromGallery();
    void captureFromCamera();
    void selectedItemPosition(int position);
}
