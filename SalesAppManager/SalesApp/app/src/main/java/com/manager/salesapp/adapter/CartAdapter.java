package com.manager.salesapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.salesapp.Interface.ImageViewClickListener;
import com.manager.salesapp.R;
import com.manager.salesapp.model.EventBus.CalculateTotalPaymentEvent;
import com.manager.salesapp.model.EventBus.CartEmptyEvent;
import com.manager.salesapp.model.OrderDetails;
import com.manager.salesapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    Context context;
    List<OrderDetails> orderDetailsList;

    public CartAdapter(Context context, List<OrderDetails> orderDetailsList) {
        this.context = context;
        this.orderDetailsList = orderDetailsList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        OrderDetails orderDetails = orderDetailsList.get(position);
        Glide.with(context).load(orderDetails.getProductImage()).into(holder.imageViewProductImage);
        holder.textViewProductName.setText(orderDetails.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.textViewProductPrice.setText("Giá: " + decimalFormat.format(orderDetails.getProductPrice()) + "đ");
        holder.textViewNumberOfProducts.setText(orderDetails.getQuantity() + "");
        holder.textViewTotalMoney.setText("Thành tiền: " + decimalFormat.format(orderDetails.getQuantity()* orderDetails.getProductPrice()) + "đ");
        if(Utils.productsToBuy.contains(orderDetails)) {
            holder.checkBoxCartItem.setChecked(true);
        }
        else {
            holder.checkBoxCartItem.setChecked(false);
        }

        holder.checkBoxCartItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && Utils.productsInCart.contains(orderDetails)) {
                    Utils.productsToBuy.add(orderDetails);
                    EventBus.getDefault().postSticky(new CalculateTotalPaymentEvent());
                }
                else {
                    for(int i=0; i<Utils.productsToBuy.size(); i++) {
                        if(Utils.productsToBuy.get(i).getProductID() == orderDetails.getProductID()) {
                            Utils.productsToBuy.remove(i);
                            EventBus.getDefault().postSticky(new CalculateTotalPaymentEvent());
                            break;
                        }
                    }
                }
            }
        });

        holder.setImageViewClickListener(new ImageViewClickListener() {
            @Override
            public void onClick(View view, int pos, int value) {
                int quantity = orderDetails.getQuantity();

                if(value == 1) {
                    if(quantity > 1) {
                        quantity--;
                        orderDetails.setQuantity(quantity);
                        holder.textViewNumberOfProducts.setText(orderDetails.getQuantity() + "");
                        holder.textViewTotalMoney.setText("Thành tiền: " + decimalFormat.format(orderDetails.getQuantity()* orderDetails.getProductPrice()) + "đ");

                        for(int i=0; i<Utils.productsToBuy.size(); i++) {
                            if(Utils.productsToBuy.get(i).getProductID() == orderDetails.getProductID()) {
                                Utils.productsToBuy.get(i).setQuantity(quantity);
                                EventBus.getDefault().postSticky(new CalculateTotalPaymentEvent());
                                break;
                            }
                        }

                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?");
                        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                for(int k=0; k<Utils.productsToBuy.size(); k++) {
                                    if(Utils.productsToBuy.get(k).getProductID() == orderDetails.getProductID()) {
                                        Utils.productsToBuy.remove(k);
                                        EventBus.getDefault().postSticky(new CalculateTotalPaymentEvent());
                                        break;
                                    }
                                }

                                orderDetailsList.remove(orderDetails);
                                notifyDataSetChanged();

                                if(orderDetailsList.size() == 0) {
                                    EventBus.getDefault().postSticky(new CartEmptyEvent());
                                }
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
                else if(value == 2){
                    if(quantity < orderDetails.getRemainingProducts()) {
                        quantity++;
                        orderDetails.setQuantity(quantity);
                        holder.textViewNumberOfProducts.setText(orderDetails.getQuantity() + "");
                        holder.textViewTotalMoney.setText("Thành tiền: " + decimalFormat.format(orderDetails.getQuantity()* orderDetails.getProductPrice()) + "đ");

                        for(int i=0; i<Utils.productsToBuy.size(); i++) {
                            if(Utils.productsToBuy.get(i).getProductID() == orderDetails.getProductID()) {
                                Utils.productsToBuy.get(i).setQuantity(quantity);
                                EventBus.getDefault().postSticky(new CalculateTotalPaymentEvent());
                                break;
                            }
                        }
                    }
                    else {
                        Toast.makeText(context, "Kho chỉ còn " + quantity + " sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox checkBoxCartItem;
        ImageView imageViewProductImage, imageViewReduceNumberOfProducts, imageViewIncreaseNumberOfProducts;
        TextView textViewProductName, textViewProductPrice, textViewNumberOfProducts, textViewTotalMoney;

        ImageViewClickListener imageViewClickListener;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxCartItem = itemView.findViewById(R.id.checkBoxCartItem);
            imageViewProductImage = itemView.findViewById(R.id.imageViewProductImage);
            imageViewReduceNumberOfProducts = itemView.findViewById(R.id.imageViewReduceNumberOfProducts);
            imageViewIncreaseNumberOfProducts = itemView.findViewById(R.id.imageViewIncreaseNumberOfProducts);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewNumberOfProducts = itemView.findViewById(R.id.textViewNumberOfProducts);
            textViewTotalMoney = itemView.findViewById(R.id.textViewTotalMoney);

            imageViewReduceNumberOfProducts.setOnClickListener(this);
            imageViewIncreaseNumberOfProducts.setOnClickListener(this);
        }

        public void setImageViewClickListener(ImageViewClickListener imageViewClickListener) {
            this.imageViewClickListener = imageViewClickListener;
        }

        @Override
        public void onClick(View view) {
            if(view == imageViewReduceNumberOfProducts) {
                imageViewClickListener.onClick(view, getAdapterPosition(), 1);
            }
            else {
                imageViewClickListener.onClick(view, getAdapterPosition(), 2);
            }
        }
    }
}
