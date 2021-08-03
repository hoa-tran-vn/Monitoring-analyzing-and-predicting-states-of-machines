package com.example.myfinalappsproject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyCellViewGanttChartAdapter extends RecyclerView.Adapter<MyCellViewGanttChartAdapter.RecylerViewHolder> {
    private List<String> listStatus;
    private Context context;
    private boolean isTitle;

    public MyCellViewGanttChartAdapter(List<String> listStatus, Context context, int lengthMax,boolean isTitle,int frame,int hour) {
        if (!isTitle && lengthMax>frame*hour){
            int a = (((lengthMax/frame)-hour+(0!=(lengthMax%frame)?1:0))*frame);
            if (listStatus.size() > a){
                this.listStatus = new ArrayList<>(listStatus.subList(a,listStatus.size()));
            }else {
                this.listStatus = new ArrayList<>();
            }
        }else if (isTitle && listStatus.size()>hour){
            this.listStatus = new ArrayList<>(listStatus.subList(listStatus.size()-hour,listStatus.size()));
        }else {
            this.listStatus = listStatus;
        }
        this.context = context;
        this.isTitle = isTitle;
    }

    @NonNull
    @Override
    public MyCellViewGanttChartAdapter.RecylerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if (isTitle){
            return new RecylerViewHolder(inflater.inflate(R.layout.text_view_item,parent,false));
        }else {
            return new RecylerViewHolder(inflater.inflate(R.layout.item_view,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyCellViewGanttChartAdapter.RecylerViewHolder holder, int position) {
        if (isTitle){
            try {
                String[] strings = listStatus.get(position).split(":");
                int i = Integer.parseInt(strings[1].length()>2?strings[1].substring(0,2):strings[1]);
                if (i<20){
                    holder.textView.setGravity(Gravity.LEFT);
                }else if (i<40){
                    holder.textView.setGravity(Gravity.CENTER);
                }else {
                    holder.textView.setGravity(Gravity.RIGHT);
                }
                if ((listStatus.get(position).charAt(0)+"").equals("[")){
                    holder.textView.setTextColor(Color.parseColor("#00ff00"));
                }else if ((listStatus.get(position).charAt(listStatus.get(position).length()-1)+"").equals("]")){
                    holder.textView.setTextColor(Color.parseColor("#ff0000"));
                }
            }catch (Exception exception){
                try{
                    if ((listStatus.get(position).charAt(0)+"").equals("@")){
                        holder.textView.setGravity(Gravity.LEFT);
                        listStatus.set(position,listStatus.get(position).substring(1));
                    }else if ((listStatus.get(position).charAt(listStatus.get(position).length()-1)+"").equals("@")) {
                        holder.textView.setGravity(Gravity.RIGHT);
                        listStatus.set(position,listStatus.get(position).substring(0,listStatus.get(position).length()-1));
                    }
                }catch (Exception e){}
            }
            holder.textView.setText(listStatus.get(position));
        }else {
            switch (listStatus.get(position).trim().toString()) {
                case "0":
                    holder.view.setBackgroundColor(Color.parseColor("#656565"));
                    break;
                case "1":
                    holder.view.setBackgroundColor(Color.parseColor("#808080"));
                    break;
                case "2":
                    holder.view.setBackgroundColor(Color.parseColor("#FF9A00"));
                    break;
                case "3":
                    holder.view.setBackgroundColor(Color.parseColor("#ffff00"));
                    break;
                case "4":
                    holder.view.setBackgroundColor(Color.parseColor("#0000FE"));
                    break;
                case "5":
                    holder.view.setBackgroundColor(Color.parseColor("#00ff00"));
                    break;
                case "6":
                    holder.view.setBackgroundColor(Color.parseColor("#ff0000"));
                    break;
                case "7":
                    holder.view.setBackgroundColor(Color.parseColor("#23262D"));
                    break;
                default:
                    holder.view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return listStatus.size();
    }

    public class RecylerViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;
        public RecylerViewHolder(@NonNull View itemView) {
            super(itemView);
            if (isTitle){
                textView = itemView.findViewById(R.id.textViewContent);
            }else {
                view = itemView.findViewById(R.id.viewItem);
            }
        }
    }
}
