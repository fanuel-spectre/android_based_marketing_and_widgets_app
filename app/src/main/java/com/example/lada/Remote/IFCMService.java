package com.example.lada.Remote;

import com.example.lada.models.FCMResponse;
import com.example.lada.models.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAw5e7eHA:APA91bEiK4DG5DyR6IhWfNikdx9_ScG0797VQk6rV_gXg-CF5AfDh0kSlVR3qnyQQ2zEhJVoMOwS9-Kz9vhYTug2xc75KdPLdCrthuauPs45W9pyahNLfYtguq0FJ6zxxb8CI_LXW1GQ"
    })

    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
