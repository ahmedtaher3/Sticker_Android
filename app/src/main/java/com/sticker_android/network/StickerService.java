package com.sticker_android.network;


import com.sticker_android.model.NewResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by monika on 19/4/17.
 */
public interface StickerService {

    @FormUrlEncoded
    @POST(ApiConstant.API_LOGIN_URL)
    public Call<ApiResponse> userLogin(@Field("language_id") int languageId, @Field("email") String email_id, @Field("password") String password, @Field("device_type") String deviceType
            , @Field("device_token") String deviceToken,
                                       @Field("device_udid") String deviceId, @Field("user_type") String accountType);

    @FormUrlEncoded
    @POST(ApiConstant.API_REGISTER)
    public Call<ApiResponse> userRegistration(@Field("language_id") int languageId, @Field("email") String email_id, @Field("password") String password, @Field("first_name") String firstName, @Field("last_name") String lastName
            , @Field("user_type") String accountType, @Field("device_type") String deviceType,
                                              @Field("device_token") String deviceToken,
                                              @Field("device_udid") String deviceId);

    @FormUrlEncoded
    @POST(ApiConstant.API_FORGOT_PASSWORD)
    public Call<ApiResponse> forgotPassword(@Field("email") String email_id);

    @FormUrlEncoded
    @POST(ApiConstant.API_CHANGE_PASSWORD)
    public Call<ApiResponse> changePassword(@Field("id") String userId, @Field("password") String password, @Field("authrized_key") String authKey);

    @FormUrlEncoded
    @POST(ApiConstant.API_PROFILE)
    public Call<ApiResponse> updateProfile(@Field("id") String userId, @Field("company_name") String companyName, @Field("authrized_key") String authKey
            , @Field("company_address") String companyAddress, @Field("first_name") String firstName,
                                           @Field("last_name") String lastName, @Field("email") String email, @Field("user_type") String userType);

    @Multipart
    @POST(ApiConstant.API_PROFILE_IMAGE)
    public Call<ApiResponse> profileImage(@Part("id") RequestBody requestBody,
                                          @Part("language_id") RequestBody requestLanguageId,
                                          @Part("authrized_key") RequestBody requestAuthKey,
                                          @Part MultipartBody.Part part);



    @Multipart
    @POST(ApiConstant.API_CREATE_NEW_VOTE_2)
    public Call<ApiResponse> createNewVote_2
            (@Part("user_id") RequestBody user_id,
             @Part("category_id") RequestBody category_id,
             @Part("vote_desc") RequestBody vote_desc,
             @Part("first_choice_desc") RequestBody first_choice_desc,
             @Part MultipartBody.Part IMG1,
             @Part MultipartBody.Part IMG2,
             @Part("second_choice_desc") RequestBody second_choice_desc);

    @FormUrlEncoded
    @POST(ApiConstant.API_VOTE)
    public Call<ApiResponse> userVote (@Field("user_id") String user_id, @Field("vote_id") String vote_id, @Field("vote") String vote);


