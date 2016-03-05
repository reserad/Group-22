package com.mapbox.mapboxsdk.android.testapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    //protected void onCreate(Bundle savedInstanceState) {
/*        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_send);

        final TextView address = (TextView) findViewById(R.id.send_address);
        final TextView title = (TextView) findViewById(R.id.send_title);
        final TextView phoneNumber = (TextView) findViewById(R.id.send_number);
        address.setText(intent.getStringExtra("Address"));
        title.setText("Morgan Freeman");
        phoneNumber.setText("867-5309");
        Button send = (Button) findViewById(R.id.btnAddContact);

        send.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                MainActivity.addToContact(view, title.getText().toString(), address.getText().toString(), phoneNumber.getText().toString());
            }
        });*/
   // }
}
