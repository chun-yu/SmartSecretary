package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class AlarmSetting extends Activity {

    TextView tvAlarmDate, _tvAlarmTime, tvAlarmSound;
    Button btnBack, btnDetermine;
    private DBHelper helper = null;
    String time = "", date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();

        initView();
        Bundle bundle = this.getIntent().getExtras();
        final int index = bundle.getInt("index");

        date = Alarm.alarmDateList.get(index);
        tvAlarmDate.setText(date);
        tvAlarmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = Integer.parseInt(date.split("/")[0]);
                int month = Integer.parseInt(date.split("/")[1]);
                int day = Integer.parseInt(date.split("/")[2]);
                new DatePickerDialog(AlarmSetting.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        date = year + "/" + (month+1) + "/" + day;
                        tvAlarmDate.setText(date);
                    }
                }, year, month-1, day).show();
            }
        });
        time = Alarm.alarmTimeList.get(index);
        _tvAlarmTime.setText(time);
        _tvAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = Integer.parseInt(time.split(":")[0]);
                int min = Integer.parseInt(time.split(":")[1]);
                new TimePickerDialog(AlarmSetting.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute < 10)
                            time = hourOfDay + ":0" + minute;
                        else
                            time = hourOfDay + ":" + minute;
                        if (hourOfDay < 10)
                            time = "0" + time;
                        _tvAlarmTime.setText(time);
                    }
                }, hour, min, false).show();
            }
        });

        btnBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        btnDetermine.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                helper.updateTimeInfo(write_db, Alarm.alarmIDList.get(index), time, 1, date, null, null);
                Toast.makeText(AlarmSetting.this, "時間已修改為 " + time, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AlarmSetting.this,Alarm.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    void initView() {
        tvAlarmDate = (TextView) findViewById(R.id.tvAlarmDate);
        _tvAlarmTime = (TextView) findViewById(R.id._tvAlarmTime);
        tvAlarmSound = (TextView) findViewById(R.id.tvAlarmSound);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnDetermine = (Button) findViewById(R.id.btnDetermine);
    }
}
