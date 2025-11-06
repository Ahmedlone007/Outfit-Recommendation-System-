package com.example.myapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Model.User;
import com.example.myapp.Model.WardRob;
import com.example.myapp.PrefManager;
import com.example.myapp.R;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HistoryRcAdapter extends RecyclerView.Adapter<HistoryRcAdapter.ViewHolder> {

    private List<JSONArray> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private User user = null;

    public interface OnItemClickListener {
        void onItemClick(WardRob item);
    }

    public HistoryRcAdapter(Context context, List<JSONArray> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.onItemClickListener = listener;
        this.user = new PrefManager(context).getUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONArray currentResults = dataList.get(position);
        StringBuilder results = new StringBuilder();

        // Bind your data to UI components in the ViewHolder
        // For example, holder.imageView.setImageResource(item.getImageResId());
//        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);
        holder.title.setText("Outfit "+ (position+1));
        try {
        for (int i = 0; i < currentResults.length(); i++) {
            JSONObject result = currentResults.getJSONObject(i);

            String label = result.getString("label");
            String value = result.getString("value");

            results.append(label).append(": ").append(value).append("\n");
        }

            holder.des.setText(results);
            if(user.getGender().toString().toLowerCase().equals("male")){
                holder.imageView.setImageResource(R.drawable.male_result);
            }else {
                holder.imageView.setImageResource(R.drawable.ai);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



//        holder.itemView.setOnClickListener(view -> {
//            if (onItemClickListener != null) {
//                onItemClickListener.onItemClick(item);
//            }
//        });
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
