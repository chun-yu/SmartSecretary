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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class Alarm extends Activity {

    private DBHelper helper = null;
    private Button btnNewAlarm;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private ListView alarmList;
    LinearLayout LLV, LLH;
    TextView tvAlarmTime, tvID;
    Cursor cursor;
    Switch swAlarm;
    ArrayAdapter arrAdap;
    ArrayList<String> alarmTimeList = new ArrayList<String>();
    ArrayList<Integer> alarmIDList = new ArrayList<Integer>();
    String today_date = AddItem.getToday();
<<<<<<< HEAD
    LayoutInflater inflater;
    View view_alarm_display;
=======
>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_layout);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();
        //helper.close();

        initView();
        showTime();
        //setAlarm(13, 3, RC);
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
                        helper.insertInfo(write_db, timeStr, 1, today_date, null, null);
                        updateIDList();
                        setAlarm(hourOfDay, minute, alarmIDList.get(alarmIDList.size()-1));
                        //arrAdap.notifyDataSetChanged();
                        LLV.removeView(view_alarm_display);
                        tvAlarmTime.setText(timeStr);
                        tvAlarmTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Alarm.this,AlarmSetting.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("time", tvAlarmTime.getText().toString());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                        LLV.addView(view_alarm_display);
                    }
                }, hour, min, false).show();
            }
        });

        /*
        arrAdap = new ArrayAdapter(Alarm.this,
                android.R.layout.select_dialog_item,
                alarmTimeList);
        alarmList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
                                helper.remove_Time(write_db, alarmIDList.get(position));
                                cancelAlarm(alarmIDList.get(position));
                                alarmTimeList.remove(position);
                                alarmIDList.remove(position);
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
                        helper.updateTimeInfo(write_db, alarmIDList.get(position),timeStr, 1, today_date, null, null);
                        cancelAlarm(alarmIDList.get(position));
                        setAlarm(hourOfDay, minute, alarmIDList.get(position));
                        alarmTimeList.set(position, timeStr);
                        arrAdap.notifyDataSetChanged();
                        //for (int i = 0; i < alarmTimeList.size(); i++)
                        //Log.d("AlarmTimeList : ", alarmTimeList.get(i) + "");
                    }
                }, hour, min, false).show();
            }
        });
        */
    }

    private void initView() {
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        btnNewAlarm = (Button)findViewById(R.id.btnNewAlarm);
        //alarmList = (ListView)findViewById(R.id.alarmList);
        LLV = (LinearLayout)findViewById(R.id.LLV);
    }

    private void showTime() {
        cursor = helper.getInfo(helper.getReadableDatabase());
        alarmTimeList.clear();
        alarmIDList.clear();
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
                tvAlarmTime = new TextView(getBaseContext());
                view_alarm_display = inflater.inflate(R.layout.alarm_display , null, true);
                tvAlarmTime = (TextView) view_alarm_display.findViewById(R.id.tvAlarmTime);
                swAlarm = (Switch) view_alarm_display.findViewById(R.id.swAlarm);
                LLH = (LinearLayout) view_alarm_display.findViewById(R.id.LLH);
                tvID = (TextView) view_alarm_display.findViewById(R.id.tvID);
                tvAlarmTime.setText(time);
                tvID.setText(id + "");
                tvAlarmTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Alarm.this,AlarmSetting.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("time", time);
                        bundle.putInt("ID", id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                LLV.addView(view_alarm_display);
                if (check == 1) {
                    alarmTimeList.add(time);
                    alarmIDList.add(id);
                    swAlarm.setChecked(true);
                }
                cursor.moveToNext();
            }
            //Log.d("getCount() ", cursor.getCount() + "");
        }
    }

    void updateIDList() {
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