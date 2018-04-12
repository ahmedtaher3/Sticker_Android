package com.sticker_android.controller.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.designer.addnew.DesignDetailActivity;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by satyendra on 4/10/18.
 */

public class DesignListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = DesignListAdapter.class.getSimpleName();
    private ArrayList<Product> mItems;
    private Context context;
    public boolean isLoaderVisible;

    private final int ITEM_FOOTER = 0;
    private final int ITEM_PRODUCT = 1;

    private TimeUtility timeUtility = new TimeUtility();

    public interface OnProductItemClickListener{
        void onProductItemClick(Product product);
    }

    private OnProductItemClickListener productItemClickListener;
    private DesignerActionListener designerActionListener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imvOfAds;
        public TextView tvProductTitle, tvStatus, tvDesciption, tvTime, tvDownloads;
        public CheckBox checkboxLike, checkboxShare;
        public ImageButton imvBtnEditRemove;
        public CardView cardItem;
        public ProgressBar pbLoader;

        public ViewHolder(View view) {
            super(view);
            imvOfAds = (ImageView) view.findViewById(R.id.imvOfAds);
            tvProductTitle = (TextView) view.findViewById(R.id.tv_add_product_title);
            tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);
            tvDesciption = (TextView) view.findViewById(R.id.tv_add_product_item_description);
            checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
            checkboxShare = (CheckBox) view.findViewById(R.id.checkboxShare);
            imvBtnEditRemove = (ImageButton) view.findViewById(R.id.imvBtnEditRemove);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvDownloads = (TextView) view.findViewById(R.id.tvDownloads);
            cardItem = (CardView) view.findViewById(R.id.card_view);
            pbLoader = (ProgressBar) view.findViewById(R.id.pgrImage);
        }
    }

    public class LoaderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ProgressBar progressBar;

        public LoaderViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.pbMore);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DesignListAdapter(Context cnxt) {
        mItems = new ArrayList<>();
        context = cnxt;
    }

    public void setDesignerActionListener(DesignerActionListener actionListener){
        this.designerActionListener = actionListener;
    }

    public void setOnProductClickListener(OnProductItemClickListener productClickListener){
        this.productItemClickListener = productClickListener;
    }

    public void setData(ArrayList<Product> data) {
        mItems = new ArrayList<>();
        mItems.addAll(data);
        notifyDataSetChanged();
        isLoaderVisible = false;
    }

    public void updateAdapterData(ArrayList<Product> data){
        mItems = new ArrayList<>();
        mItems.addAll(data);
    }

    public void addLoader() {
        AppLogger.error(TAG, "Add loader... in adapter");
        Product postItem = new Product();
        postItem.setProductid(-1);
        mItems.add(postItem);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(mItems.size() - 1);
            }
        });
        isLoaderVisible = true;
    }

    public void removeLoader() {
        AppLogger.error(TAG, "Remove loader... from adapter");
        Product postItem = new Product();
        postItem.setProductid(-1);
        int index = mItems.indexOf(postItem);
        AppLogger.error(TAG, "Loader index => "+ index);
        if (index != -1) {
            mItems.remove(index);
            //notifyDataSetChanged();
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
            isLoaderVisible = false;
        }
    }

    public void removeProductData(int index){
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
        }
    }

    public void removeProductData(Product product){
        int index = mItems.indexOf(product);
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
        }
    }

    public void updateModifiedItem(Product postItem) {
        int index = mItems.indexOf(postItem);
        if (index != -1) {
            mItems.set(index, postItem);
            notifyDataSetChanged();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loader_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final LoaderViewHolder vh = new LoaderViewHolder(v);
            return vh;
        } else {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_design_items, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final ViewHolder vh = new ViewHolder(v);

            vh.cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = vh.getAdapterPosition();
                    Product product = mItems.get(position);
                    Intent intent = new Intent(context, DesignDetailActivity.class);
                    intent.putExtra(AppConstant.PRODUCT, product);
                    ((Activity)context).startActivityForResult(intent, 0);
                }
            });
            return vh;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_FOOTER) {

        } else {
            final ViewHolder itemHolder = (ViewHolder) holder;
            final Product productItem = mItems.get(position);

            itemHolder.checkboxLike.setText(Utils.format(0));
            itemHolder.checkboxShare.setText(Utils.format(0));
            itemHolder.tvDownloads.setText(Utils.format(0));
            itemHolder.imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position, "pending", productItem);
                }
            });
            itemHolder.tvProductTitle.setText(Utils.capitlizeText(productItem.getProductname()));

            if(productItem.getDescription() != null && productItem.getDescription().trim().length() != 0){
                itemHolder.tvDesciption.setVisibility(View.VISIBLE);
                itemHolder.tvDesciption.setText(Utils.capitlizeText(productItem.getDescription()));
            }
            else{
                itemHolder.tvDesciption.setVisibility(View.GONE);
            }
            itemHolder.tvTime.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(productItem.getCreatedTime()), context).replaceAll("about", "").trim());

            String status = "pending";
            if (status.equalsIgnoreCase("rejected")) {
                itemHolder.tvStatus.setTextColor(Color.RED);
                itemHolder.tvStatus.setText("Rejected");
            } else if(status.equalsIgnoreCase("approved")){
                itemHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorHomeGreen));
                itemHolder.tvStatus.setText("Approved");
            }
            else{
                itemHolder.tvStatus.setTextColor(Color.parseColor("#1D93FB"));
                itemHolder.tvStatus.setText("Pending");
            }

            if(productItem.getImagePath() != null && !productItem.getImagePath().isEmpty()){
                itemHolder.pbLoader.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(productItem.getImagePath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                itemHolder.pbLoader.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                itemHolder.pbLoader.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(itemHolder.imvOfAds);
            }
            else{
                itemHolder.imvOfAds.setBackgroundColor(ContextCompat.getColor(context, R.color.image_background_color));
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getProductid() == -1) {
            return ITEM_FOOTER;
        } else {
            return ITEM_PRODUCT;
        }
    }

    /**
     * Method is used to show the popup with edit and delete option     *
     * @param v view on which click is perfomed
     * @param position position of item
     * @param product
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
