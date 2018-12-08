package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                    startActivity(new Intent().setClass(WelcomeActivity.this,SmartSecretary.class));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
