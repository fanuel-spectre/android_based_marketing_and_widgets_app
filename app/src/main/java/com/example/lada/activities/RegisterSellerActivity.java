package com.example.lada.activities;

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
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RegisterSellerActivity extends AppCompatActivity implements LocationListener {

    ImageButton backBtn, myLocationBtn;
    CircularImageView profileImage;
    TextInputLayout fullName, phoneNo, email, shopName, deliveryFee, password, confirmPassword, countryEt, stateEt, cityEt, completeAddress;
    AppCompatButton registerBtn;

    private static final int LOCATION_REQUEST_CODE = 100;

    private String[] locationPermissions;

    LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private double latitude, longitude;
    private Uri image_uri;
    private String myUri = "";
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_seller);

        backBtn = findViewById(R.id.back_btn);
        myLocationBtn = findViewById(R.id.my_location_btn);
        profileImage = findViewById(R.id.reg_picture);
        fullName = findViewById(R.id.full_name);
        phoneNo = findViewById(R.id.phoneEt);
        shopName = findViewById(R.id.shop_name);
        deliveryFee = findViewById(R.id.delivery);
        email = findViewById(R.id.emailEt);
        countryEt = findViewById(R.id.countryEt);
        stateEt = findViewById(R.id.stateEt);
        cityEt = findViewById(R.id.cityEt);
        completeAddress = findViewById(R.id.complete_address);
        password = findViewById(R.id.passwordEt);
        confirmPassword = findViewById(R.id.confirm_passwordEt);
        registerBtn = findViewById(R.id.register_btn);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait");
        progressDialog.setCanceledOnTouchOutside(false);

        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    //already allowed
                    detectLocation();
                } else {
                    //not allowed
                    requestLocationPermission();
                }

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(RegisterSellerActivity.this);

            }
        });

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

//    private final ActivityResultLauncher<Intent> galleryActivity = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
////                @Override
////                public void onActivityResult(ActivityResult result) {
////                    if(result.getResultCode() == Activity.RESULT_OK){
////                        Intent data = result.getData();
////                        assert data != null;
////                        Uri imageUri = data.getData();
////                        profileImage.setImageURI(imageUri);
////                    }
////                    else{
////                        Toast.makeText(RegisterSellerActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
////                    }
////                }
//
//                @Override
//                public void onActivityResult(ActivityResult result) {
//
//                    Intent intent = result.getData();
//                    if (intent !=null){
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),intent.getData());
//
//                            profileImage.setImageBitmap(bitmap);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//    );


    private String FullName, ShopName, PhoneNumber, DeliveryFee, CountryEt, StateEt, CityEt, CompleteAddress, Email, PasswordEt, ConfirmPasswordEt;
    private void inputData() {
        FullName = fullName.getEditText().getText().toString().trim();
        ShopName = shopName.getEditText().getText().toString().trim();
        DeliveryFee = deliveryFee.getEditText().getText().toString().trim();
        PhoneNumber = phoneNo.getEditText().getText().toString().trim();
        CountryEt = countryEt.getEditText().getText().toString().trim();
        CityEt = cityEt.getEditText().getText().toString().trim();
        StateEt = stateEt.getEditText().getText().toString().trim();
        CompleteAddress = completeAddress.getEditText().getText().toString().trim();
        Email = email.getEditText().getText().toString().trim();
        PasswordEt = password.getEditText().getText().toString().trim();
        ConfirmPasswordEt = confirmPassword.getEditText().getText().toString().trim();


        if (TextUtils.isEmpty(FullName)){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ShopName)){
            Toast.makeText(this, "Enter Shop Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(PhoneNumber)){
            Toast.makeText(this, "Enter Phone No", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(DeliveryFee)){
            Toast.makeText(this, "Enter Delivery Fee", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(CountryEt)){
            Toast.makeText(this, "Enter Country", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(StateEt)){
            Toast.makeText(this, "Enter State", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(CityEt)){
            Toast.makeText(this, "Enter City", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(CompleteAddress)){
            Toast.makeText(this, "Enter Complete Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Email)){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(PasswordEt)){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ConfirmPasswordEt)){
            Toast.makeText(this, "Enter 2nd Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude == 0.0 || longitude == 0.0){
            Toast.makeText(this, "please Click GPS Button to detect your location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (PasswordEt.length()<6){
            Toast.makeText(this, "Password Too Short, must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PasswordEt.equals(ConfirmPasswordEt)){
            Toast.makeText(this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();

    }

    private void createAccount() {

        progressDialog.setMessage("Creating Account");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(Email, PasswordEt)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void saveFirebaseData() {

        progressDialog.setMessage("Saving Account Info");

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
            hashMap.put("online","true");
            hashMap.put("shopOpen","true");
            hashMap.put("profileImage","");


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this, RegisterSellerActivity.class));
                            finish();

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
                                hashMap.put("shopName",""+ShopName);
                                hashMap.put("deliveryFee",""+DeliveryFee);
                                hashMap.put("phone",""+PhoneNumber);
                                hashMap.put("country",""+CountryEt);
                                hashMap.put("state",""+StateEt);
                                hashMap.put("city",""+CityEt);
                                hashMap.put("completeAddress",""+CompleteAddress);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("accountType","Seller");
                                hashMap.put("online","true");
                                hashMap.put("shopOpen","true");
                                hashMap.put("profileImage",myUri);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this, LoginActivity.class));
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this, RegisterSellerActivity.class));
                                                finish();

                                            }
                                        });

                            }
                            else {
                                Toast.makeText(RegisterSellerActivity.this, "Please Select image", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(RegisterSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



        }
    }


    private void detectLocation() {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();

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

    private boolean checkLocationPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
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
            Objects.requireNonNull(countryEt.getEditText()).setText(country);
            Objects.requireNonNull(stateEt.getEditText()).setText(state);
            Objects.requireNonNull(cityEt.getEditText()).setText(city);
            Objects.requireNonNull(completeAddress.getEditText()).setText(address);
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean locationAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        //permission granted
                        detectLocation();
                    }
                    else {
                        //permission denied
                        Toast.makeText(this, "Location is needed for this service", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

//            case CAMERA_REQUEST_CODE:{
//                if (grantResults.length>0){
//                    boolean cameraAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
//                    boolean storageAccepted = grantResults[1] ==PackageManager.PERMISSION_GRANTED;
//                    if (cameraAccepted && storageAccepted){
//                        //permission granted
//                        pickFromCamera();
//                    }
//                    else {
//                        //permission denied
//                        Toast.makeText(this, "Camera permission is needed for this service", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            break;
//
////            case STORAGE_REQUEST_CODE:{
////                if (grantResults.length>0){
////                    boolean storageAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
////                    if (storageAccepted){
////                        //permission granted
////                        pickFromGallery();
////                    }
////                    else {
////                        //permission denied
////                        Toast.makeText(this, "Storage permission is needed for this service", Toast.LENGTH_SHORT).show();
////                    }
////                }
////            }
////            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}