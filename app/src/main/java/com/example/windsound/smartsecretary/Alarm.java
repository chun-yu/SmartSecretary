package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class Alarm extends Activity {

    private Button btnNewAlarm;
    private AlarmManager alarmManager;
    private ListView alarmList;
    ArrayAdapter arrAdap;
    ArrayList<String> alarmTimeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_layout);

        initView();
        btnNewAlarm.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                new TimePickerDialog(Alarm.this, new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStr = hourOfDay + ":" + minute;
                        alarmTimeList.add(timeStr);
                        arrAdap.add(timeStr);
                        arrAdap.notifyDataSetChanged();
                    }
                }, hour, minute, false).show();
            }
        });
        arrAdap = new ArrayAdapter(Alarm.this,
                android.R.layout.simple_list_item_1);
        alarmList.setAdapter(arrAdap);

        alarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(Alarm.this)
                        .setTitle("Want to delele?")
                        .setMessage("Want to delete alarm " + alarmTimeList.get(position))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                arrAdap.remove(alarmTimeList.get(position));
                                arrAdap.notifyDataSetChanged();
                                alarmTimeList.remove(position);
                                //Log.d("AlarmTimeList Size : ", alarmTimeList.size() + "");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return false;
            }
        });
    }

    private void initView() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        btnNewAlarm = (Button)findViewById(R.id.btnNewAlarm);
        alarmList = (ListView)findViewById(R.id.alarmList);
    }
}
