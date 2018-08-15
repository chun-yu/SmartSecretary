package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
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

    private DBHelper helper = null;
    private Button btnNewAlarm;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private ListView alarmList;
    int counter = 0;
    Cursor cursor;
    ArrayAdapter arrAdap;
    ArrayList<String> alarmTimeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_layout);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();
        //helper.close();

        initView();
        showTime();
        setAlarm(10, 1);
        btnNewAlarm.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);
                new TimePickerDialog(Alarm.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStr = "";
                        if (minute < 10)
                            timeStr = hourOfDay + ":0" + minute;
                        else
                            timeStr = hourOfDay + ":" + minute;
                        counter++;
                        helper.insertInfo(write_db, timeStr, 1,null, null);
                        alarmTimeList.add(timeStr);
                        arrAdap.notifyDataSetChanged();
                    }
                }, hour, min, false).show();
            }
        });
        arrAdap = new ArrayAdapter(Alarm.this,
                android.R.layout.simple_list_item_1,
                alarmTimeList);
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
                                helper.remove_Time(write_db, position+1);
                                alarmTimeList.remove(position);
                                counter--;
                                arrAdap.notifyDataSetChanged();
                                //Log.d("AlarmTimeList Size : ", alarmTimeList.size() + "");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return true;
            }
        });
        alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                int hour = Integer.parseInt(alarmTimeList.get(position).split(":")[0]);
                int min = Integer.parseInt(alarmTimeList.get(position).split(":")[1]);
                new TimePickerDialog(Alarm.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStr = "";
                        if (minute < 10)
                            timeStr = hourOfDay + ":0" + minute;
                        else
                            timeStr = hourOfDay + ":" + minute;
                        helper.updateTimeInfo(write_db, position+1,timeStr, 1,null, null);
                        alarmTimeList.set(position, timeStr);
                        arrAdap.notifyDataSetChanged();
                        /*
                        for (int i = 0; i < alarmTimeList.size(); i++)
                            Log.d("AlarmTimeList : ", alarmTimeList.get(i) + "");
                        */
                    }
                }, hour, min, false).show();

            }
        });
    }

    private void initView() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        btnNewAlarm = (Button)findViewById(R.id.btnNewAlarm);
        alarmList = (ListView)findViewById(R.id.alarmList);
    }

    private void showTime() {
        cursor = helper.getInfo(helper.getReadableDatabase());
        if (cursor != null) {
            cursor.moveToFirst();
            //Log.d("getCount() ", cursor.getCount() + "");
            for (int i = 0; i < cursor.getCount(); i++) {
                counter++;
                int id = cursor.getInt(0);
                int number = cursor.getInt(1);
                String time = cursor.getString(2);
                Log.d("ID ", id + "");
                Log.d("COUNTER ", number + "");
                Log.d("TIME ", time);
                alarmTimeList.add(time);
                cursor.moveToNext();
            }
            //Log.d("getCount() ", cursor.getCount() + "");
        }
    }

    void setAlarm(int hour, int min) {
        Intent intent = new Intent(Alarm.this, AlarmReceiver.class);
        intent.putExtra("msg", "time's_up");
        alarmIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
                , alarmIntent);

    }
}
