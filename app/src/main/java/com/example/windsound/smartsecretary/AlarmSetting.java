package com.example.windsound.smartsecretary;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

public class AlarmSetting extends AppCompatActivity {

    TextView tvAlarmDate, _tvAlarmTime, tvAlarmSound;
    private DBHelper helper = null;
    String today_date = AddItem.getToday();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();

        initView();
        Bundle bundle = this.getIntent().getExtras();
        final String time =  bundle.getString("time");
        final int id = bundle.getInt("ID");

        Log.d("time", time);
        Log.d("ID", id + "");

        tvAlarmDate.setText(today_date);
        _tvAlarmTime.setText(time);
        _tvAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = Integer.parseInt(time.split(":")[0]);
                int min = Integer.parseInt(time.split(":")[1]);
                new TimePickerDialog(AlarmSetting.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStr = "";
                        if (minute < 10)
                            timeStr = hourOfDay + ":0" + minute;
                        else
                            timeStr = hourOfDay + ":" + minute;
                        helper.updateTimeInfo(write_db, id, timeStr, 1, today_date, null, null);
                        _tvAlarmTime.setText(timeStr);
                    }
                }, hour, min, false).show();
            }
        });

    }

    void initView() {
        tvAlarmDate = (TextView) findViewById(R.id.tvAlarmDate);
        _tvAlarmTime = (TextView) findViewById(R.id._tvAlarmTime);
        tvAlarmSound = (TextView) findViewById(R.id.tvAlarmSound);
    }
}
