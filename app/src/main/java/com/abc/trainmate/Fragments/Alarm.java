package com.abc.trainmate.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.abc.trainmate.Destinations;
import com.abc.trainmate.R;

public class Alarm extends Fragment {

    public Alarm() {
        // Required empty public constructor
    }
  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_alarm, container, false);
      Button b=v.findViewById(R.id.dest);
      b.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(getActivity(), Destinations.class));
          }
      });
        return v;
    }

}
