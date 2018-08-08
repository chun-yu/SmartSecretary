package com.example.windsound.smartsecretary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SmartSecretary extends AppCompatActivity {
    private Button new_button;
    private Button my_alarm_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_secretary);
        new_button = (Button) findViewById(R.id.new_button);
        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SmartSecretary.this, AddItem.class);
                startActivity(intent);
            }
        });
        my_alarm_button = (Button) findViewById(R.id.my_alarm_button);
        my_alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SmartSecretary.this, Alarm.class);
                startActivity(intent);
            }
        });
    }
}
