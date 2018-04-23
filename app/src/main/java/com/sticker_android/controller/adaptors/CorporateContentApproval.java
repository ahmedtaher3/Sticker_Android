package com.sticker_android.controller.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.corporate.productdetails.ProductDetailsActivity;
import com.sticker_android.controller.activities.designer.addnew.DesignDetailActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.OngoingContest;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by user on 18/4/18.
 */

public class CorporateContentApproval extends RecyclerView.Adapter<CorporateContentApproval.ContentViewHolder> {

    private final String TAG = CorporateContentApproval.class.getSimpleName();

    private DesignerActionListener designerActionListener;

    Context context;
        private ArrayList<Product> mItems = new ArrayList<>();

        public CorporateContentApproval(Context context) {
            this.context = context;

        }

        @Override
        public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new ContentViewHolder(inflater.inflate(R.layout.rec_item_content_approval, parent, false));
        }

        @Override
        public void onBindViewHolder(final ContentViewHolder holder, final int position) {

            final Product listItem = mItems.get(position);


            /*

            holder.tvStatus.setText("Pending");
            if (listItem.productList.getImagePath() != null && !listItem.productList.getImagePath().isEmpty())
                Glide.with(context)
                        .load(listItem.productList.getImagePath()).fitCenter()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.pgrImage.setVisibility(View.GONE);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.pgrImage.setVisibility(View.GONE);

                                return false;
                            }
                        })
                        .into(holder.imvContainer);

*/
           holder.imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   showPopup(v, position, "pending", listItem);
               }
           });
            holder.cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToDetails(listItem);
                }
            });
        }
    private void moveToDetails(Product product) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, product);

        if(product.getType().equals("ads")||product.getType().equals("product")) {

            Intent intent = new Intent(context, ProductDetailsActivity.class);

            intent.putExtras(bundle);
            ((Activity) context).startActivityForResult(intent, AppConstant.INTENT_PRODUCT_DETAILS);

            ((Activity) context).overridePendingTransition(R.anim.activity_animation_enter,
                    R.anim.activity_animation_exit);
        }else {
            Intent intent = new Intent(context, DesignDetailActivity.class);

            intent.putExtras(bundle);
            ((Activity) context).startActivityForResult(intent, AppConstant.INTENT_PRODUCT_DETAILS);

            ((Activity) context).overridePendingTransition(R.anim.activity_animation_enter,
                    R.anim.activity_animation_exit);

        }


    }
        @Override
        public int getItemCount() {
            return mItems.size();
        }

    public void setDesignerActionListener(DesignerActionListener actionListener){
        this.designerActionListener = actionListener;
    }
        public void setData(ArrayList<Product> data) {
            mItems = new ArrayList<>();
            mItems.addAll(data);
            notifyDataSetChanged();

        }


        public class ContentViewHolder extends RecyclerView.ViewHolder {
            public ImageView imvContainer;
            public CardView cardItem;
            public ProgressBar pgrImage;
            public TextView tvStatus;
            public ImageButton imvBtnEditRemove;

            public ContentViewHolder(View view) {

                super(view);
                cardItem          =   (CardView) view.findViewById(R.id.card_view);
                imvContainer      =   (ImageView) view.findViewById(R.id.imvContainer);
                pgrImage          =   (ProgressBar) view.findViewById(R.id.pgrImage);
                tvStatus          =   (TextView) view.findViewById(R.id.tv_add_product_status);
                imvBtnEditRemove  =   (ImageButton)view.findViewById(R.id.imvBtnEditRemove);
            }
        }

    /**
     * Method is used to show the popup with edit and delete option     *
     * @param v view on which click is perfomed
     * @param position position of item
     * @param
     */
    public void showPopup(View v, final int position, String status, final Product product) {
        final int editId = 1;
        final int removeId = 2;
        final int reSubmitId = 3;
        PopupMenu popup = new PopupMenu(context, v);
        popup.getMenu().add(1, editId, editId, R.string.edit);
        if(status.equalsIgnoreCase("rejected")){
            popup.getMenu().add(1, removeId, removeId, R.string.remove);
            popup.getMenu().add(1, reSubmitId, reSubmitId, R.string.resubmit_with_justification);
        }
        else{
            popup.getMenu().add(1, removeId, removeId, R.string.remove);
        }
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case editId:
                        AppLogger.error(TAG, "Edit item");
                        designerActionListener.onEdit(product);
                        break;
                    case removeId:
                        AppLogger.error(TAG, "Remove item");
                        if(Utils.isConnectedToInternet(context)){
                            Utils.deleteDialog(context.getString(R.string.txt_are_you_sure), (Activity) context, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeProductApi(product);
                                }
                            });
                        }
                        else{
                            Utils.showToastMessage(context, context.getString(R.string.pls_check_ur_internet_connection));
                        }
                        break;
                    case reSubmitId:
                        AppLogger.error(TAG, "Re submit item");
                        designerActionListener.onResubmit(product);
                        break;
                }
                return false;
            }
        });
    }

    private void removeProductApi(final Product product) {
        AppPref appPref = new AppPref(context);
        User mUserdata = appPref.getUserInfo();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiDeleteProduct(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId(),
                String.valueOf(product.getProductid()));

        apiResponseCall.enqueue(new ApiCall((Activity) context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    Utils.showToast(context, context.getString(R.string.deleted_successfully));
                    designerActionListener.onRemove(product);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

}
