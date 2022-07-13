package com.example.lada.lada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.lada.HomeDriversActivity;
import com.example.lada.R;
import com.example.lada.Utils.UserUtils;
import com.example.lada.models.DriverInfoModel;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressLint("CustomSplashScreen")
public class LadaSplashActivity extends AppCompatActivity {

    private final static int LOGIN_REQUEST_CODE = 7070;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

//    @BindView(R.id.progress_bar)
//    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference driverInfoRef;


    @Override
    protected void onStart() {
        super.onStart();
        delaySplashScreen();
    }

    @Override
    protected void onStop() {
        if (firebaseAuth !=null && listener !=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lada_splash);

        initialize();
    }

    private void initialize() {

        ButterKnife.bind(this);


        database = FirebaseDatabase.getInstance();
        driverInfoRef = database.getReference(Common.DRIVER_INFO_REFERENCE);

        providers = Arrays.asList( new AuthUI.IdpConfig.GoogleBuilder().build(), new AuthUI.IdpConfig.PhoneBuilder().build());

        firebaseAuth = FirebaseAuth.getInstance();
        listener = myFirebaseAuth -> {
            FirebaseUser user = myFirebaseAuth.getCurrentUser();
            if (user !=null) {

                //update token
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LadaSplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                Log.d("TOKEN", instanceIdResult.getToken());
                                UserUtils.updateToken(LadaSplashActivity.this, instanceIdResult.getToken());
                            }
                        });
                checkUserFromFirebase();
            }
            else
                showLoginLayout();
        };
    }

    private void checkUserFromFirebase() {

        driverInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
//                            Toast.makeText(LadaSplashActivity.this, "User Already Registered", Toast.LENGTH_SHORT).show();

                            DriverInfoModel driverInfoModel = snapshot.getValue(DriverInfoModel.class);
                            goToHomeActivity(driverInfoModel);

                        }else{
                            showRegisterLayout();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(LadaSplashActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void goToHomeActivity(DriverInfoModel driverInfoModel) {

        Common.currentUser = driverInfoModel; // init value
        startActivity(new Intent(LadaSplashActivity.this, HomeDriversActivity.class));
        finish();
    }

    private void showRegisterLayout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogTheme);
        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register_driver, null);

        TextInputEditText edt_first_name = (TextInputEditText)itemView.findViewById(R.id.first_name);
        TextInputEditText edt_last_name = (TextInputEditText)itemView.findViewById(R.id.last_name);
        TextInputEditText edt_phone_no = (TextInputEditText)itemView.findViewById(R.id.phone_noD);

        AppCompatButton btn_continue = (AppCompatButton)itemView.findViewById(R.id.btn_register);


        //set data
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() !=null &&
        !TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
            edt_phone_no.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        //set view
        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_first_name.getText().toString()))
                {
                    Toast.makeText(LadaSplashActivity.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(edt_last_name.getText().toString()))
                {
                    Toast.makeText(LadaSplashActivity.this, "Please Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(edt_phone_no.getText().toString()))
                {
                    Toast.makeText(LadaSplashActivity.this, "Please Enter Phone no", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{

                    DriverInfoModel model = new DriverInfoModel();
                    model.setFirstName(edt_first_name.getText().toString());
                    model.setLastName(edt_last_name.getText().toString());
                    model.setPhoneNumber(edt_phone_no.getText().toString());
                    model.setRating(0.0);

                    driverInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(model)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(LadaSplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
//                                    Toast.makeText(HomeDriversActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    goToHomeActivity(model);

                                }
                            });
                }
            }
        });
    }

    private void showLoginLayout() {

        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.layout_sign_in)
                .setPhoneButtonId(R.id.phone_sign_in_btn)
                .setGoogleButtonId(R.id.google_sign_in_btn)
                .build();
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),LOGIN_REQUEST_CODE);

    }

    private void delaySplashScreen() {

//        progressBar.setVisibility(View.VISIBLE);

        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() ->

                    firebaseAuth.addAuthStateListener(listener)

                );


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE){

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }else{
                Toast.makeText(this, "[Failed To Sign in]: "+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}