package com.sticker_android.model.interfaces;

import com.sticker_android.model.corporateproduct.Category;

import java.util.ArrayList;

/**
 * Created by satyendra on 4/10/18.
 */

public interface CategoryDataListener {

    void onCategoryDataRetrieved(ArrayList<Category> categories);

    void onFailure();
}
