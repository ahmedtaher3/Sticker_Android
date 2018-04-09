package com.sticker_android.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root .
 */
public class JsonParser {


    /**
     * Parse json for title
     * @param Json
     * @return
     */
    public static String getTitle(String Json) {

        String status = "";

        if (Json != null) {
            try {
                //create a json object
                JSONObject jsonObject = new JSONObject(Json);
                status = jsonObject.getString("title");

            } catch (JSONException e) {
            }
        }

        return status;
    }
}
