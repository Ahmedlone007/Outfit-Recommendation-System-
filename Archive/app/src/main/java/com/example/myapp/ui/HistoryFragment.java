package com.example.myapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Common.DbUtils;
import com.example.myapp.Model.WardRob;
import com.example.myapp.adapters.HistoryRcAdapter;
import com.example.myapp.adapters.HomeRcAdapter;
import com.example.myapp.databinding.FragmentHistoryBinding;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initRecycleView();
        return root;
    }

    void initRecycleView() {
        RecyclerView recyclerView = binding.rcvHistory;
        LinearLayout nodata = binding.nodata;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        List<JSONArray> history = DbUtils.getHistory();
        Log.d("history","size: "+history.size());
         if(!history.isEmpty()) {
             nodata.setVisibility(View.GONE);
         }
        HistoryRcAdapter adapter = new HistoryRcAdapter(getActivity(), history, item -> {
            // Handle item click here
            Toast.makeText(getActivity(), "Clicked on item: " + item.toString(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}