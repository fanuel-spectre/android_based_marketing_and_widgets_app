package com.example.lada.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lada.FilterProduct;
import com.example.lada.models.Product;
import com.example.lada.R;
import com.example.lada.activities.EditProductActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.HolderProductSeller> implements Filterable {

    public Context context;
    public ArrayList<Product> productList, filterList;
    private FilterProduct filter;


    public AdapterProduct(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.product_recyclerview, parent, false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {

        //get data
        Product product = productList.get(position);
        String id = product.getProductId();
        String uid = product.getUid();
        String productName = product.getProductTitle();
        String discountAvailable = product.getDiscountAvailable();
        String productPic = product.getProductImage();
        String brand = product.getProductBrand();
        String price = product.getOriginalPrice();
        String discountPrice = product.getProductDiscountPrice();


        holder.product_name.setText(productName);
        holder.brand_name.setText(brand);
        holder.price.setText(Html.fromHtml(price+"<sub><small>Br.</small></sub>"));
        holder.discountPrice.setText(Html.fromHtml(discountPrice+"<sub><small>Br.</small></sub>"));

        if (discountAvailable.equals("true")){
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountPrice.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(productPic).placeholder(R.drawable.ic_dashboard).into(holder.product_pic);
        }
        catch (Exception e){
            holder.product_pic.setImageResource(R.drawable.ic_city);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item clicks
                detailsBottom(product);

            }
        });
    }

    private void detailsBottom(Product product) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view for bottom sheet
        View view = LayoutInflater.from(context).inflate(R.layout.product_details_seller, null);
        //set view to bottomSheet
        bottomSheetDialog.setContentView(view);



        //init views of bottomSheet
        ImageButton backBtn = view.findViewById(R.id.back_btn);
        ImageButton deleteBtn = view.findViewById(R.id.delete_btn);
        ImageButton editBtn = view.findViewById(R.id.edit_profile_btn);
        ImageView ProductPic = view.findViewById(R.id.product_pic_details);
        TextView productDescription = view.findViewById(R.id.product_details_description);
        TextView productBrand = view.findViewById(R.id.product_details_brand);
        TextView ProductName = view.findViewById(R.id.product_name);
        TextView productCondition = view.findViewById(R.id.product_details_condition);
        TextView originalPrice = view.findViewById(R.id.original_price);
        TextView DiscountPrice = view.findViewById(R.id.discount_price);

        String id = product.getProductId();
        String uid = product.getUid();
        String productName = product.getProductTitle();
        String productPic = product.getProductImage();
        String brand = product.getProductBrand();
        String discountAvailable = product.getDiscountAvailable();
        String price = product.getOriginalPrice();
        String discountPrice = product.getProductDiscountPrice();

        ProductName.setText(productName);
        productBrand.setText(brand);
        originalPrice.setText(Html.fromHtml(price+"<sub><small>Br</small></sub>"));
        ProductName.setText(productName);
        DiscountPrice.setText(Html.fromHtml(discountPrice+"<sub><small>Br</small></sub>"));

        if (discountAvailable.equals("true")){
            DiscountPrice.setVisibility(View.VISIBLE);
            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            DiscountPrice.setVisibility(View.GONE);
            originalPrice.setPaintFlags(0);
        }
        try{
            Picasso.get().load(productPic).placeholder(R.drawable.ic_dashboard).into(ProductPic);
        }
        catch (Exception e){
            ProductPic.setImageResource(R.drawable.ic_city);
        }

        //show dialog
        bottomSheetDialog.show();


        //edit click

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product"+productName+" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //delete
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //cancel deleting
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
            }
        });
    }

    private void deleteProduct(String id) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        //product deleted
                        Toast.makeText(context, "Product deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{

        private ImageView product_pic, like_button, cart_button;
        private TextView  product_name, brand_name, price, discountPrice;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            product_pic = itemView.findViewById(R.id.product_pic);
            like_button = itemView.findViewById(R.id.like_button);
            cart_button = itemView.findViewById(R.id.cart_button);
            product_name = itemView.findViewById(R.id.product_name);
            brand_name = itemView.findViewById(R.id.shop_name);
            price = itemView.findViewById(R.id.price);
            discountPrice = itemView.findViewById(R.id.discount_price);

            //set data



        }
    }
}
