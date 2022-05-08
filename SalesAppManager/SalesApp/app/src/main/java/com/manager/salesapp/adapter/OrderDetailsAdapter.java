package com.manager.salesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.salesapp.R;
import com.manager.salesapp.model.OrderDetails;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {
    Context context;
    List<OrderDetails> orderDetailsList;

    public OrderDetailsAdapter(Context context, List<OrderDetails> orderDetailsList) {
        this.context = context;
        this.orderDetailsList = orderDetailsList;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_details, parent, false);
        return new OrderDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position) {
        OrderDetails orderDetails = orderDetailsList.get(position);
        Glide.with(context).load(orderDetails.getProductImage()).into(holder.imageViewProductImage);
        holder.textViewProductName.setText(orderDetails.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.textViewProductPrice.setText("Giá: " + decimalFormat.format(orderDetails.getProductPrice()) + "đ");
        holder.textViewNumberOfProducts.setText("Số lượng: " + orderDetails.getQuantity());
    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }

    public class OrderDetailsViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewProductImage;
        TextView textViewProductName, textViewProductPrice, textViewNumberOfProducts;

        public OrderDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProductImage = itemView.findViewById(R.id.imageViewProductImage);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewNumberOfProducts = itemView.findViewById(R.id.textViewNumberOfProducts);
        }
    }
}
