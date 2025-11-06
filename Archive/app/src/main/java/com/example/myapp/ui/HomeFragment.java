package com.example.myapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Model.WardRob;
import com.example.myapp.activites.NewQuestionsActivity;
import com.example.myapp.adapters.HomeRcAdapter;
import com.example.myapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnGenrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), QuestionsActivity.class));
//                startActivity(new Intent(getActivity(), QuestionAnswerActivity.class));
                startActivity(new Intent(getActivity(), NewQuestionsActivity.class));
            }
        });
        initRecycleView();
        return root;
    }

    void initRecycleView() {
        RecyclerView recyclerView = binding.rcvHome;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        List<WardRob> dataList = generateData(); // Implement your own method to generate data

        HomeRcAdapter adapter = new HomeRcAdapter(getActivity(), dataList, item -> {
            // Handle item click here
            Toast.makeText(getActivity(), "Clicked on item: " + item.toString(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
    }

    private List<WardRob> generateData() {
        // Implement your own logic to generate data
        List<WardRob> dataList = new ArrayList<>();
        dataList.add(new WardRob("", "", "Image"));
        dataList.add(new WardRob("", "", "Image1"));


        return dataList;
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