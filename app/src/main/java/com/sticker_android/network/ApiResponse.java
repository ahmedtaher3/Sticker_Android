package com.sticker_android.network;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.message.Message;
import com.sticker_android.model.payload.Payload;

/**
 * Created by user on 12/4/17.
 */
public class ApiResponse {


    @SerializedName("success")
    public boolean success;

    @SerializedName("error")
    public Error error;

    @SerializedName("responseCode")
    public int responseCode;


    @SerializedName("status")
    public Boolean status;
    @SerializedName("authrized_status")
    public String authrizedStatus;
     @SerializedName("sql_error")
     public Object sqlError;
    @SerializedName("paylpad")
    public Payload paylpad;

    @SerializedName("message")
    public Message message;


}