    @FormUrlEncoded
    @POST(ApiConstant.API_CHANGE_LANGUAGE)
    public Call<ApiResponse> changeLanguage(@Field("id") String userId, @Field("language_id") int languageId, @Field("authrized_key") String authKey);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_CONTENT)
    public Call<ApiResponse> apiGetContent(@Field("id") String userId);

    @FormUrlEncoded
    @POST(ApiConstant.API_ADD_PRODUCT)
    public Call<ApiResponse> apiAddProduct(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                           @Field("user_id") String userId, @Field("product_name") String productname,
                                           @Field("type") String type, @Field("description") String description,
                                           @Field("expiry_date") String expireDate
            , @Field("image_path") String imagePath, @Field("product_id") String productId, @Field("category_id") int categoryId);

    @FormUrlEncoded
    @POST(ApiConstant.API_ADD_PRODUCT)
    public Call<ApiResponse> apiAddProduct(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                           @Field("user_id") String userId, @Field("product_name") String productname,
                                           @Field("type") String type, @Field("description") String description,
                                           @Field("expiry_date") String expireDate
            , @Field("image_path") String imagePath, @Field("product_id") String productId, @Field("category_id") int categoryId, @Field("key_name") String keyName);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_PRODUCT_LIST)
    public Call<ApiResponse> apiGetProductList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                               @Field("user_id") String userId, @Field("index") int index,
                                               @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("search") String search, @Field("status") String status);


    @FormUrlEncoded
    @POST(ApiConstant.API_DELETE_PRODUCT)
    public Call<ApiResponse> apiDeleteProduct(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                              @Field("user_id") String userId, @Field("product_id") String productId);

    @FormUrlEncoded
    @POST(ApiConstant.API_SEARCH_PRODUCT)
    public Call<ApiResponse> apiSearchProduct(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                              @Field("user_id") String userId, @Field("index") int index,
                                              @Field("limit") int limit, @Field("type") String type, @Field("search") String search, @Field("key_name") String name);


    @FormUrlEncoded
    @POST(ApiConstant.API_FETCH_CORPORATE_CATEGORY_LIST)
    public Call<ApiResponse> apiCorporateCategoryList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                      @Field("user_id") String userId, @Field("key_name") String name);


    @FormUrlEncoded
    @POST(ApiConstant.API_FETCH_NOTIFICATION_LIST)
    public Call<ApiResponse> apiNotificationList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                 @Field("user_id") String userId, @Field("key_name") String name);


    @FormUrlEncoded
    @POST(ApiConstant.API_SAVE_USER_CONTEST)
    public Call<ApiResponse> saveUserContest(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                             @Field("user_id") String userId, @Field("product_id") int productId, @Field("contest_id") int contestId, @Field("key_name") String name, @Field("notification_id") long notificationId);


    @FormUrlEncoded
    @POST(ApiConstant.API_GET_USER_CONTEST_LIST)
    public Call<ApiResponse> getUserContestList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                @Field("user_id") String userId, @Field("key_name") String name);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_FAN_PRODUCT_LIST)
    public Call<ApiResponse> getFanHomeProductList
            (@Field("language_id") String languageId, @Field("authrized_key") String authKey,
             @Field("user_id") String userId, @Field("index") int index,
             @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("search") String search, @Field("category_id") String categoryId, @Field("filter") String filter);


    @FormUrlEncoded
    @POST(ApiConstant.API_GET_FAN_PRODUCT_LIST_new)
    public Call<ApiResponse> getFanHomeProductList_new
            (@Field("language_id") String languageId,
              @Field("index") int index,
             @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("search") String search, @Field("category_id") String categoryId, @Field("filter") String filter);

    @FormUrlEncoded
    @POST(ApiConstant.API_SAVE_PRODUCT_LIKE)
    public Call<ApiResponse> apiSaveProductLike(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
             @Field("user_id") String userId, @Field("user_contest_id") String userContestId,
             @Field("product_id") int productId, @Field("status") String status, @Field("key_name") String name, @Field("type") String type);




    @FormUrlEncoded
    @POST(ApiConstant.API_GET_FAN_CONTEST_LIST)
    public Call<ApiResponse> getFanContestList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                               @Field("user_id") String userId, @Field("key_name") String keyName);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_FAN_ALL_CONTEST_LIST)
    public Call<ApiResponse> getFanAllContestList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                  @Field("user_id") String userId, @Field("contest_id") long contestId, @Field("index") int index,
                                                  @Field("limit") int limit, @Field("key_name") String keyName);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_FAN_DOWNLOAD_LIST)
    public Call<ApiResponse> getFanDownloads
            (@Field("language_id") String languageId, @Field("authrized_key") String authKey,
             @Field("user_id") String userId, @Field("index") int index,
             @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("search") String search);


    @FormUrlEncoded
    @POST(ApiConstant.API_GET_USER_PENDING_LIST)
    public Call<ApiResponse> getUserPendingList
            (@Field("language_id") String languageId, @Field("authrized_key") String authKey,
             @Field("user_id") String userId, @Field("index") int index,
             @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("status") String status);


    @FormUrlEncoded
    @POST(ApiConstant.API_GET_USER_COMPLETED_LIST)
    public Call<ApiResponse> getUserCompletedList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                  @Field("user_id") String userId, @Field("key_name") String name);

    @FormUrlEncoded
    @POST(ApiConstant.API_FILTER_LIST)
    public Call<ApiResponse> apiFilterList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                           @Field("user_id") String userId, @Field("index") int index,
                                           @Field("limit") int limit, @Field("search") String search, @Field("key_name") String name, @Field("type") String type);


    @FormUrlEncoded
    @POST(ApiConstant.API_FILTER_LIST_new)
    public Call<ApiResponse> apiFilterList_new(@Field("language_id") String languageId,
                                               @Field("index") int index,
                                               @Field("limit") int limit, @Field("search") String search,
                                               @Field("key_name") String name, @Field("type") String type);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_VOTES)
    public Call<NewResponse> getvotes(@Field("user_id") String user_id);




    @FormUrlEncoded
    @POST(ApiConstant.API_SAVE_PROJECT_REJECTION)
    public Call<ApiResponse> apiSaveProjectRejection(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                     @Field("user_id") String userId, @Field("product_name") String productname,
                                                     @Field("type") String type, @Field("description") String description,
                                                     @Field("expiry_date") String expireDate
            , @Field("image_path") String imagePath, @Field("product_id") String productId, @Field("description_reject") String descriptionReject, @Field("category_id") int categoryId, @Field("key_name") String keyName);


    @FormUrlEncoded
    @POST(ApiConstant.API_DELETE_NOTIFICATION)
    public Call<ApiResponse> deleteNotification(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                @Field("user_id") String userId, @Field("notification_id") long notificationId);


    @FormUrlEncoded
    @POST(ApiConstant.API_USER_LOGOUT)
    public Call<ApiResponse> userLogout(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                        @Field("user_id") String userId);


    @FormUrlEncoded
    @POST(ApiConstant.API_GET_PRODUCT_LIST_WITH_CONTEST)
    public Call<ApiResponse> apiGetProductWithContestList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                          @Field("user_id") String userId, @Field("index") int index,
                                                          @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("search") String search, @Field("status") String status);

    @FormUrlEncoded
    @POST(ApiConstant.API_MY_DOWNLOAD_LIST)
    public Call<ApiResponse> getCustomizeImageList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                   @Field("user_id") String userId, @Field("index") int index,
                                                   @Field("limit") int limit, @Field("key_name") String keyName);

    @FormUrlEncoded
    @POST(ApiConstant.API_SAVE_CUSTOMIZED_IMAGE)
    public Call<ApiResponse> saveCustomizeImage(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                @Field("user_id") String userId, @Field("image_url") String imageUrl);

    @FormUrlEncoded
    @POST(ApiConstant.API_DELETE_CUSTOMIZED_IMAGE)
    public Call<ApiResponse> deleteCustomizeImage(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                  @Field("user_id") String userId, @Field("user_my_id") String userMyId);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_USER_PRODUCT_CONTEST_LIST)
    public Call<ApiResponse> getUserContestProductList(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                       @Field("user_id") String userId, @Field("contest_id") String contestId, @Field("key_name") String name, @Field("index") int index,
                                                       @Field("limit") int limit);

    @FormUrlEncoded
    @POST(ApiConstant.API_GET_FAN_PRODUCT_LIST_ALL)
    public Call<ApiResponse> getAllProductWithFeature
            (@Field("language_id") String languageId, @Field("authrized_key") String authKey,
             @Field("user_id") String userId, @Field("index") int index,
             @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("search") String search, @Field("category_id") String categoryId, @Field("filter") String filter);


    @FormUrlEncoded
    @POST(ApiConstant.API_CREATE_NEW_VOTE)
    public Call<ApiResponse> createNewVote
            (@Field("user_id") String user_id, @Field("category_id") String category_id,
             @Field("vote_desc") String vote_desc, @Field("first_choice_desc") String first_choice_desc, @Field("first_choice_img") String first_choice_img,
             @Field("second_choice_img") String second_choice_img, @Field("second_choice_desc") String second_choice_desc);


    @FormUrlEncoded
    @POST(ApiConstant.API_GET_FAN_PRODUCT_LIST_ALL_new)
    public Call<ApiResponse> getAllProductWithFeature_new
            (@Field("language_id") String languageId,
             @Field("index") int index,
             @Field("limit") int limit, @Field("type") String type, @Field("key_name") String name, @Field("search") String search, @Field("category_id") String categoryId, @Field("filter") String filter);


    @FormUrlEncoded
    @POST(ApiConstant.API_FILTER_LIST__FAN_All)
    public Call<ApiResponse> apiFilterListCorporateAndDesigner(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                           @Field("user_id") String userId, @Field("index") int index,
                                           @Field("limit") int limit, @Field("search") String search, @Field("key_name") String name, @Field("type") String type);

    @FormUrlEncoded
    @POST(ApiConstant.API_FILTER_LIST__FAN_All_new)
    public Call<ApiResponse> apiFilterListCorporateAndDesigner_new(@Field("language_id") String languageId,
                                                              @Field("index") int index,
                                                               @Field("limit") int limit, @Field("search") String search, @Field("key_name") String name, @Field("type") String type);


    @FormUrlEncoded
    @POST(ApiConstant.API_GET_RANDOM_FEATURED_PRODUCT)
    public Call<ApiResponse> getRandomFeaturedProduct(@Field("language_id") String languageId, @Field("authrized_key") String authKey,
                                                       @Field("user_id") String userId, @Field("key_name") String name);




    @FormUrlEncoded
    @POST(ApiConstant.API_UPDATE_LATEST_VERSION)
    public Call<ApiResponse> checkVersion(@Field("key_name")String keyName);




}
