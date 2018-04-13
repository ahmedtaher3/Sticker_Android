package com.sticker_android.model.interfaces;

import com.sticker_android.model.corporateproduct.Product;

/**
 * Created by satyendra on 4/12/18.
 */

public interface DesignerActionListener {

    void onEdit(Product product);

    void onRemove(Product product);

    void onResubmit(Product product);
}
