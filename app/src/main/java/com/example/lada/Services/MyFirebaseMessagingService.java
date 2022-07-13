package com.example.lada.Services;

import com.example.lada.Utils.UserUtils;
import com.example.lada.lada.Common;
import com.example.lada.models.EventBus.DriverRequestRecieved;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (FirebaseAuth.getInstance().getCurrentUser() !=null)
            UserUtils.updateToken(this, token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String,String> dataReceived = message.getData();
        if (dataReceived !=null)
        {

            if (dataReceived.get(Common.NOTIFICATION_TITLE).equals(Common.REQUEST_DRIVER_TITLE))
            {
                DriverRequestRecieved driverRequestRecieved = new DriverRequestRecieved();
                driverRequestRecieved.setKey(dataReceived.get(Common.RIDER_KEY));
                driverRequestRecieved.setPickupLocation(dataReceived.get(Common.RIDER_PICKUP_LOCATION));
                driverRequestRecieved.setPickupLocationString(dataReceived.get(Common.RIDER_PICKUP_LOCATION_STRING));
                driverRequestRecieved.setDestinationLocation(dataReceived.get(Common.RIDER_DESTINATION));
                driverRequestRecieved.setDestinationLocationString(dataReceived.get(Common.RIDER_DESTINATION_STRING));

                EventBus.getDefault().postSticky(driverRequestRecieved);

            }else {
                Common.showNotification(this, new Random().nextInt(),
                        dataReceived.get(Common.NOTIFICATION_TITLE),
                        dataReceived.get(Common.NOTIFICATION_CONTENT), null);
            }
        }

    }
}
