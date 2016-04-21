package com.mapbox.mapboxsdk.android.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Alec Reser on 2/25/2016.
 */
public class SendFragment extends Fragment {

    public SendFragment(){}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        TextView address = (TextView) view.findViewById(R.id.send_address);
        TextView title = (TextView) view.findViewById(R.id.send_title);
        TextView phoneNumber = (TextView) view.findViewById(R.id.send_number);

        address.setText(getArguments().getString("Address"));
        title.setText("Morgan Freeman");
        phoneNumber.setText("867-5309");
        return view;
    }
}
