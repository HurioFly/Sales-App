package com.example.salesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.salesapp.Interface.ItemClickListener;
import com.example.salesapp.R;
import com.example.salesapp.activity.ProductDetailsActivity;
import com.example.salesapp.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class LatestProductAdapter extends RecyclerView.Adapter<LatestProductAdapter.LatestProductViewHolder> {

    Context context;
    List<Product> productList;

    public LatestProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public LatestProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_latest_product, parent, false);
        return new LatestProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestProductViewHolder holder, int position) {
        Product product = productList.get(position);
        Glide.with(context).load(product.getProductImage()).into(holder.imageViewProductImage);
        holder.textViewProductName.setText(product.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.textViewProductPrice.setText(decimalFormat.format(product.getProductPrice()) + "đ");

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick) {
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra("productID", product.getProductID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class LatestProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageViewProductImage;
        TextView textViewProductName, textViewProductPrice;
        private ItemClickListener itemClickListener;

        public LatestProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProductImage = itemView.findViewById(R.id.imageViewProductImage);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
