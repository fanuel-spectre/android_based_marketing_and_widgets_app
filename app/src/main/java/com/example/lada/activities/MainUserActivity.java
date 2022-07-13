package com.example.lada.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.lada.R;
import com.example.lada.adapters.AdapterShop;
import com.example.lada.models.Shop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {
    private TextView nameTittle, tabProduct, tabShop;
    private ImageButton backBtn, logoutBtn, editProfileBtn;
    CircularImageView Profile;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private RelativeLayout ProductsRl, ShopsRl;
    private RecyclerView shopRecyclerview;
    ChipNavigationBar chipNavigationBar;
    private ArrayList<Shop> shopList;
    private AdapterShop adapterShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_user);

        nameTittle = findViewById(R.id.register_title);
        tabProduct = findViewById(R.id.tab_products);
        tabShop = findViewById(R.id.tab_shops);
        ProductsRl = findViewById(R.id.productsRl);
        ShopsRl = findViewById(R.id.shopsRl);
        shopRecyclerview = findViewById(R.id.shop_recyclerview);
        backBtn = findViewById(R.id.back_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        Profile = findViewById(R.id.reg_picture);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();

//        showProductsUI();
        showShopsUI();

        backBtn.setOnClickListener(view -> onBackPressed());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MainUserActivity.this)
                        .setMessage("Are You Sure You Want To Logout")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeMeOffline();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit profile
                startActivity(new Intent(MainUserActivity.this, UserEditProfileActivity.class));
            }
        });

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tabShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showShopsUI();
            }
        });

        tabProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProductsUI();
            }
        });

        chipNavigationBar = findViewById(R.id.bottom_menu);
//        bottomMenu();
    }

    private void showShopsUI() {
        ShopsRl.setVisibility(View.VISIBLE);
        ProductsRl.setVisibility(View.GONE);

        tabProduct.setTextColor(getResources().getColor(R.color.black));
        tabProduct.setBackgroundColor(R.drawable.shape_products);

        tabShop.setTextColor(getResources().getColor(R.color.white));
        tabShop.setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }

    private void showProductsUI() {

        ProductsRl.setVisibility(View.VISIBLE);
        ShopsRl.setVisibility(View.GONE);


        tabProduct.setTextColor(getResources().getColor(R.color.white));
        tabProduct.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabShop.setTextColor(getResources().getColor(R.color.black));
        tabShop.setBackgroundColor(R.drawable.shape_products);
    }

//    private void bottomMenu() {
//
//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int i) {
//                Activity activity = null;
//                switch (i){
//                    case R.id.bottom_nav_home:
//                        startActivity(new Intent(MainUserActivity.this, ForgotPasswordActivity.class));
//                        break;
//
//                    case R.id.bottom_nav_add:
//                        startActivity(new Intent(MainUserActivity.this, UserEditProfileActivity.class));
//                        break;
//
//                    case R.id.bottom_nav_settings:
//                        activity = new SellerEditProfileActivity();
//                        break;
//
//                }
//            }
//        });
//
//    }


    private void makeMeOffline() {

        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else{
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
                            String profilePic = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String city = ""+ds.child("city").getValue();


                            nameTittle.setText(name);

                            try {
                                Picasso.get().load(profilePic).placeholder(R.drawable.ic_person).into(Profile);

                            }
                            catch (Exception e){
                                Profile.setImageResource(R.drawable.ic_person);
                            }

                        //load local shops
                            loadShops(city);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShops(String myCity) {
        //init list
        shopList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //clear list before adding
                        shopList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            Shop shop = ds.getValue(Shop.class);

                            String shopCity = ""+ds.child("city").getValue();

                            //show only city shops
                            if (shopCity.equals(myCity)){
                                shopList.add(shop);
                            }
                            //if you want to display all shops, skip the if statement and add this
                            //shopList.add(shop);
                        }
                        //setup adapter
                        adapterShop = new AdapterShop(MainUserActivity.this, shopList);
                        shopRecyclerview.setAdapter(adapterShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}