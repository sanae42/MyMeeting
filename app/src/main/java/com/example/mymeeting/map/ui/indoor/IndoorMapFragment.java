package com.example.mymeeting.map.ui.indoor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mymeeting.R;

public class IndoorMapFragment extends Fragment {

    private IndoorMapViewModel indoorMapViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        indoorMapViewModel =
//                new ViewModelProvider(this).get(IndoorMapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_indoor, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
//        indoorMapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}