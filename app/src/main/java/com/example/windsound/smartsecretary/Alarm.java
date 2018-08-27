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
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class Alarm extends Activity {

    private DBHelper helper = null;
    private FloatingActionButton fabNewAlarm;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    LinearLayout LLV, LLV_display;
    TextView tvAlarmTime;
    Cursor cursor;
    Switch swAlarm;
    ArrayList<String> alarmTimeList = new ArrayList<String>();
    ArrayList<Integer> alarmIDList = new ArrayList<Integer>();
    ArrayList<Integer> checkList = new ArrayList<Integer>();
    ArrayList<View> viewList = new ArrayList<View>();
    ArrayList<TextView> tvList = new ArrayList<TextView>();
    ArrayList<Switch> switchList = new ArrayList<Switch>();
    String today_date = AddItem.getToday();
    LayoutInflater inflater;
    View view_alarm_display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_layout);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();
        //helper.close();

        initView();
        showTime(write_db);
        fabNewAlarm.setOnClickListener(new Button.OnClickListener(){
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
                        helper.insertInfo(write_db, timeStr, 1, today_date, null, null);
                        updateIDList();
                        Log.d("ID List size", alarmIDList.size() + "");
                        alarmTimeList.add(timeStr);
                        checkList.add(1);
                        final int id = alarmIDList.get(alarmIDList.size()-1);
                        //setAlarm(hourOfDay, minute, alarmIDList.get(alarmIDList.size()-1));
                        view_alarm_display = inflater.inflate(R.layout.alarm_display , null, true);
                        viewList.add(view_alarm_display);
                        tvAlarmTime = (TextView) view_alarm_display.findViewById(R.id.tvAlarmTime);
                        tvList.add(tvAlarmTime);
                        swAlarm = (Switch) view_alarm_display.findViewById(R.id.swAlarm);
                        switchList.add(swAlarm);
                        LLV_display = (LinearLayout) view_alarm_display.findViewById(R.id.LLV_display);
                        tvAlarmTime.setText(timeStr);
                        swAlarm.setChecked(true);
                        setListener(write_db, swAlarm, tvAlarmTime, alarmIDList.size()-1);
                        LLV.addView(view_alarm_display);
                    }
                }, hour, min, false).show();
            }
        });
    }

    private void initView() {
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        fabNewAlarm = (FloatingActionButton)findViewById(R.id.fabNewAlarm);
        LLV = (LinearLayout)findViewById(R.id.LLV);
    }

    private void setListener(final SQLiteDatabase db, Switch sw, TextView tv, final int index) {
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    helper.updateTimeInfo(db, alarmIDList.get(index), alarmTimeList.get(index), 1, today_date, null, null);
                }
                else {
                    helper.updateTimeInfo(db, alarmIDList.get(index), alarmTimeList.get(index), 0, today_date, null, null);
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Alarm.this,AlarmSetting.class);
                Bundle bundle = new Bundle();
                bundle.putString("time", alarmTimeList.get(index));
                bundle.putInt("ID", alarmIDList.get(index));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try
                {
                    showAlertDialog(db, tvList.get(index).getText().toString(), alarmIDList.get(index));
                }catch(Exception e)
                {
                    Log.d("TextView Long click", "error");
                    return false;
                }
                return false;
            }
        });
    }

    private void showTime(final SQLiteDatabase write_db) {
        cursor = helper.getInfo(helper.getReadableDatabase());
        alarmTimeList.clear();
        alarmIDList.clear();
        viewList.clear();
        switchList.clear();
        tvList.clear();
        checkList.clear();
        if (cursor != null) {
            cursor.moveToFirst();
            //Log.d("getCount() ", cursor.getCount() + "");
            for (int i = 0; i < cursor.getCount(); i++) {
                final int id = cursor.getInt(0);
                final String time = cursor.getString(1);
                int check = cursor.getInt(2);
                String date = cursor.getString(3);
                Log.d("ID ", id + "");
                Log.d("TIME ", time);
                Log.d("CHECK ", check + "");
                Log.d("DATE ", date + "");
                view_alarm_display = inflater.inflate(R.layout.alarm_display , null, true);
                viewList.add(view_alarm_display);
                alarmTimeList.add(time);
                alarmIDList.add(id);
                checkList.add(check);
                tvAlarmTime = (TextView) view_alarm_display.findViewById(R.id.tvAlarmTime);
                tvList.add(tvAlarmTime);
                swAlarm = (Switch) view_alarm_display.findViewById(R.id.swAlarm);
                switchList.add(swAlarm);
                LLV_display = (LinearLayout) view_alarm_display.findViewById(R.id.LLV_display);
                tvAlarmTime.setText(time);
                LLV.addView(view_alarm_display);

                setListener(write_db, swAlarm, tvAlarmTime, i);

                if (check == 1) {
                    swAlarm.setChecked(true);
                }
                cursor.moveToNext();
            }
            //Log.d("getCount() ", cursor.getCount() + "");
        }
    }

    void updateIDList() {
        cursor = helper.getInfo(helper.getReadableDatabase());
        alarmIDList.clear();
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(0);
                alarmIDList.add(id);
                cursor.moveToNext();
            }
        }
    }

    void showAlertDialog(final SQLiteDatabase db, String timeStr, final int id) {
        new AlertDialog.Builder(Alarm.this)
            .setTitle("Want to delele?")
            .setMessage("Want to delete alarm " + timeStr)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    helper.remove_Time(db, id);
                    cancelAlarm(id);
                    int idx = alarmIDList.indexOf(id);
                    Log.d("ID", id + "");
                    Log.d("ID index", idx + "");
                    //Log.d("viewList", viewList.get(idx) + "");
                    Log.d("checkList", checkList.get(idx) + "");
                    //Log.d("switchList", switchList.get(idx) + "");
                    //Log.d("tvList", tvList.get(idx) + "");
                    Log.d("alarmTimeList", alarmTimeList.get(idx) + "");
                    Log.d("alarmIDList", alarmIDList.get(idx) + "");
                    LLV.removeView(viewList.get(idx));
                    viewList.remove(idx);
                    checkList.remove(idx);
                    switchList.remove(idx);
                    tvList.remove(idx);
                    alarmTimeList.remove(idx);
                    alarmIDList.remove(idx);
                    for (int i = idx; i < alarmIDList.size(); i++) {
                        setListener(db, switchList.get(i), tvList.get(i), idx);
                    }
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
    }

    void setAlarm(int hour, int min, int RC) {
        Intent intent = new Intent(Alarm.this, AlarmReceiver.class);
        intent.putExtra("msg", "time's_up");
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(Alarm.this, RC, intent, PendingIntent.FLAG_ONE_SHOT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
                , alarmIntent);
    }

    void cancelAlarm(int RC) {
        Intent intent = new Intent(Alarm.this, AlarmReceiver.class);
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(Alarm.this, RC, intent, FLAG_ONE_SHOT);
        alarmManager.cancel(alarmIntent);
        alarmIntent = null;
    }
}