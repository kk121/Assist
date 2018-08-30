package com.krishna.assist.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    String FCM_KEY = "AAAA4Ubio1Q:APA91bGWkw84b1Pw2nnnOKn8MO25U2giLRtv5TUkXidojFluZk_qKOGllS27oMZZV5goTQdwRtpdmvI1iAPRZZDNKz6c-mpU6nvHZJ-Jg9f1fQ5NdttftqUpqwAkObLEED26VFDDbXN8";

    @Headers({"Authorization: key=" + FCM_KEY,
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendPushNotification(@Body RequestNotificaton requestNotificaton);
}
