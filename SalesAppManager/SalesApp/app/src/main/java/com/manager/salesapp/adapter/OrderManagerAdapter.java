package com.manager.salesapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.salesapp.R;
import com.manager.salesapp.model.EventBus.ProductManagerEvent;
import com.manager.salesapp.model.EventBus.SetOrderStatusEvent;
import com.manager.salesapp.model.Order;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrderManagerAdapter extends RecyclerView.Adapter<OrderManagerAdapter.OrderManagerViewHolder> {
    Context context;
    List<Order> orderList;
    List<String> orderStatus;

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public OrderManagerAdapter(Context context, List<Order> orderList, List<String> orderStatus) {
        this.context = context;
        this.orderList = orderList;
        this.orderStatus = orderStatus;
    }

    @NonNull
    @Override
    public OrderManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_manager, parent, false);
        return new OrderManagerAdapter.OrderManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderManagerViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textViewOrderID.setText("Mã đơn hàng: " + order.getOrderID());
        holder.textViewDateCreated.setText("Ngày tạo: " + order.getDateCreated());
        holder.textViewUserPhoneNumber.setText("Tài khoản tạo: " + order.getPhoneNumber());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.textViewTotalPayment.setText("Tổng tiền: " + decimalFormat.format(order.getTotalMoney()) + "đ");
        holder.textViewPaymentMethods.setText("Phương thức thanh toán: Thanh toán khi nhận hàng");
        holder.textViewConsigneeName.setText("Người nhận: " + order.getConsigneeName());
        holder.textViewConsigneeAddress.setText("Địa chỉ nhận: " + order.getConsigneeAddress());
        holder.textViewConsigneePhoneNumber.setText("Số điện thoại người nhận: " + order.getConsigneePhoneNumber());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, orderStatus);
        holder.spinnerOrderStatus.setAdapter(spinnerAdapter);
        holder.spinnerOrderStatus.post(new Runnable() {
            @Override
            public void run() {
                holder.spinnerOrderStatus.setSelection(orderStatus.indexOf(order.getOrderStatus()));
            }
        });

        holder.spinnerOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order.setOrderStatus(orderStatus.get(position));
                EventBus.getDefault().postSticky(new SetOrderStatusEvent(order));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    public class OrderManagerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderID, textViewDateCreated, textViewUserPhoneNumber, textViewTotalPayment, textViewPaymentMethods, textViewConsigneeName, textViewConsigneeAddress, textViewConsigneePhoneNumber;
        Spinner spinnerOrderStatus;
        RecyclerView recycleViewOrderDetails;

        public OrderManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderID = itemView.findViewById(R.id.textViewOrderID);
            textViewDateCreated = itemView.findViewById(R.id.textViewDateCreated);
            textViewUserPhoneNumber = itemView.findViewById(R.id.textViewUserPhoneNumber);
            textViewTotalPayment = itemView.findViewById(R.id.textViewTotalPayment);
            textViewPaymentMethods = itemView.findViewById(R.id.textViewPaymentMethods);
            textViewConsigneeName = itemView.findViewById(R.id.textViewConsigneeName);
            textViewConsigneeAddress = itemView.findViewById(R.id.textViewConsigneeAddress);
            textViewConsigneePhoneNumber = itemView.findViewById(R.id.textViewConsigneePhoneNumber);
            spinnerOrderStatus = itemView.findViewById(R.id.spinnerOrderStatus);
            recycleViewOrderDetails = itemView.findViewById(R.id.recycleViewOrderDetails);
        }
    }
}
