package com.example.lada.Utils;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.lada.R;
import com.example.lada.Remote.IFCMService;
import com.example.lada.Remote.RetrofitFCMClient;
import com.example.lada.lada.Common;
import com.example.lada.models.EventBus.NotifyToRiderEvent;
import com.example.lada.models.FCMSendData;
import com.example.lada.models.TokenModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UserUtils {
    public static void updateUser(View view , Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(Common.DRIVER_INFO_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(updateData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Snackbar.make(view,"Update Info Successfully",Snackbar.LENGTH_SHORT).show();

                    }
                });
    }

    public static void updateToken(Context context, String token) {

        TokenModel tokenModel = new TokenModel(token);
        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
    }

    public static void sendDeclineRequest(View view, Context context, String key) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //Get token
        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {

                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                            Map<String,String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTIFICATION_TITLE,Common.REQUEST_DRIVER_DECLINE);
                            notificationData.put(Common.NOTIFICATION_CONTENT,"Driver Decline Request");
                            notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0)
                                        {
                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.decline_failed),Snackbar.LENGTH_LONG).show();

                                        }
                                        else
                                        {
                                            Snackbar.make(view,context.getString(R.string.decline_success),Snackbar.LENGTH_LONG).show();
                                        }

                                    }, throwable -> {
                                        compositeDisposable.clear();
                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                    }));
                        }
                        else
                        {
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendAcceptRequestToRider(View view, Context context, String key, String tripNumberId) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {

                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                            Map<String,String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTIFICATION_TITLE,Common.REQUEST_DRIVER_ACCEPT);
                            notificationData.put(Common.NOTIFICATION_CONTENT,"Driver Accept Request");
                            notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());

                            notificationData.put(Common.TRIP_KEY, tripNumberId);

                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0)
                                        {
                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.accept_failed),Snackbar.LENGTH_LONG).show();

                                        }

                                    }, throwable -> {
                                        compositeDisposable.clear();
                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                    }));
                        }
                        else
                        {
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendNotifyToRider(Context context, View view, String key) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {

                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                            Map<String,String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTIFICATION_TITLE, context.getString(R.string.driver_arrived));
                            notificationData.put(Common.NOTIFICATION_CONTENT, context.getString(R.string.your_driver_arrived));
                            notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());

                            notificationData.put(Common.RIDER_KEY, key);

                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0)
                                        {
                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.accept_failed),Snackbar.LENGTH_LONG).show();

                                        }
                                        else
                                            EventBus.getDefault().postSticky(new NotifyToRiderEvent());

                                    }, throwable -> {
                                        compositeDisposable.clear();
                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                    }));
                        }
                        else
                        {
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendDeclineAndRemoveTripRequest(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //remove trip from firebase
        FirebaseDatabase.getInstance().getReference(Common.Trip)
                .child(tripNumberId)
                .removeValue()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        FirebaseDatabase
                                .getInstance()
                                .getReference(Common.TOKEN_REFERENCE)
                                .child(key)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists())
                                        {

                                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                                            Map<String,String> notificationData = new HashMap<>();
                                            notificationData.put(Common.NOTIFICATION_TITLE,Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP);
                                            notificationData.put(Common.NOTIFICATION_CONTENT,"Driver Decline Request");
                                            notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(fcmResponse -> {
                                                        if (fcmResponse.getSuccess() == 0)
                                                        {
                                                            compositeDisposable.clear();
                                                            Snackbar.make(view,context.getString(R.string.decline_failed),Snackbar.LENGTH_LONG).show();

                                                        }
                                                        else
                                                        {
                                                            Snackbar.make(view,context.getString(R.string.decline_success),Snackbar.LENGTH_LONG).show();
                                                        }

                                                    }, throwable -> {
                                                        compositeDisposable.clear();
                                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                                    }));
                                        }
                                        else
                                        {
                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        compositeDisposable.clear();
                                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

    }

    public static void sendCompleteTripToRider(View view, Context context, String key, String tripNumberId) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //remove trip from firebase
        FirebaseDatabase.getInstance().getReference(Common.Trip)
                .child(tripNumberId)
                .removeValue()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        FirebaseDatabase
                                .getInstance()
                                .getReference(Common.TOKEN_REFERENCE)
                                .child(key)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists())
                                        {

                                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                                            Map<String,String> notificationData = new HashMap<>();
                                            notificationData.put(Common.NOTIFICATION_TITLE,Common.RIDER_COMPLETE_TRIP);
                                            notificationData.put(Common.NOTIFICATION_CONTENT,"Trip Complete");
                                            notificationData.put(Common.TRIP_KEY,tripNumberId);


                                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(fcmResponse -> {
                                                        if (fcmResponse.getSuccess() == 0)
                                                        {
                                                            compositeDisposable.clear();
                                                            Snackbar.make(view,context.getString(R.string.complete_trip_failed),Snackbar.LENGTH_LONG).show();

                                                        }
                                                        else
                                                        {
                                                            Snackbar.make(view,context.getString(R.string.complete_trip_success),Snackbar.LENGTH_LONG).show();
                                                        }

                                                    }, throwable -> {
                                                        compositeDisposable.clear();
                                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                                    }));
                                        }
                                        else
                                        {
                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        compositeDisposable.clear();
                                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }
}
