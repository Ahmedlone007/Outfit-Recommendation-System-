package com.example.myapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapp.Model.User;
import com.example.myapp.Model.WardRob;
import com.example.myapp.PrefManager;
import com.example.myapp.R;

import java.util.List;

public class HomeRcAdapter extends RecyclerView.Adapter<HomeRcAdapter.ViewHolder> {

    private List<WardRob> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private User user = null;

    public interface OnItemClickListener {
        void onItemClick(WardRob item);
    }

    public HomeRcAdapter(Context context, List<WardRob> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.onItemClickListener = listener;
        this.user = new PrefManager(context).getUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WardRob item = dataList.get(position);

        // Bind your data to UI components in the ViewHolder
        // For example, holder.imageView.setImageResource(item.getImageResId());
//        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);
//        Glide.with(context).load(R.drawable.dress2).into(holder.imageView);
        if(user.getGender().toString().toLowerCase().equals("male")){
            holder.imageView.setImageResource(R.drawable.image_male_1);
        }else {
            holder.imageView.setImageResource(R.drawable.dress2);
        }

        if(position == 0 ){
            if(user.getGender().toString().toLowerCase().equals("male")){
                holder.imageView.setImageResource(R.drawable.image_male);
            }else {
                holder.imageView.setImageResource(R.drawable.dress1);
            }

        }


        holder.title.setText(item.getName());
        holder.des.setText(item.getId());

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define your UI components for each item
        // For example, ImageView imageView;
        ImageView imageView;
        TextView title, des;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_rc_home);
            title = itemView.findViewById(R.id.title_rc_home);
            des = itemView.findViewById(R.id.des_rc_home);
            // Initialize your UI components
            // For example, imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
