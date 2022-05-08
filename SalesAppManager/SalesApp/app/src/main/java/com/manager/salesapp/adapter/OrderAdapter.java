package com.manager.salesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.salesapp.R;
import com.manager.salesapp.model.Order;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    List<Order> orderList;

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textViewOrderID.setText("Mã đơn hàng: " + order.getOrderID());
        holder.textViewDateCreated.setText("Ngày tạo: " + order.getDateCreated());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.textViewTotalPayment.setText("Tổng tiền: " + decimalFormat.format(order.getTotalMoney()) + "đ");
        holder.textViewPaymentMethods.setText("Phương thức thanh toán: Thanh toán khi nhận hàng");
        holder.textViewConsigneeName.setText("Người nhận: " + order.getConsigneeName());
        holder.textViewConsigneeAddress.setText("Địa chỉ nhận: " + order.getConsigneeAddress());
        holder.textViewConsigneePhoneNumber.setText("Số điện thoại người nhận: " + order.getConsigneePhoneNumber());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                holder.recycleViewOrderDetails.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        linearLayoutManager.setInitialPrefetchItemCount(order.getOrderDetailsList().size());
        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(context, order.getOrderDetailsList());
        holder.recycleViewOrderDetails.setLayoutManager(linearLayoutManager);
        holder.recycleViewOrderDetails.setAdapter(orderDetailsAdapter);
        holder.recycleViewOrderDetails.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView textViewOrderID, textViewDateCreated, textViewTotalPayment, textViewPaymentMethods, textViewConsigneeName, textViewConsigneeAddress, textViewConsigneePhoneNumber;
        RecyclerView recycleViewOrderDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderID = itemView.findViewById(R.id.textViewOrderID);
            textViewDateCreated = itemView.findViewById(R.id.textViewDateCreated);
            textViewTotalPayment = itemView.findViewById(R.id.textViewTotalPayment);
            textViewPaymentMethods = itemView.findViewById(R.id.textViewPaymentMethods);
            textViewConsigneeName = itemView.findViewById(R.id.textViewConsigneeName);
            textViewConsigneeAddress = itemView.findViewById(R.id.textViewConsigneeAddress);
            textViewConsigneePhoneNumber = itemView.findViewById(R.id.textViewConsigneePhoneNumber);
            recycleViewOrderDetails = itemView.findViewById(R.id.recycleViewOrderDetails);
        }
    }
}
