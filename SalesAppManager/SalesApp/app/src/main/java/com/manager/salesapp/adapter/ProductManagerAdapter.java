package com.manager.salesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.salesapp.Interface.ButtonClickListener;
import com.manager.salesapp.Interface.ItemClickListener;
import com.manager.salesapp.R;
import com.manager.salesapp.activity.ProductEditActivity;
import com.manager.salesapp.model.EventBus.ProductManagerEvent;
import com.manager.salesapp.model.Product;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class ProductManagerAdapter extends RecyclerView.Adapter<ProductManagerAdapter.ProductManagerViewHolder> {
    Context context;
    List<Product> productList;

    public ProductManagerAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_manager, parent, false);
        return new ProductManagerAdapter.ProductManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductManagerViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewProductID.setText("ID: " + product.getProductID());
        Glide.with(context).load(product.getProductImage()).into(holder.imageViewProductImage);
        holder.textViewProductName.setText(product.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.textViewProductPrice.setText("Giá: " + decimalFormat.format(product.getProductPrice()) + "đ");
        holder.textViewRemainingProducts.setText("Kho còn: " + product.getRemainingProducts());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick) {
                    if(holder.linearLayoutProductManager.getVisibility() == View.GONE) {
                        holder.linearLayoutProductManager.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.linearLayoutProductManager.setVisibility(View.GONE);
                    }
                }
            }
        });

        holder.setButtonClickListener(new ButtonClickListener() {
            @Override
            public void onClick(View view, int pos, int value) {
                if(value == 1) {
                    Intent intent = new Intent(context, ProductEditActivity.class);
                    intent.putExtra("product", product);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else if(value == 2){
                    EventBus.getDefault().postSticky(new ProductManagerEvent(product));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductManagerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewProductImage;
        TextView textViewProductID, textViewProductName, textViewProductPrice, textViewRemainingProducts;

        LinearLayout linearLayoutProductManager;
        Button buttonProductEdit, buttonProductDelete;

        private ItemClickListener itemClickListener;
        private ButtonClickListener buttonClickListener;

        public ProductManagerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewProductID = itemView.findViewById(R.id.textViewProductID);
            imageViewProductImage = itemView.findViewById(R.id.imageViewProductImage);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewRemainingProducts = itemView.findViewById(R.id.textViewRemainingProducts);

            linearLayoutProductManager = itemView.findViewById(R.id.linearLayoutProductManager);
            buttonProductEdit = itemView.findViewById(R.id.buttonProductEdit);
            buttonProductDelete = itemView.findViewById(R.id.buttonProductDelete);

            itemView.setOnClickListener(this);

            buttonProductEdit.setOnClickListener(this);
            buttonProductDelete.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setButtonClickListener(ButtonClickListener buttonClickListener) {
            this.buttonClickListener = buttonClickListener;
        }

        @Override
        public void onClick(View view) {
            if(view == buttonProductEdit) {
                buttonClickListener.onClick(view, getAdapterPosition(), 1);
            }
            else if(view == buttonProductDelete) {
                buttonClickListener.onClick(view, getAdapterPosition(), 2);
            }
            else {
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }
        }
    }
}
