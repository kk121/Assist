package com.krishna.assist.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers({"Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendPushNotification(@Header("Authorization") String fcmKey, @Body RequestNotificaton requestNotificaton);
}
