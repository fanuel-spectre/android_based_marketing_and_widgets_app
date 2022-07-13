package com.example.lada.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lada.FilterProductUser;
import com.example.lada.R;
import com.example.lada.activities.DisplayProductActivity;
import com.example.lada.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<Product> productsList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<Product> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.user_product_recyclerview, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

        //get data
        Product product = productsList.get(position);
        String id = product.getProductId();
        String uid = product.getUid();
        String productName = product.getProductTitle();
        String brand = product.getProductBrand();
        String discountAvailable = product.getDiscountAvailable();
        String price = product.getOriginalPrice();
        String productImage = product.getProductImage();
        String discountPrice = product.getProductDiscountPrice();

        //set data
        holder.ProductName.setText(productName);
        holder.brandName.setText(brand);
        holder.price.setText(Html.fromHtml(price+"<sub><small>Br</small></sub>"));
        holder.discountPrice.setText(Html.fromHtml(discountPrice+"<sub><small>Br</small></sub>"));

        if (discountAvailable.equals("true")){
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountPrice.setVisibility(View.GONE);
            holder.price.setPaintFlags(0);
        }
        try{
            Picasso.get().load(productImage).placeholder(R.drawable.ic_dashboard).into(holder.productPicture);
        }
        catch (Exception e){
            holder.productPicture.setImageResource(R.drawable.ic_city);
        }

        holder.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //cart button
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show product details
                Intent intent = new Intent(context, DisplayProductActivity.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProductUser(this, filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productPicture, cartBtn, likeBtn;
        private TextView brandName, price, discountPrice, ProductName;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productPicture = itemView.findViewById(R.id.product_pic_user);
            ProductName = itemView.findViewById(R.id.product_name_user);
            brandName = itemView.findViewById(R.id.shop_name_user);
            price = itemView.findViewById(R.id.price_user);
            discountPrice = itemView.findViewById(R.id.discount_price_user);
            cartBtn = itemView.findViewById(R.id.cart_button);
            likeBtn = itemView.findViewById(R.id.like_button);
        }
    }
}
