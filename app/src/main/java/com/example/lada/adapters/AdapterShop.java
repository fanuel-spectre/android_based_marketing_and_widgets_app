package com.example.lada.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.lada.R;
import com.example.lada.activities.ShopDetailsActivity;
import com.example.lada.models.Shop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop>{

    private Context context;
    public ArrayList<Shop> shopList;

    public AdapterShop(Context context, ArrayList<Shop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout shop recyclerview
        View view = LayoutInflater.from(context).inflate(R.layout.shop_recyclerview, parent, false);

        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        //get data
        Shop shop = shopList.get(position);
        String accountType = shop.getAccountType();
        String address = shop.getCompleteAddress();
        String city = shop.getCity();
        String country = shop.getCountry();
        String deliveryFee = shop.getDeliveryFee();
        String email = shop.getEmail();
        String latitude = shop.getLatitude();
        String longitude = shop.getLongitude();
        String online = shop.getOnline();
        String name = shop.getName();
        final String uid = shop.getUid();
        String phone = shop.getPhone();
        String timestamp = shop.getTimestamp();
        String shopOpen = shop.getShopOpen();
        String state = shop.getState();
        String profileImage = shop.getProfileImage();
        String shopName = shop.getShopName();

        //set data
        holder.ShopName.setText(shopName);
        holder.phoneNo.setText(phone);
        holder.address.setText(address);

        if (online.equals("true")){
            holder.online.setVisibility(View.VISIBLE);
        }
        else {
            holder.online.setVisibility(View.GONE);
        }

        if (shopOpen.equals("true")){
            holder.shopClosed.setVisibility(View.GONE);
        }
        else {
            holder.shopClosed.setVisibility(View.VISIBLE);
        }

        try{
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_dashboard).into(holder.shopPic);
        }
        catch (Exception e){
            holder.shopPic.setImageResource(R.drawable.ic_city);
        }

        //handle click listener, show shop product
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailsActivity.class);
                intent.putExtra("shopUid", uid);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return shopList.size(); //return no of records
    }

    //view holder

    class HolderShop extends RecyclerView.ViewHolder {

        //ui views of shop recyclerview

        private ImageView shopPic, online;
        private TextView ShopName, phoneNo, address, shopClosed;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView) {
            super(itemView);

            shopPic = itemView.findViewById(R.id.shop_pic);
            online = itemView.findViewById(R.id.online_status);
            ShopName = itemView.findViewById(R.id.shop_nameTv);
            phoneNo = itemView.findViewById(R.id.phone_Tv);
            address = itemView.findViewById(R.id.addressTv);
            shopClosed = itemView.findViewById(R.id.shop_closed);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}

