package com.manager.salesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.manager.salesapp.R;
import com.manager.salesapp.model.ProductType;

import java.util.List;

public class ProductTypeAdapter extends BaseAdapter {

    Context context;
    List<ProductType>  productTypeList;

    public ProductTypeAdapter(Context context, List<ProductType> productTypeList) {
        this.context = context;
        this.productTypeList = productTypeList;
    }

    @Override
    public int getCount() {
        return productTypeList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ProductTypeViewHolder viewHolder = null;
        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_product_type, null);
            viewHolder = new ProductTypeViewHolder();
            viewHolder.imageViewProductTypeImage = view.findViewById(R.id.imageViewProductTypeImage);
            viewHolder.textViewProductTypeName = view.findViewById(R.id.textViewProductTypeName);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ProductTypeViewHolder) view.getTag();
        }
        ProductType productType = productTypeList.get(i);
        Glide.with(context).load(productType.getProductTypeImage()).into(viewHolder.imageViewProductTypeImage);
        viewHolder.textViewProductTypeName.setText(productType.getProductTypeName());
        return view;
    }

    public class ProductTypeViewHolder {
        ImageView imageViewProductTypeImage;
        TextView textViewProductTypeName;
    }
}
