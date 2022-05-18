package com.manager.salesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.salesapp.Interface.ItemClickListener;
import com.manager.salesapp.R;
import com.manager.salesapp.activity.AccountInformationActivity;
import com.manager.salesapp.activity.LoginActivity;
import com.manager.salesapp.activity.MainActivity;
import com.manager.salesapp.activity.ManagerActivity;
import com.manager.salesapp.activity.OrderHistoryActivity;
import com.manager.salesapp.activity.OrderManagerActivity;
import com.manager.salesapp.activity.ProductActivity;
import com.manager.salesapp.activity.ProductManagerActivity;
import com.manager.salesapp.model.User;
import com.manager.salesapp.utils.Utils;

import java.util.List;
import java.util.zip.Inflater;

import io.paperdb.Paper;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerViewholder> {
    Context context;
    List<String> managerItemList;

    public ManagerAdapter(Context context, List<String> managerItemList) {
        this.context = context;
        this.managerItemList = managerItemList;
    }

    @NonNull
    @Override
    public ManagerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager, parent, false);
        return new ManagerViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerViewholder holder, int position) {
        String managerItem = managerItemList.get(position);
        holder.textViewItemManager.setText(managerItem);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick) {
                    switch (pos) {
                        case 0:
                            Intent productManager = new Intent(context, ProductManagerActivity.class);
                            productManager.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(productManager);
                            break;
                        case 1:
                            Intent orderManager = new Intent(context, OrderManagerActivity.class);
                            orderManager.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(orderManager);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return managerItemList.size();
    }

    public class ManagerViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewItemManager;

        private ItemClickListener itemClickListener;
        public ManagerViewholder(@NonNull View itemView) {
            super(itemView);
            textViewItemManager = itemView.findViewById(R.id.textViewItemManager);

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
