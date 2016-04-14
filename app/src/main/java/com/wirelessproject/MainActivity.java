package com.wirelessproject;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button transmitter = (Button) findViewById(R.id.transmitter);
        Button receiver = (Button) findViewById(R.id.receiver);

        transmitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TransmitterActivity.class);
                startActivity(intent);
            }
        });

        receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReceiverActivity.class);
                startActivity(intent);
            }
        });
    }


//    public void transmitterButton(View view){
//        Intent intent = new Intent(this, TransmitterActivity.class);
//
//    }
    @Override
    protected void onResume() {
        super.onResume();
    }

}