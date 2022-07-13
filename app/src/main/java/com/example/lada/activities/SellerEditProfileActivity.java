package com.example.lada.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.lada.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SellerEditProfileActivity extends AppCompatActivity implements LocationListener {

    ImageButton backBtn, gpsBtn;
    CircularImageView profileImage;
    TextInputLayout newFullName, newShopName, newEmail, newPhone, newDelivery, newCountry, newCity, newState, newCompleteAddress, oldPassword, newPassword, newConfirmPassword;
    AppCompatButton updateBtn;
    SwitchCompat shopOpenSwitch;

    private static final int LOCATION_REQUEST_CODE = 100;

    private String[] locationPermissions;

    LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri image_uri;
    private String myUri = "";
    private StorageTask uploadTask;

    private double latitude=0.0;
    private double longitude=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_seller_edit_profile);

        backBtn = findViewById(R.id.back_btn);
        gpsBtn = findViewById(R.id.gps_btn);
        profileImage = findViewById(R.id.reg_picture);
        newFullName = findViewById(R.id.full_name);
        newShopName = findViewById(R.id.shop_name);
        newEmail = findViewById(R.id.emailEt);
        newPhone = findViewById(R.id.phone);
        newDelivery = findViewById(R.id.delivery);
        newCountry = findViewById(R.id.countryEt);
        newCity = findViewById(R.id.cityEt);
        newState = findViewById(R.id.stateEt);
        newCompleteAddress = findViewById(R.id.complete_address);
        updateBtn = findViewById(R.id.update_btn);
        shopOpenSwitch = findViewById(R.id.ShopOpenSwitch);

        String[] locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocationPermission()) {
                    //already allowed
                    detectLocation();
                } else {
                    //not allowed
                    requestLocationPermission();
                }

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(SellerEditProfileActivity.this);

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();

            }
        });
    }

    private String FullName, ShopName, PhoneNumber, DeliveryFee, CountryEt, StateEt, CityEt, CompleteAddress, Email, PasswordEt, ConfirmPasswordEt;
   private boolean shopOpen;
    private void inputData() {
        FullName = newFullName.getEditText().getText().toString().trim();
        ShopName = newShopName.getEditText().getText().toString().trim();
        PhoneNumber = newPhone.getEditText().getText().toString().trim();
        DeliveryFee = newDelivery.getEditText().getText().toString().trim();
        CountryEt = newCountry.getEditText().getText().toString().trim();
        CityEt = newCity.getEditText().getText().toString().trim();
        StateEt = newState.getEditText().getText().toString().trim();
        CompleteAddress = newCompleteAddress.getEditText().getText().toString().trim();
        Email = newEmail.getEditText().getText().toString().trim();
        shopOpen = shopOpenSwitch.isChecked();


//        PasswordEt = password.getEditText().getText().toString().trim();
//        ConfirmPasswordEt = confirmPassword.getEditText().getText().toString().trim();



        updateProfile();

    }

    private void updateProfile() {
        progressDialog.setMessage("Updating Profile");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String timestamp = ""+System.currentTimeMillis();

        if (image_uri==null){

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+Email);
            hashMap.put("name",""+FullName);
            hashMap.put("shopName",""+ShopName);
            hashMap.put("phone",""+PhoneNumber);
            hashMap.put("deliveryFee",""+DeliveryFee);
            hashMap.put("country",""+CountryEt);
            hashMap.put("state",""+StateEt);
            hashMap.put("city",""+CityEt);
            hashMap.put("completeAddress",""+CompleteAddress);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("accountType","Seller");
            hashMap.put("shopOpen",""+shopOpen);
            hashMap.put("profileImage","");


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(SellerEditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SellerEditProfileActivity.this, "Something is Wrong Please Try Again ", Toast.LENGTH_SHORT).show();


                        }
                    });

        }
        else {

            String filePathName = "profile_images/" + ""+firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            uploadTask = storageReference.putFile(image_uri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful())
                            {

                                Uri downloadUri = task.getResult();
                                myUri = downloadUri.toString();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+Email);
                                hashMap.put("name",""+FullName);
                                hashMap.put("phone",""+PhoneNumber);
                                hashMap.put("country",""+CountryEt);
                                hashMap.put("state",""+StateEt);
                                hashMap.put("shopName",""+ShopName);
                                hashMap.put("deliveryFee",""+DeliveryFee);
                                hashMap.put("city",""+CityEt);
                                hashMap.put("completeAddress",""+CompleteAddress);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("accountType","Seller");
                                hashMap.put("shopOpen",""+shopOpen);
                                hashMap.put("profileImage",myUri);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SellerEditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SellerEditProfileActivity.this, "Something is Wrong Please Try Again ", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }
                            else {
                                Toast.makeText(SellerEditProfileActivity.this, "Please Select image", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(SellerEditProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



        }

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String shopName = ""+ds.child("shopName").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String deliveryFee = ""+ds.child("deliveryFee").getValue();
                            String country = ""+ds.child("country").getValue();
                            String city = ""+ds.child("city").getValue();
                             latitude = Double.parseDouble(""+ds.child("latitude").getValue());
                             longitude = Double.parseDouble(""+ds.child("longitude").getValue());
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String online = ""+ds.child("online").getValue();
                            String state = ""+ds.child("state").getValue();
                            String completeAddress = ""+ds.child("completeAddress").getValue();
                            String profilePic = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String shopOpen = ""+ds.child("shopOpen").getValue();
                            String uid = ""+ds.child("uid").getValue();

                            newFullName.getEditText().setText(name);
                            newEmail.getEditText().setText(email);
                            newShopName.getEditText().setText(shopName);
                            newPhone.getEditText().setText(phone);
                            newDelivery.getEditText().setText(deliveryFee);
                            newCountry.getEditText().setText(country);
                            newCity.getEditText().setText(city);
                            newState.getEditText().setText(state);
                            newCompleteAddress.getEditText().setText(completeAddress);
                            if (shopOpen.equals("true")){
                                shopOpenSwitch.setChecked(true);
                            }
                            else{
                                shopOpenSwitch.setChecked(false);
                            }
                            try {
                                Picasso.get().load(profilePic).placeholder(R.drawable.ic_person).into(profileImage);

                            }
                            catch (Exception e){
                                profileImage.setImageResource(R.drawable.ic_person);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);

    }

    private void detectLocation() {
        Toast.makeText(this, "Please Wait", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();

    }

    private void findAddress() {
        //find address, country, state, city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); //complete address
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            //set addresses
            Objects.requireNonNull(newCountry.getEditText()).setText(country);
            Objects.requireNonNull(newState.getEditText()).setText(state);
            Objects.requireNonNull(newCity.getEditText()).setText(city);
            Objects.requireNonNull(newCompleteAddress.getEditText()).setText(address);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "please enable location", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //permission granted
                        detectLocation();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Location is needed for this service", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data !=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            image_uri = result.getUri();
            profileImage.setImageURI(image_uri);
        }
        else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }
}