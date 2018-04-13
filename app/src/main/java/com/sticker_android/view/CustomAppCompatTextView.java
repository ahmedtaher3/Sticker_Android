package com.sticker_android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.sticker_android.R;

/**
 * Created by satyendra on 1/30/18.
 */

public class CustomAppCompatTextView extends AppCompatTextView {

    private final String TAG = CustomAppCompatTextView.class.getSimpleName();

    private Drawable drawableLeft = null;
    private Drawable drawableRight = null;
    private Drawable drawableBottom = null;
    private Drawable drawableTop = null;
    private Context mContext;

    public CustomAppCompatTextView(Context context) {
        super(context);
        mContext = context;
    }

    public CustomAppCompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttrs(context, attrs);
    }

    public CustomAppCompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(context, attrs);
    }

    void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomAppCompatEditText);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableLeft = attributeArray.getDrawable(R.styleable.CustomAppCompatEditText_drawableLeftCompat);
                drawableRight = attributeArray.getDrawable(R.styleable.CustomAppCompatEditText_drawableRightCompat);
                drawableBottom = attributeArray.getDrawable(R.styleable.CustomAppCompatEditText_drawableBottomCompat);
                drawableTop = attributeArray.getDrawable(R.styleable.CustomAppCompatEditText_drawableTopCompat);
            } else {
                final int drawableLeftId = attributeArray.getResourceId(R.styleable.CustomAppCompatEditText_drawableLeftCompat, -1);
                final int drawableRightId = attributeArray.getResourceId(R.styleable.CustomAppCompatEditText_drawableRightCompat, -1);
                final int drawableBottomId = attributeArray.getResourceId(R.styleable.CustomAppCompatEditText_drawableBottomCompat, -1);
                final int drawableTopId = attributeArray.getResourceId(R.styleable.CustomAppCompatEditText_drawableTopCompat, -1);

                if (drawableLeftId != -1)
                    drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId);
                if (drawableRightId != -1)
                    drawableRight = AppCompatResources.getDrawable(context, drawableRightId);
                if (drawableBottomId != -1)
                    drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
                if (drawableTopId != -1)
                    drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            attributeArray.recycle();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if(drawableLeft != null){
            Drawable drawable = drawableLeft.mutate();
            drawable = DrawableCompat.wrap(drawable);

            if(focused){
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
            else{
                if(getText().toString().trim().length() != 0){
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorPrimary));
                }
                else{
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.icon_color));
                }
            }
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            setCompoundDrawablesWithIntrinsicBounds(drawable, drawableTop, drawableRight, drawableBottom);
        }
    }

    public void setDrawableTopColor(boolean isChecked){

        try{
            Drawable drawable = drawableTop.mutate();
            drawable = DrawableCompat.wrap(drawable);

            if(isChecked){
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorAccent));
                setTextColor(Color.WHITE);
            }
            else{
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorPrimary));
                setTextColor(Color.BLACK);
            }
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawable, drawableRight, drawableBottom);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setDrawableLeftColor(boolean isChecked){

        try{
            Drawable drawable = drawableLeft.mutate();
            drawable = DrawableCompat.wrap(drawable);

            if(isChecked){
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorAccent));
                setTextColor(Color.WHITE);
            }
            else{
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorPrimary));
                setTextColor(Color.BLACK);
            }
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            setCompoundDrawablesWithIntrinsicBounds(drawable, drawableTop, drawableRight, drawableBottom);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setDrawableRightColor(boolean isChecked){

        try{
            Drawable drawable = drawableRight.mutate();
            drawable = DrawableCompat.wrap(drawable);

            if(isChecked){
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorAccent));
                setTextColor(Color.WHITE);
            }
            else{
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.colorPrimary));
                setTextColor(Color.BLACK);
            }
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawable, drawableBottom);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setCompoundDrawable(Drawable left, Drawable top, Drawable right, Drawable bottom){
        drawableLeft = left;
        drawableTop = top;
        drawableRight = right;
        drawableBottom = bottom;
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }
}
