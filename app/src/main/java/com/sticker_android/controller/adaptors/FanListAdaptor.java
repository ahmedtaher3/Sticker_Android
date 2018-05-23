package com.sticker_android.controller.adaptors;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.sticker_android.R;
import com.sticker_android.controller.activities.fan.home.fandownloadmage.FanDownloadedImageActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.DownloadImage;
import com.sticker_android.utils.FileUtil;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;

import java.io.File;
import java.util.ArrayList;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import retrofit2.Call;


/**
 * Created by user on 19/4/18.
 */

public class FanListAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = FanListAdaptor.class.getSimpleName();
    private ArrayList<Product> mItems;
    private Context context;
    public boolean isLoaderVisible;

    private final int ITEM_FOOTER = 0;
    private final int ITEM_PRODUCT = 1;

    private TimeUtility timeUtility = new TimeUtility();
    AppPref appPref;

    User mUserdata;

    public interface OnProductItemClickListener {
        void onProductItemClick(Product product);
    }

    private DesignListAdapter.OnProductItemClickListener productItemClickListener;
    private DesignerActionListener designerActionListener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imvOfAds;
        public TextView tvProductTitle, tvStatus, tvDesciption, tvTime, tvDownloads;
        public CheckBox checkboxLike, checkboxShare;
        public ImageButton imvBtnEditRemove;
        public CardView cardItem;
        public ProgressBar pbLoader;
        public TextView tvName;
        public TextView tvFeatured;

        public ViewHolder(View view) {
            super(view);
            imvOfAds = (ImageView) view.findViewById(R.id.imvOfAds);
            tvProductTitle = (TextView) view.findViewById(R.id.tv_add_product_title);
            tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);
            tvDesciption = (TextView) view.findViewById(R.id.tv_add_product_item_description);
            checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
            checkboxShare = (CheckBox) view.findViewById(R.id.checkboxShare);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvDownloads = (TextView) view.findViewById(R.id.tvDownloads);
            cardItem = (CardView) view.findViewById(R.id.card_view);
            pbLoader = (ProgressBar) view.findViewById(R.id.pgrImage);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvFeatured = (TextView) view.findViewById(R.id.tvFeatured);
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
    public FanListAdaptor(Context cnxt) {
        mItems = new ArrayList<>();
        context = cnxt;
        appPref = new AppPref(context);
        mUserdata = appPref.getUserInfo();
    }

    public void setDesignerActionListener(DesignerActionListener actionListener) {
        this.designerActionListener = actionListener;
    }

    public void setOnProductClickListener(DesignListAdapter.OnProductItemClickListener productClickListener) {
        this.productItemClickListener = productClickListener;
    }

    public void setData(ArrayList<Product> data) {
        if (data != null) {
            mItems = new ArrayList<>();
            mItems.addAll(data);
            notifyDataSetChanged();
            isLoaderVisible = false;
        }
    }

    public void updateAdapterData(ArrayList<Product> data) {
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
        AppLogger.error(TAG, "Loader index => " + index);
        if (index != -1) {
            mItems.remove(index);
            //notifyDataSetChanged();
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
            isLoaderVisible = false;
        }
    }

    public void removeProductData(int index) {
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
        }
    }

    public void removeProductData(Product product) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item_fan, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final ViewHolder vh = new ViewHolder(v);

            vh.cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = vh.getAdapterPosition();
                    Product product = mItems.get(position);
                    if (productItemClickListener != null)
                        productItemClickListener.onProductItemClick(product);
                }
            });
            likeListener(vh);
            downloadListener(vh);
            share(vh);
            return vh;
        }
    }

    private void share(final ViewHolder vh) {

        vh.checkboxShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = vh.getAdapterPosition();
                final Product product = mItems.get(position);
                createDeepLink(product);

                shareApi(product, 1, position);
            }
        });
    }

    private void createDeepLink(final Product product){
        Gson gson = new Gson();

        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/" + product.getProductid())
                .setTitle(context.getString(R.string.app_name))
                .setContentDescription(product.getProductname())
                .setContentImageUrl(product.getImagePath())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("property1", gson.toJson(product));

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing");
                /*.addControlParameter("$desktop_url", "http://www.google.com")
                .addControlParameter("$ios_url", "http://example.com/ios");*/

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(context, "Check this out!", "")
                .setCopyUrlStyle(context.getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(context.getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .setAsFullWidthStyle(true)
                .setSharingTitle(context.getResources().getString(R.string.txt_share));

        branchUniversalObject.showShareSheet((Activity) context,
                linkProperties,
                shareSheetStyle,
                new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {
                    }
                    @Override
                    public void onShareLinkDialogDismissed() {
                    }
                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {

                        Log.e(TAG, "Shared link => " + sharedLink);
                    }
                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                });

        branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Image url " + product.getImagePath();
                    String shareSub = "Share data";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + "\n" + url);
                    context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.txt_share) + " :" + mUserdata.getEmail()));*/
                }
            }
        });

    }

    private void downloadListener(final ViewHolder vh) {

        vh.tvDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = vh.getAdapterPosition();
                Product product = mItems.get(position);
                downloadApi(product, 1, position);
                //  if(product.statics.downloadCount==0)
                saveToLocal(product);
            }
        });


    }

    private void saveToLocal(Product product) {
        if (Utils.isConnectedToInternet(context)) {
            new DownloadImage(new DownloadImage.ISaveImageToLocal() {
                @Override
                public void imageResult(Bitmap result) {
                    Uri tempUri = Utils.getImageUri(context, result);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    File finalFile = new File(Utils.getRealPathFromURI(context, tempUri));
                    if (finalFile != null) {
                        FileUtil.albumUpdate(context, finalFile.getAbsolutePath());
                        MediaScannerConnection.scanFile(context, new String[] { finalFile.getPath() }, new String[] { "image/jpeg" }, null);

                        Utils.showToast(context, "Image Saved Successfully.");
                    } AppLogger.debug(FanDownloadedImageActivity.class.getSimpleName(), "called here" + finalFile);
                }
            }).execute(product.getImagePath());
        }
    }

    private void likeListener(final ViewHolder vh) {

        vh.checkboxLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = vh.getAdapterPosition();
                Product product = mItems.get(position);
                boolean checked = isChecked;
                if (buttonView.isPressed())
                    if (product.isLike > 0) {
                        likeApi(vh, product, 0, position);
                        vh.checkboxLike.setEnabled(false);
                    } else {
                        likeApi(vh, product, 1, position);
                        viewCountApi(product);
                        vh.checkboxLike.setEnabled(false);
                    }
              /*  if (product.isLike==1) {
                    likeApi(product, 0, position);
                } else if(product.isLike==0){
                    likeApi(product, 1, position);

                }*/

            }
        });
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


            if (productItem.getType().equals("product") || productItem.getType().equals("ads")) {
                itemHolder.tvDesciption.setVisibility(View.VISIBLE);
                itemHolder.tvDownloads.setVisibility(View.GONE);
                itemHolder.tvFeatured.setVisibility(View.VISIBLE);
            } else {
                itemHolder.tvDownloads.setVisibility(View.VISIBLE);
                itemHolder.tvDesciption.setVisibility(View.GONE);
                itemHolder.tvFeatured.setVisibility(View.GONE);
            }
            itemHolder.tvName.setText(productItem.userName);
            itemHolder.checkboxLike.setText(Utils.format(productItem.statics.likeCount));
            itemHolder.checkboxShare.setText(Utils.format(productItem.statics.shareCount));
            itemHolder.tvDownloads.setText(Utils.format(productItem.statics.downloadCount));

            itemHolder.tvProductTitle.setText(Utils.capitlizeText(productItem.getProductname()));

            if (productItem.getDescription() != null && productItem.getDescription().trim().length() != 0) {
                itemHolder.tvDesciption.setVisibility(View.VISIBLE);
                itemHolder.tvDesciption.setText(Utils.capitlizeText(productItem.getDescription()));
            } else {
                itemHolder.tvDesciption.setVisibility(View.GONE);
            }
            itemHolder.tvTime.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(productItem.getCreatedTime()), context).replaceAll("about", "").trim());

            if (productItem.isFeatured > 0) {
                itemHolder.tvFeatured.setVisibility(View.VISIBLE);
            } else
                itemHolder.tvFeatured.setVisibility(View.GONE);

            if (productItem.getImagePath() != null && !productItem.getImagePath().isEmpty()) {
                itemHolder.pbLoader.setVisibility(View.VISIBLE);
                AppLogger.debug(TAG, "loading ...");
                Glide.with(context)
                        .load(productItem.getImagePath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                itemHolder.pbLoader.setVisibility(View.GONE);
                                AppLogger.debug(TAG, "loading ... on Exception");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                itemHolder.pbLoader.setVisibility(View.GONE);
                                AppLogger.debug(TAG, "loading ... ready");
                                return false;
                            }
                        })
                        .into(itemHolder.imvOfAds);
            } else {
                itemHolder.imvOfAds.setBackgroundColor(ContextCompat.getColor(context, R.color.image_background_color));
            }
            if (productItem.isLike > 0) {
                itemHolder.checkboxLike.setChecked(true);
                itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_hand));
            } else {
                itemHolder.checkboxLike.setChecked(false);
                itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_like));

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


    private void likeApi(final ViewHolder vh, final Product product, final int i, final int position) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId()
                , "", product.getProductid(), "" + i, "statics", "like_count");
        apiResponseCall.enqueue(new ApiCall((Activity) context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    product.isLike = i;
                    mItems.get(position).statics.likeCount = apiResponse.paylpad.statics.likeCount;
                    vh.checkboxLike.setEnabled(true);
                    notifyDataSetChanged();

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                vh.checkboxLike.setEnabled(true);
            }
        });


    }

    private void downloadApi(final Product product, int i, final int position) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId()
                , "", product.getProductid(), "" + i, "statics", "download_count");
        apiResponseCall.enqueue(new ApiCall((Activity) context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    mItems.get(position).statics.downloadCount = apiResponse.paylpad.statics.downloadCount;
                    //  mItems.get(position).statics.downloadCount++;
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }


    private void shareApi(final Product product, int i, final int position) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId()
                , "", product.getProductid(), "" + i, "statics", "share_count");
        apiResponseCall.enqueue(new ApiCall((Activity) context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    mItems.get(position).statics.shareCount = apiResponse.paylpad.statics.shareCount;
                    //  mItems.get(position).statics.downloadCount++;
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }


    private void viewCountApi(final Product product) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId()
                , "", product.getProductid(), "1", "statics", "view_count");
        apiResponseCall.enqueue(new ApiCall((Activity) context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }

}



