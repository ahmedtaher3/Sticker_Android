package com.sticker_android.controller.activities.corporate;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.CorporateCategory;
import com.sticker_android.model.corporateproduct.ProductList;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.CategoryAdapter;
import com.sticker_android.view.NothingSelectedSpinnerAdapter;
import com.sticker_android.view.SetDate;

import java.util.ArrayList;

import retrofit2.Call;

public class RenewAdandProductActivity extends AppBaseActivity implements View.OnClickListener {

    private AppPref appPref;
    private User userdata;
    private Toolbar toolbar;
    private Button btnRePost;
    private EditText edtExpireDate;
    private EditText edtCorpName, edtDescription;
    private ProductList productObj;
    private String mExpireDate;
    private SetDate setDate;
    private String type="";
    private Spinner spnrCategory;
    private ArrayList<CorporateCategory> corporateCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_adand_product);
        init();
        getuserInfo();
        getProductData();
        setToolbar();
        setViewReferences();
        setViewListeners();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setProductdataIntoView();
        setButtonText();
        setExpireDate();
        fetchCategoryApi();
    }

    /**
     * Method is used to fetch the category api
     */
    private void fetchCategoryApi() {

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiCorporateCategoryList(userdata.getLanguageId(), userdata.getAuthrizedKey()
                , userdata.getId(), "corporate_category");

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    corporateCategories = apiResponse.paylpad.corporateCategories;
                    setSpinnerAdaptor();
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
    }


    /**
     * Method is used to set the category in spinner
     */
    private void setSpinnerAdaptor() {

        if (corporateCategories != null) {
            CategoryAdapter categoryAdapter=new CategoryAdapter(this,corporateCategories);
            //  ArrayAdapter<CorporateCategory> adapter = new ArrayAdapter<CorporateCategory>(this, android.R.layout.simple_spinner_item, corporateCategories);
            //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrCategory.setAdapter(
                    new NothingSelectedSpinnerAdapter(
                            categoryAdapter,
                            R.layout.spinner_without_selection,
                            this));
        }
    }

    private void setExpireDate() {
        setDate = new SetDate(edtExpireDate, this);
        setDate.setDate(productObj.getExpireDate());
        setDate.setMinDate(productObj.getExpireDate());
    }

    private void setButtonText() {

    if(type.equals("Edit")){
        btnRePost.setText("Update");
    }else{
        edtDescription.setLongClickable(false);
        edtDescription.setEnabled(false);
        edtCorpName.setLongClickable(false);
        edtCorpName.setEnabled(false);
        btnRePost.setText("Repost");
    }
    }

    private void setProductdataIntoView() {

        if (productObj != null) {

            edtExpireDate.setText(Utils.dateModify(productObj.getExpireDate()));
            edtCorpName.setText(productObj.getProductname());
            edtDescription.setText(productObj.getDescription());
            edtDescription.setSelection(edtDescription.getText().length());
            edtCorpName.setSelection(edtCorpName.getText().length());
            mExpireDate=productObj.getExpireDate();
        }
    }


    private void getProductData() {

        if (getIntent().getExtras() != null) {

            productObj = getIntent().getExtras().getParcelable(AppConstant.PRODUCT_OBJ_KEY);
            type=getIntent().getExtras().getString("edit");
        }
    }

    @Override
    protected void setViewListeners() {

        btnRePost.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {

        btnRePost          =   (Button) findViewById(R.id.act_corp_add_new_btn_re_post);
        edtExpireDate      =   (EditText) findViewById(R.id.act_add_new_ad_corp_edt_expire_date);
        edtDescription     =   (EditText) findViewById(R.id.act_add_new_ad_corp_edt_description);
        edtCorpName        =   (EditText) findViewById(R.id.act_add_new_corp_edt_name);
        spnrCategory       =   (Spinner)findViewById(R.id.spnrCategory);
    }

    @Override
    protected boolean isValidData() {

        if (edtCorpName.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, "Please enter a name.");
            return false;
        } else if (edtExpireDate.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, "Please enter a expire date.");

            return false;
        } else if (edtDescription.getText().toString().trim().isEmpty()) {
            return false;
        } /*else if (productObj.getExpireDate().trim().equals(mExpireDate)) {
            Utils.showToast(this, "Please select a valid date.");
            return false;
        }*/
        return true;
    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }

    /**
     * Method is used to set the toolbar title
     */
    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(type);
        toolbar.setTitle(" ");
    }

    /**
     * Method is used to set the toolbar
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarBackground();
        setToolBarTitle();
        setSupportActionBar(toolbar);
    }

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }


    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.act_corp_add_new_btn_re_post:
                if (isValidData()) {
                    renewOrEditApi();
                }

        }
    }


    /**
     * Method is used to call the add ads or product api
     */
    private void renewOrEditApi() {
        if(setDate!=null)
        mExpireDate = setDate.getChosenDate();
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        final String type = productObj.getType();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiAddProduct(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), edtCorpName.getText().toString().trim(), type, edtDescription.getText().toString().trim()
                , mExpireDate, "", String.valueOf(productObj.getProductid()));

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    Utils.showToast(getApplicationContext(), type + " added successfully.");
                    setResult(RESULT_OK);
                    onBackPressed();
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
    }

}
