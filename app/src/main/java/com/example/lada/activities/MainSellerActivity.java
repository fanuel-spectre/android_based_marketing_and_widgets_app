package com.example.lada.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.lada.adapters.AdapterProduct;
import com.example.lada.adapters.CategoryAdapter;
import com.example.lada.CategoryDomain;
import com.example.lada.Constants;
import com.example.lada.models.Product;
import com.example.lada.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView nameTittle, shopName, phoneNo, filteredCategory;
    private ImageButton backBtn, logoutBtn, editProfileBtn, addProductBtn, filterProduct;
    CircularImageView Profile;
    private EditText searchProduct;
    private RecyclerView productsRv, recyclerViewCategoryList;
    private RecyclerView.Adapter adapter;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private NestedScrollView nestedScrollView;
    private ArrayList<Product> productList;
    private AdapterProduct adapterProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_seller);

//        nameTittle = findViewById(R.id.seller_name);
//        backBtn = findViewById(R.id.back_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        searchProduct = findViewById(R.id.search_bar_text);
        filterProduct = findViewById(R.id.filter_product);
        nestedScrollView = findViewById(R.id.scroll_product);
        productsRv = findViewById(R.id.products_recycler_view);
        filteredCategory = findViewById(R.id.filtered_products_name);
//        shopName = findViewById(R.id.shop);
//        phoneNo = findViewById(R.id.phone_no);
//        addProductBtn = findViewById(R.id.add_product);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        Profile = findViewById(R.id.reg_picture);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();
        loadAllProducts();
        recyclerViewCategory();

//        backBtn.setOnClickListener(view -> onBackPressed());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMeOffline();
            }
        });




//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                new AlertDialog.Builder(MainSellerActivity.this)
//                        .setMessage("Are You Sure You Want To Logout")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                makeMeOffline();
//                            }
//                        })
//                        .setNegativeButton("No",null)
//                        .show();
//            }
//        });

//        editProfileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //edit profile
//                startActivity(new Intent(MainSellerActivity.this, SellerEditProfileActivity.class));
//            }
//        });

//        addProductBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
//
//            }
//        });


        nestedScrollView.setVerticalScrollBarEnabled(true);
        nestedScrollView.setScrollBarFadeDuration(0);

        filterProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String selected = Constants.productCategories1[which];
                                filteredCategory.setText(selected);
                                if (selected.equals("All")){
                                    loadAllProducts();
                                }
                                else {
                                    loadFilteredProducts(selected);
                                }

                            }
                        })

                .show();
            }
        });

        //search
        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    adapterProduct.getFilter().filter(s);

                }
                catch (Exception e){

                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void recyclerViewCategory() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoryList = findViewById(R.id.recyclerView2);
        recyclerViewCategoryList.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryDomain> categoryList = new ArrayList<>();
        categoryList.add(new CategoryDomain("Pizza", "pizzaa"));
        categoryList.add(new CategoryDomain("Pasta", "pasta"));
        categoryList.add(new CategoryDomain("Burger", "burg"));
        categoryList.add(new CategoryDomain("Soda", "soda"));
        categoryList.add(new CategoryDomain("Sushi", "sushi"));
        categoryList.add(new CategoryDomain("Steak", "steak"));
        categoryList.add(new CategoryDomain("Hot Dog", "hotdog"));
        categoryList.add(new CategoryDomain("Lasagna", "lasagna"));
        categoryList.add(new CategoryDomain("Soup", "soup"));
        categoryList.add(new CategoryDomain("Juice","soda1"));


        adapter = new CategoryAdapter(categoryList);
        recyclerViewCategoryList.setAdapter(adapter);
    }

    private void loadFilteredProducts(String selected) {

        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            String productCategories = ""+ds.child("productCategory").getValue();
                            if (selected.equals(productCategories)){
                                Product product = ds.getValue(Product.class);
                                productList.add(product);
                            }

                        }
                        adapterProduct = new AdapterProduct(MainSellerActivity.this, productList);
                        productsRv.setAdapter(adapterProduct);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadAllProducts() {

        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            Product product = ds.getValue(Product.class);
                            productList.add(product);
                        }
                        adapterProduct = new AdapterProduct(MainSellerActivity.this, productList);
                        productsRv.setAdapter(adapterProduct);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

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
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                            String name = ""+ds.child("name").getValue();
//                            String profilePic = ""+ds.child("profileImage").getValue();
//                            String shop = ""+ds.child("shopName").getValue();
//                            String phone = ""+ds.child("phone").getValue();
//
//                            nameTittle.setText(name);
//                            shopName.setText(shop);
//                            phoneNo.setText(phone);
//                            try {
//                                Picasso.get().load(profilePic).placeholder(R.drawable.ic_person).into(Profile);
//
//                            }
//                            catch (Exception e){
//                                Profile.setImageResource(R.drawable.ic_person);
//                            }
//                            //                          nameTittle.setText(name +" ("+accountType+")");
//                        }
//                    }

//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }
}