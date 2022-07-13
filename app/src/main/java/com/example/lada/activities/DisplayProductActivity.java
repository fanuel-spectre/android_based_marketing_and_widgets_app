package com.example.lada.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lada.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayProductActivity extends AppCompatActivity {

     ImageView like, star1, star2, star3, star4, star5, prodImageDt;
     TextView prodOriginalPrice, prodDiscountPrice, prodDescriptionDt, prodNameDt, prodNameDt2, prodBrandDt, prodConditionDt, prodCategoryDt, prodColorDt, prodTypeDt, ProdNegotiationAvailable, prodDiscountAvailable, prodOriginalPrice2, prodDiscountPrice2;
    Boolean isClicked;
     String productId;
    private FirebaseAuth firebaseAuth;

//    String productNameDt, productOriginalPriceDt, productDiscountPriceDt, productDescriptionDt,
//    productBrandDt, productConditionDt, productColorDt, productCategoryDt, productColourDt, productTypeDt,
//    productNegotiationAvailable, productDiscountAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide status bar
        setContentView(R.layout.activity_display_product);



        isClicked = true;
        like = findViewById(R.id.like_button);
        star1 = findViewById(R.id.star_1);
        star2 = findViewById(R.id.star_2);
        star3 = findViewById(R.id.star_3);
        star4 = findViewById(R.id.star_4);
        star5 = findViewById(R.id.star_5);

        prodImageDt = findViewById(R.id.product_imageDt);
        prodNameDt = findViewById(R.id.product_name_details);
        prodNameDt2 = findViewById(R.id.product_nameDt);
        prodOriginalPrice = findViewById(R.id.original_price_details);
        prodDiscountPrice = findViewById(R.id.discounted_price_details);
        prodDescriptionDt = findViewById(R.id.product_descriptionDt);
        prodBrandDt = findViewById(R.id.product_brandDt);
        prodConditionDt = findViewById(R.id.product_conditionDt);
        prodCategoryDt = findViewById(R.id.product_categoryDt);
        prodColorDt = findViewById(R.id.product_colorDt);
        prodTypeDt = findViewById(R.id.product_typeDt);
        ProdNegotiationAvailable = findViewById(R.id.negotiation_availableDt);
        prodDiscountAvailable = findViewById(R.id.discount_availableDt);
        prodOriginalPrice2 = findViewById(R.id.original_priceDt);
        prodDiscountPrice2 = findViewById(R.id.discount_priceDt);


        productId = getIntent().getStringExtra("productId");
        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDetails();


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked){
                    like.setImageDrawable(getDrawable(R.drawable.orangeheart));
                    isClicked = false;
                }else {

                    like.setImageDrawable(getDrawable(R.drawable.heart));
                    isClicked = true;
                }
            }
        });

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked){
                    star1.setImageDrawable(getDrawable(R.drawable.starfill));
                    isClicked = false;
                }else{
                    star1.setImageDrawable(getDrawable(R.drawable.star));
                    isClicked = true;
                    star2.setImageDrawable(getDrawable(R.drawable.star));
                    star3.setImageDrawable(getDrawable(R.drawable.star));
                    star4.setImageDrawable(getDrawable(R.drawable.star));
                    star5.setImageDrawable(getDrawable(R.drawable.star));
                }
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked){
                    star2.setImageDrawable(getDrawable(R.drawable.starfill));
                    star1.setImageDrawable(getDrawable(R.drawable.starfill));
                    isClicked = false;
                }else{
                    isClicked = true;
                    star3.setImageDrawable(getDrawable(R.drawable.star));
                    star4.setImageDrawable(getDrawable(R.drawable.star));
                    star5.setImageDrawable(getDrawable(R.drawable.star));
                }
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked){
                    star3.setImageDrawable(getDrawable(R.drawable.starfill));
                    star2.setImageDrawable(getDrawable(R.drawable.starfill));
                    star1.setImageDrawable(getDrawable(R.drawable.starfill));
                    isClicked = false;
                }else{
                    isClicked = true;
                    star4.setImageDrawable(getDrawable(R.drawable.star));
                    star5.setImageDrawable(getDrawable(R.drawable.star));
                }
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked){
                    star4.setImageDrawable(getDrawable(R.drawable.starfill));
                    star3.setImageDrawable(getDrawable(R.drawable.starfill));
                    star2.setImageDrawable(getDrawable(R.drawable.starfill));
                    star1.setImageDrawable(getDrawable(R.drawable.starfill));
                    isClicked = false;
                }else{
                    isClicked = true;
                    star5.setImageDrawable(getDrawable(R.drawable.star));
                }
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked){
                    star5.setImageDrawable(getDrawable(R.drawable.starfill));
                    star4.setImageDrawable(getDrawable(R.drawable.starfill));
                    star3.setImageDrawable(getDrawable(R.drawable.starfill));
                    star2.setImageDrawable(getDrawable(R.drawable.starfill));
                    star1.setImageDrawable(getDrawable(R.drawable.starfill));
                    isClicked = false;
                }else{
                    star5.setImageDrawable(getDrawable(R.drawable.star));
                    isClicked = true;
                }
            }
        });

    }

    private void loadProductDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String productId = ""+ dataSnapshot.child("productId").getValue();
                String productNameDt = ""+dataSnapshot.child("productTitle").getValue();
                String productOriginalPriceDt = ""+dataSnapshot.child("originalPrice").getValue();
                String  productDiscountPriceDt = ""+dataSnapshot.child("productDiscountPrice").getValue();
                String productDescriptionDt = ""+dataSnapshot.child("productDescription").getValue();
                String  productBrandDt = ""+dataSnapshot.child("productBrand").getValue();
                String  productConditionDt = ""+dataSnapshot.child("productCondition").getValue();
                String productColorDt = ""+dataSnapshot.child("productColor").getValue();
                String productCategoryDt = ""+dataSnapshot.child("productCategory").getValue();
                String productTypeDt = ""+dataSnapshot.child("productType").getValue();
                String NegotiationAvailable = ""+dataSnapshot.child("negotiationAvailable").getValue();
                String DiscountAvailable = ""+dataSnapshot.child("discountAvailable").getValue();
                String productImage = ""+dataSnapshot.child("productImage").getValue();
                String uid = ""+ dataSnapshot.child("uid").getValue();

                //set data

                prodNameDt.setText(productNameDt);
                prodOriginalPrice.setText(productOriginalPriceDt);
                prodDiscountPrice.setText(productDiscountPriceDt);
                prodDescriptionDt.setText(productDescriptionDt);
                prodBrandDt.setText(productBrandDt);
                prodConditionDt.setText(productConditionDt);
                prodCategoryDt.setText(productCategoryDt);
                prodColorDt.setText(productColorDt);
                prodTypeDt.setText(productTypeDt);
                prodOriginalPrice2.setText(productOriginalPriceDt);
                prodDiscountPrice2.setText(productDiscountPriceDt);

                if (NegotiationAvailable.equals("true")){
                    ProdNegotiationAvailable.setText("Yes");
                }
                else{
                    ProdNegotiationAvailable.setText("No");
                }

                if (DiscountAvailable.equals("true")){
                    prodDiscountAvailable.setText("Yes");
                }
                else{
                    prodDiscountAvailable.setText("NO");
                }

                try {
                    Picasso.get().load(productImage).placeholder(R.drawable.ic_dashboard).into(prodImageDt);

                }
                catch (Exception e){
                    prodImageDt.setImageResource(R.drawable.ic_dashboard);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}