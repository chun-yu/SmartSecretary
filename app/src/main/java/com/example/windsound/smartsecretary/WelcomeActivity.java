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
                    Thread.sleep(1800);
                    startActivity(new Intent().setClass(WelcomeActivity.this,SmartSecretary.class));
                    finish();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
