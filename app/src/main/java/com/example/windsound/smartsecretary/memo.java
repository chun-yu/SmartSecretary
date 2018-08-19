package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;

public class memo extends Activity {

    private DBHelper helper = null;
    private Button back_btn2,search_btn,addmemo_fbtn;
    private ListView memolist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_secretary);
        back_btn2 = (Button) findViewById(R.id.button1);
        back_btn2.setOnClickListener( new View.OnClickListener(){
            public void onClick (View v){
                SmartSecretary smartSecretary = new SmartSecretary(v);
            }
        });
    }

}
