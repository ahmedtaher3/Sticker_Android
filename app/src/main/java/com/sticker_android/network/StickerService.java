package com.sticker_android.network;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by monika on 19/4/17.
 */
public interface StickerService {

    /*@FormUrlEncoded
    @POST(ChatConstant.ACTION_RESTORE_MESSAGE_LIST)
    Call<ApiResponse> restoreMessageList(@Field("user_id") String user_id,

                                         @Field("restore_media") int restore_media);*/

  /*  @FormUrlEncoded
    @POST(ApiConstant.API_LOGIN_URL)
    Call<ApiResponse> testApi(@Header("Authorization") String token,"vivekp@gmail.com","");
*/
  @FormUrlEncoded
  @POST(ApiConstant.API_LOGIN_URL)
  public Call<ApiResponse> userLogin(@Field("email") String email_id, @Field("password") String password, @Field("device_type") String deviceType
                                           , @Field("device_token") String deviceToken,
                                            @Field("device_udid") String deviceId,@Field("user_type") String accountType);
  @FormUrlEncoded
  @POST(ApiConstant.API_REGISTER)
  public Call<ApiResponse> userRegistration(@Field("language_id")int languageId,@Field("email") String email_id,@Field("password") String password,@Field("first_name") String firstName, @Field("last_name") String lastName
          , @Field("user_type") String accountType,@Field("device_type") String deviceType,
                                             @Field("device_token") String deviceToken,
                                            @Field("device_udid") String deviceId);
  @FormUrlEncoded
  @POST(ApiConstant.API_FORGOT_PASSWORD)
  public Call<ApiResponse> forgotPassword(@Field("email") String email_id);
  @FormUrlEncoded
  @POST(ApiConstant.API_CHANGE_PASSWORD)
  public Call<ApiResponse> changePassword(@Field("id") String userId,@Field("password")String password,@Field("authrized_key")String authKey);

  @FormUrlEncoded
  @POST(ApiConstant.API_PROFILE)
  public Call<ApiResponse> updateProfile(@Field("id") String userId,@Field("company_name")String companyName,@Field("authrized_key")String authKey
  ,@Field("company_address")String companyAddress,@Field("first_name")String firstName,
  @Field("last_name")String lastName,@Field("email")String email,@Field("user_type")String userType);

}
