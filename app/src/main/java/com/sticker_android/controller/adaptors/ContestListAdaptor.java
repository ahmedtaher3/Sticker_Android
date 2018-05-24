package com.sticker_android.controller.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.fan.home.contest.FanContestListActivity;
import com.sticker_android.model.contest.FanContest;

import java.util.ArrayList;

/**
 * Created by user on 24/4/18.
 */

public class ContestListAdaptor extends RecyclerView.Adapter<ContestListAdaptor.ContestViewHolder> {

    private final Context context;
    private ArrayList<FanContest> mFanList = new ArrayList<>();
    private ArrayList<FanContest> mFilterList = new ArrayList<>();

    public ContestListAdaptor(Context context) {
        this.context = context;
    }

    @Override
    public ContestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ContestViewHolder(inflater.inflate(R.layout.view_fan_contest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContestViewHolder holder, int position) {
        FanContest fanContest = mFanList.get(position);
        holder.tvDescription.setText(fanContest.contestName);
      /*  Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.contest_defaul);
        Bitmap blurredBitmap = blur(bitmap,context);
        holder.imvBackground.setImageBitmap(blurredBitmap);
*/
        //   holder.cardView.setBackgroundResource(R.drawable.contest_defaul);
        Configuration config = context.getResources().getConfiguration();
        final boolean isLeftToRight;
        isLeftToRight = config.getLayoutDirection() != View.LAYOUT_DIRECTION_RTL;
        if (isLeftToRight) {
            holder.imvBackground.setBackgroundResource(R.drawable.contest_hdpi);
        }else{
            holder.imvBackground.setBackgroundResource(R.drawable.contest_ldrtl_hdpi);

        }

        //   holder.imvBackground.setImageResource(R.drawable.contest_hdpi);
        holder.imvBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FanContestListActivity.class);
                int position = holder.getAdapterPosition();
                intent.putExtra(AppConstant.FAN_CONTEST_OBJ, mFanList.get(position));
                ((Activity) context).startActivityForResult(intent, 31);

            }
        });
    }

    public void setData(ArrayList<FanContest> data) {

        if (data != null) {
            mFanList.clear();
            mFanList.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mFanList.size();
    }


    class ContestViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription;
        ImageButton imbNext;
        CardView cardView;
        ImageView imvBackground;

        public ContestViewHolder(View view) {
            super(view);
            tvDescription = (TextView) view.findViewById(R.id.tv_content_description);
            imbNext = (ImageButton) view.findViewById(R.id.imbNext);
            //  cardView        =   (CardView) view.findViewById(R.id.card_view);
            imvBackground = (ImageView) view.findViewById(R.id.imvBackground);
        }
    }

    // Do Search...
    public ArrayList<FanContest> filter(final String text) {
        final ArrayList<FanContest> tempList = new ArrayList<>();
        if (mFanList != null)
            tempList.addAll(mFanList);
        new Thread(new Runnable() {
            @Override
            public void run() {

                mFanList.clear();
                if (TextUtils.isEmpty(text)) {
                    mFanList.clear();
                    mFanList.addAll(tempList);

                } else {
                    for (FanContest item : tempList) {
                        if (item.contestName.toLowerCase().contains(text.toLowerCase())) {
                            // Adding Matched items
                            mFanList.add(item);
                        }
                    }
                }

                // Set on UI Thread
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

        return mFanList;
    }

    private static final float BLUR_RADIUS = 12f;

    public static Bitmap blur(Bitmap image, Context context) {
        if (null == image) return null;
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}
