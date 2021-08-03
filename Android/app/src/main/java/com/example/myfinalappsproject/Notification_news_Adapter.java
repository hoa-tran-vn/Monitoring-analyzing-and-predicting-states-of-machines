package com.example.myfinalappsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Notification_news_Adapter extends RecyclerView.Adapter<Notification_news_Adapter.NewsViewHolder> {

    Context mContext;
    ArrayList<NewsDataObject> mData;
    RecycleViewClickInterface recycleViewClickInterface;

    public Notification_news_Adapter(Context mContext, ArrayList<NewsDataObject> mData, RecycleViewClickInterface recycleViewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recycleViewClickInterface = recycleViewClickInterface;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout;
        layout = LayoutInflater.from(mContext).inflate(R.layout.notification_news,parent,false);
        return new NewsViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        holder.mContent.setText(mData.get(position).getSystemNotification());
        holder.mCategory.setText("Cấp độ "+mData.get(position).getCategory()+"");
        //set animation for View
        holder.mContainer.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_anim));
        holder.mContainer.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.scale_anim));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView mContent,mCategory;
        RelativeLayout mContainer;
        ImageView mNotificationImg;
        TextView mDelete;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.notification_content);
            mCategory = itemView.findViewById(R.id.Category);
            mContainer = itemView.findViewById(R.id.contentView);
            mNotificationImg = itemView.findViewById(R.id.notification_icon);
            mDelete = itemView.findViewById(R.id.Deleete);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recycleViewClickInterface.OnItemClick(getAdapterPosition());
                }
            });

        }
    }
}
