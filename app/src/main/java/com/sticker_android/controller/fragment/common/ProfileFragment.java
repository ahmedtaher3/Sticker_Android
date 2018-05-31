package com.sticker_android.controller.fragment.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sticker_android.R;
import com.sticker_android.controller.activities.corporate.home.CorporateHomeActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText firstName, lastName;
    private RelativeLayout rlBgProfile;
    private LinearLayout llCorporate;
    private AppPref appPref;
    private EditText edtCompanyName, edtCompanyAddress, edtProfileFirstName;
    private EditText edtProfileLastName, edtProfileEmail;
    private Button btnSubmit;
    private User user;
    private ImageView imvProfile;
    private String mCapturedImageUrl;
    public static final int PROFILE_CAMERA_IMAGE = 0;
    public static final int PROFILE_GALLERY_IMAGE = 1;
    private TextView personalProfile;
    private TextView tvCompanyDetails;
    private DisplayImageOptions mDisplayImageOptions;
    private OnFragmentProfileListener mListener;
    private File compressedImageFile;
    private String TAG=ProfileFragment.class.getSimpleName();

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FanHomeActivity) {
            FanHomeActivity profileActivity = (FanHomeActivity) context;
            profileActivity.setProfileFragmentReference(this);
        } else if (context instanceof CorporateHomeActivity) {
            CorporateHomeActivity profileActivity = (CorporateHomeActivity) context;
            profileActivity.setProfileFragmentReference(this);

        } else {
            DesignerHomeActivity profileActivity = (DesignerHomeActivity) context;
            profileActivity.setProfileFragmentReference(this);
        }

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .build();
        if (context instanceof OnFragmentProfileListener) {
            mListener = (OnFragmentProfileListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        setViewReferences(view);
        setViewListeners();
        setUserBackground();
        setUserData();
        return view;
    }

    private void setUserData() {
        /*ApiConstant.IMAGE_URl + user.getCompanyLogo()*/
        edtProfileFirstName.setText(user.getFirstName());
        edtProfileLastName.setText(user.getLastName());
        edtProfileEmail.setText(user.getEmail());
        edtProfileFirstName.setSelection(edtProfileFirstName.getText().length());
        edtProfileLastName.setSelection(edtProfileLastName.getText().length());
        edtProfileEmail.setSelection(edtProfileEmail.getText().length());
        AppLogger.debug("Profile", "Url is " + ApiConstant.IMAGE_URl + user.getCompanyLogo());
        if (user.getCompanyLogo() != null && !user.getCompanyLogo().isEmpty()) {

            imageLoader.displayImage(ApiConstant.IMAGE_URl + user.getCompanyLogo(), imvProfile, mDisplayImageOptions);
        }

       // imageLoader.displayImage(ApiConstant.IMAGE_URl + user.getCompanyLogo(), imvProfile, mDisplayImageOptions);

    }


    class Request implements RequestListener {

        public Request() {

        }

        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            AppLogger.debug("Profile", "Url is hsefcbjm");
            if (e != null)
                e.printStackTrace();
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            AppLogger.debug("Profile", "Url is hsefcbjm nvdfjv");

            return false;
        }

    }

        private void setUserBackground() {
        if (user.getUserType() != null) {
            UserTypeEnum userTypeEnum = Enum.valueOf(UserTypeEnum.class, user.getUserType().toUpperCase());

            switch (userTypeEnum) {
                case FAN:
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.fan_profile_bg_xhdpi));
                    llCorporate.setVisibility(View.GONE);
                    btnSubmit.setBackground(getResources().getDrawable(R.drawable.fan_btn_background));
                    personalProfile.setTextColor(getResources().getColor(R.color.colorFanText));
                    tvCompanyDetails.setVisibility(View.GONE);
                    imvProfile.setImageResource(R.drawable.fan_hdpi);
                    break;
                case DESIGNER:
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.designer_profile_bg_hdpi));
                    llCorporate.setVisibility(View.GONE);
                    btnSubmit.setBackground(getResources().getDrawable(R.drawable.designer_btn_background));
                    personalProfile.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    tvCompanyDetails.setVisibility(View.GONE);
                    imvProfile.setImageResource(R.drawable.designer_hdpi);
                    break;
                case CORPORATE:
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.profile_bg_hdpi));
                    llCorporate.setVisibility(View.VISIBLE);
                    edtCompanyName.setText(user.getCompanyName());
                    edtCompanyAddress.setText(user.getCompanyAddress());
                    edtCompanyName.setSelection(edtCompanyName.getText().length());
                    edtCompanyAddress.setSelection(edtCompanyAddress.getText().length());
                    imageLoader.displayImage(ApiConstant.IMAGE_URl + user.getCompanyLogo(), imvProfile, mDisplayImageOptions);
                    btnSubmit.setBackground(getResources().getDrawable(R.drawable.corporate_btn_background));
                    personalProfile.setTextColor(getResources().getColor(R.color.corporateBtnBackground));
                    tvCompanyDetails.setVisibility(View.VISIBLE);
                    imvProfile.setImageResource(R.drawable.corporate_hdpi);
                    break;
            }
        }
    }

    private void init() {
        appPref = new AppPref(getActivity());
        user = appPref.getUserInfo();

    }

    @Override
    protected void setViewListeners() {
        btnSubmit.setOnClickListener(this);
        imvProfile.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences(View view) {
        rlBgProfile = (RelativeLayout) view.findViewById(R.id.bgProfile);
        llCorporate = (LinearLayout) view.findViewById(R.id.llCorporate);
        edtCompanyName = (EditText) view.findViewById(R.id.act_profile_edt_company_name);
        edtCompanyAddress = (EditText) view.findViewById(R.id.act_profile_edt_company_address);
        edtProfileFirstName = (EditText) view.findViewById(R.id.act_profile_edt_first_name);
        edtProfileLastName = (EditText) view.findViewById(R.id.act_profile_edt_last_name);
        edtProfileEmail = (EditText) view.findViewById(R.id.act_profile_edt_email);
        btnSubmit = (Button) view.findViewById(R.id.act_profile_btn_register);
        imvProfile = (ImageView) view.findViewById(R.id.imvProfile);
        personalProfile = (TextView) view.findViewById(R.id.frag_profile_tv_personal);
        tvCompanyDetails = (TextView) view.findViewById(R.id.tvCompanyDetails);
    }

    @Override
    protected boolean isValidData() {
        if (user.getUserType().equals("corporate")) {
            if (edtCompanyName.getText().toString().trim().isEmpty()) {
                Utils.showToast(getActivity(), getResources().getString(R.string.txt_enter_company_name));

                //   CommonSnackBar.show(edtCompanyName, "Company name cannot be empty", Snackbar.LENGTH_SHORT);
                this.edtCompanyName.requestFocus();
                return false;
            } else if (edtCompanyAddress.getText().toString().trim().isEmpty()) {
                Utils.showToast(getActivity(), getResources().getString(R.string.txt_please_enter_company_address));

                //   CommonSnackBar.show(edtCompanyAddress, "Company address cannot be empty", Snackbar.LENGTH_SHORT);
                this.edtCompanyAddress.requestFocus();
                return false;
            }
        }

        String firstName = this.edtProfileFirstName.getText().toString().trim();
        String lastName = this.edtProfileLastName.getText().toString().trim();
        String email = this.edtProfileEmail.getText().toString().trim();
        if (firstName.isEmpty()) {
            Utils.showToast(getActivity(), getString(R.string.first_name_cannot_be_empty));
            this.edtProfileFirstName.requestFocus();
            return false;
        } else if (firstName.length() < 3) {
            Utils.showToast(getActivity(), getString(R.string.first_name_cannot_be_less_than));

            //  CommonSnackBar.show(edtFirstName, getString(R.string.first_name_cannot_be_less_than), Snackbar.LENGTH_SHORT);
            this.edtProfileFirstName.requestFocus();
            return false;

        } else if (lastName.isEmpty()) {
            Utils.showToast(getActivity(), getString(R.string.last_name_cannot_be_empty));
            this.edtProfileLastName.requestFocus();
            return false;

        } else if (lastName.length() < 3) {
            Utils.showToast(getActivity(), getString(R.string.last_name_cannot_be_less_than));

            //  CommonSnackBar.show(edtFirstName, getString(R.string.first_name_cannot_be_less_than), Snackbar.LENGTH_SHORT);
            this.edtProfileLastName.requestFocus();
            return false;

        } else if (email.isEmpty()) {
            Utils.showToast(getActivity(), getString(R.string.msg_email_cannot_be_empty));
            this.edtProfileEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Utils.showToast(getActivity(), getString(R.string.msg_email_not_valid));
            this.edtProfileEmail.requestFocus();
            return false;
        } else
            return true;
    }


    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(getActivity());
        switch (v.getId()) {
            case R.id.act_profile_btn_register:
                if (isValidData()) {
                    updateProfileApi();
                }
                break;
            case R.id.imvProfile:
                Utils.showAlertDialogToGetPic(getActivity(), new ImagePickerListener() {
                    @Override
                    public void pickFromGallery() {
                        pickGalleryImage();
                    }

                    @Override
                    public void captureFromCamera() {
                        captureImage();
                    }

                    @Override
                    public void selectedItemPosition(int position) {

                    }
                });
                break;
        }
    }

    private void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(getActivity(), System.currentTimeMillis() + "");
        mCapturedImageUrl = file.getAbsolutePath();
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        getActivity().startActivityForResult(takePicture, PROFILE_CAMERA_IMAGE);
    }

    private void pickGalleryImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(openGallery, PROFILE_GALLERY_IMAGE);
    }

    private void updateProfileApi() {
        if (user.getId() != null) {
            final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(getActivity());
            progressDialogHandler.show();
            Call<ApiResponse> apiResponseCall = RestClient.getService().updateProfile(user.getId(), edtCompanyName.getText().toString(),
                    user.getAuthrizedKey(), edtCompanyAddress.getText().toString(), edtProfileFirstName.getText().toString(), edtProfileLastName.getText().toString(),
                    edtProfileEmail.getText().toString(), user.getUserType());
            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    progressDialogHandler.hide();
                    if (apiResponse.status) {
                        appPref.saveUserObject(null);
                        appPref.saveUserObject(apiResponse.paylpad.getData());
                        appPref.setLoginFlag(true);
                        if (mListener != null)
                            mListener.updatedata();
                        Utils.showToast(getActivity(), getString(R.string.txt_profile_updated_successfully));
                        getActivity().onBackPressed();
                        //  CommonSnackBar.show(edtCompanyAddress, "Data updated successfully", Snackbar.LENGTH_SHORT);

                    } else {
                        Utils.showToast(getActivity(), apiResponse.error.message);
                        // CommonSnackBar.show(edtCompanyAddress, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    progressDialogHandler.hide();
                }
            });

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Profile fragment", "on activity");
        switch (requestCode) {

            case PROFILE_CAMERA_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (mCapturedImageUrl != null) {
                        openCropActivity(mCapturedImageUrl);
                        //uploadImage();
                    }
                }
                break;

            case PROFILE_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(getActivity(), selectedImage);
                    File file = Utils.getCustomImagePath(getActivity(), "temp");
                    mCapturedImageUrl = file.getAbsolutePath();
                    mCapturedImageUrl = sourceUrl;
                    openCropActivity(sourceUrl);
                    //uploadImage();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imageLoader.displayImage(resultUri.toString(), imvProfile, mDisplayImageOptions);
                    mCapturedImageUrl = resultUri.getPath();

                  /*Compress code added*/
                    try {
/*     File file = new File(getRealPathFromURI(resultUri));
                   */
                        File file = new File(mCapturedImageUrl);
                        AppLogger.debug(TAG, "Size is before compress in unit " + Utils.getFileSize(file));
                        compressedImageFile = new Compressor(getActivity()).compressToFile(file);
                        AppLogger.debug(TAG, "Size is after compress " + Integer.parseInt(String.valueOf(compressedImageFile.length() / 1024)));
                        AppLogger.debug(TAG, "Size is after compress in unit " + Utils.getFileSize(compressedImageFile));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCapturedImageUrl = compressedImageFile.getAbsolutePath();
                    uploadImage();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
        }
    }

    private void openCropActivity(String url) {
        CropImage.activity(Uri.fromFile(new File(url)))
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .start(getActivity());
    }

    public void uploadImage() {
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(getActivity());
        progressDialogHandler.show();
        File file = new File(mCapturedImageUrl);
        MultipartBody.Part body = MultipartBody.Part.createFormData("company_logo", file.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file));

        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(user.getId()));
        RequestBody languageId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(user.getLanguageId()));
        RequestBody authKey = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(user.getAuthrizedKey()));

        Call<ApiResponse> apiResponseCall = RestClient.getService().profileImage(userId, languageId, authKey, body);
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    Utils.showToast(getActivity(), getString(R.string.txt_data_save_successfully));
                    user.setImageUrl(apiResponse.paylpad.getData().getCompanyLogo());
                    User userNew = new User();
                    userNew = user;
                    userNew.setCompanyLogo(apiResponse.paylpad.getData().getCompanyLogo());
                    appPref.saveUserObject(null);
                    appPref.saveUserObject(userNew);
                    imageLoader.displayImage("file://" + mCapturedImageUrl, imvProfile, mDisplayImageOptions);
                    if (mListener != null)
                        mListener.updatedata();

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
    }

    public interface OnFragmentProfileListener {
        public void updatedata();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
