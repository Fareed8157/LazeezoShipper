package com.example.fareed.lazeezoshipper.ViewHolder;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fareed.lazeezoshipper.R;

/**
 * Created by fareed on 19/04/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder {


    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate;
    //private ItemClickListener itemClickListener;

    public Button btnShipping;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderDate=(TextView)itemView.findViewById(R.id.order_dateTime);

        btnShipping=(Button)itemView.findViewById(R.id.btnShipping);
        //itemView.setOnClickListener(this);
        //itemView.setOnCreateContextMenuListener(this);
        // itemView.setOnLongClickListener(this);
    }



//    @Override
//    public void onClick(View view) {
//        itemClickListener.onClick(view,getAdapterPosition(),false);
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//        contextMenu.setHeaderTitle("Choose Option");
//        contextMenu.add(0,0,getAdapterPosition(),"Update");
//        contextMenu.add(0,1,getAdapterPosition(),"Delete");
//
//    }
//
//    @Override
//    public boolean onLongClick(View view) {
//        itemClickListener.onClick(view,getAdapterPosition(),true);
//        return true;
//    }
}

