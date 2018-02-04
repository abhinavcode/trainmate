package com.abc.trainmate.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abc.trainmate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Messages extends Fragment {


    public Messages() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_messages, container, false);
        return v;
    }

}
