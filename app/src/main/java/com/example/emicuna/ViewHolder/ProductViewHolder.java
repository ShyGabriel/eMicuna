package com.example.emicuna.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emicuna.Interface.ItemClickListener;
import com.example.emicuna.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductRestaurant, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.product_image_item);
        txtProductName = itemView.findViewById(R.id.product_name_item);
        txtProductRestaurant = itemView.findViewById(R.id.product_restaurant_item);
        txtProductPrice = itemView.findViewById(R.id.product_price_item);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
