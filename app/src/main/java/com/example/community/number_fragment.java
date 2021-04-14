package com.example.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class number_fragment extends Fragment {
    private Button sit_up;
    private Button squat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_number_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sit_up=view.findViewById(R.id.number_situp);
        squat=view.findViewById(R.id.number_squat);
        sit_up.setOnClickListener(listener);
        squat.setOnClickListener(listener);
    }
    View.OnClickListener listener= new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.number_situp){
                Intent i = new Intent(getActivity(),number_situp.class);
                startActivity(i);
            }
            if(v.getId()==R.id.number_squat){
                Intent i2 = new Intent(getActivity(),number_squat.class);
                startActivity(i2);
            }
        }
    };
}