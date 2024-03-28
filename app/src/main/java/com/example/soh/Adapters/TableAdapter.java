package com.example.soh.Adapters;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.Models.TableNumber;
import com.example.soh.MyInterface.OnTableItemClickListener;
import com.example.soh.R;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private List<TableNumber> mListTableNumber;
    private final OnTableItemClickListener onTableItemClickListener;


    // Constructor mặc định
    public TableAdapter(List<TableNumber> mListTableNumber) {
        this.mListTableNumber = mListTableNumber;
        this.onTableItemClickListener = null; // Khởi tạo listener là null
    }

    public TableAdapter(List<TableNumber> mListTableNumber, OnTableItemClickListener listener) {
        this.mListTableNumber = mListTableNumber;
        this.onTableItemClickListener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {

        TableNumber tableNumber = mListTableNumber.get(position);

        if (mListTableNumber == null){
            return;
        }

        holder.tvTableNum.setText(String.format("%d", tableNumber.getTableNum()));

        int tableStatus = tableNumber.getTableStatus();
        switch (tableStatus) {
            case 0:
                holder.tvStatus.setText("Đang trống");
                holder.imgTable.setImageResource(R.drawable.ic_table);
                break;
            case 1:
                holder.tvStatus.setText("Đang phục vụ");
                holder.imgTable.setImageResource(R.drawable.ic_table2);
                break;
            case 2:
                holder.tvStatus.setText("Cần lên món");
                holder.imgTable.setImageResource(R.drawable.ic_table3);
                break;
            default:
                holder.tvStatus.setText("Lỗi hiển thị");
                holder.imgTable.setImageResource(R.drawable.ic_table4);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TableAdapter", "Click on table: " + tableNumber.getTableNum());
                if (onTableItemClickListener != null) {
                    onTableItemClickListener.onTableItemClick(tableNumber);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mListTableNumber != null){
            return mListTableNumber.size();
        }
        return 0;
    }

    public  class TableViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgTable;
        private TextView tvStatus;
        private TextView tvTableNum;


        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            imgTable = itemView.findViewById(R.id.imgProduct);
            tvTableNum = itemView.findViewById(R.id.tvProductName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
//    public void updateData(List<TableNumber> newData) {
//        if (newData != null) {
//            mListTableNumber.clear();
//            mListTableNumber.addAll(newData);
//            notifyDataSetChanged();
//        }
//    }

}
