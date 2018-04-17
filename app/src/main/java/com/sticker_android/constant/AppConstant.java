package com.sticker_android.constant;

import com.amazonaws.regions.Regions;

/**
 * Created by root on 21/2/18.
 */

public class AppConstant {
    public static final int SPLASH_TIMER_WAIT=3000;
    /*Intent Constant*/
    public static final String PRODUCT_OBJ_KEY="productObj";
    public static final String PRODUCT = "product";
    public static final String DATA_REFRESH_NEEDED = "data_refresh_needed";
    public static final String NOTIFICATION_OBJ="notificationObj";

    public static final int INTENT_NOTIFICATION_CODE =10 ;

    public static final int INTENT_RENEW_CODE =12 ;
    public static final int INTENT_PRODUCT_DETAILS =121 ;
    public static final String BUCKET_IMAGE_BASE_URL = "https://s3.ap-south-1.amazonaws.com/sportwidget/";

    /*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     */
    public static final String COGNITO_POOL_ID = "us-east-1:1bfb6922-f05c-4a93-8b4d-9212e1748e9e";

    /*
     * Region of your Cognito identity pool ID.
     */
    public static final String COGNITO_POOL_REGION = Regions.US_EAST_1.getName();

    /*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     */
    public static final String BUCKET_NAME = "sportwidget";

    /*
     * Region of your bucket.
     */
    public static final String BUCKET_REGION = Regions.AP_SOUTH_1.getName();
}