package com.chat.Interfaces;

import com.chat.Notifications.MyResponse;
import com.chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                        "Content-Type:application/json",
                        "Authorization:key=AAAAnFyCXcA:APA91bEmDA1Sb8vp6zxYSIf8-K5Qj32_VkizFsWoTuwPw5mOwKwlw2WQ_Ib71Oh2NIIWTo3Wpd12PzIJm_23PuuMxkpldMRlIuppk-rQFinrDGrupVVhDWpP1E3--Ii_9rSS0Irn5yHM",
           }
            )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
