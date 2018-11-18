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
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class Alarm extends Activity {

    static DBHelper helper = null;
    private FloatingActionButton fabNewAlarm;
    //private AlarmManager alarmManager;
    //private PendingIntent alarmIntent;
    LinearLayout LLV, LLV_display;
    TextView tvAlarmTime;
    static Cursor cursor;
    Switch swAlarm;
    public static ArrayList<String> alarmTimeList = new ArrayList<String>();
    public static ArrayList<String> alarmDateList = new ArrayList<String>();
    public static ArrayList<Integer> alarmIDList = new ArrayList<Integer>();
    ArrayList<Integer> checkList = new ArrayList<Integer>();
    ArrayList<View> viewList = new ArrayList<View>();
    ArrayList<TextView> tvList = new ArrayList<TextView>();
    public static ArrayList<Switch> switchList = new ArrayList<Switch>();
    public static ArrayList<String> titleList = new ArrayList<String>();
    public static ArrayList<String> noteList = new ArrayList<String>();
    public static ArrayList<String> songList = new ArrayList<String>();
    public static ArrayList<String> songPathList = new ArrayList<String>();
    String today_date = AddItem.getToday();
    LayoutInflater inflater;
    View view_alarm_display;
    int YMD[] = new int[3];
    int nowYear, nowMonth, nowDate, nowHour, nowMin;
    Button btnBack ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_layout);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();
        //        //helper.close();

        initView();
        showTime(write_db);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            int index = b.getInt("index");
            helper.updateTimeInfo(write_db, alarmIDList.get(index), alarmTimeList.get(index), 0, alarmDateList.get(index), titleList.get(index), noteList.get(index), songList.get(index), songPathList.get(index));
            checkList.set(index, 0);
            switchList.get(index).setChecked(false);
        }

        fabNewAlarm.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                final int nowHour = c.get(Calendar.HOUR_OF_DAY);
                final int nowMin = c.get(Calendar.MINUTE);
                new TimePickerDialog(Alarm.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStr = hourOfDay + ":" + minute;
                        if (minute < 10)
                            timeStr = hourOfDay + ":0" + minute;
                        if (hourOfDay < 10)
                            timeStr = "0" + timeStr;

                        setDate(hourOfDay, minute);
                        String dateStr = "";
                        if (YMD[1] < 10 && YMD[2] < 10)
                            dateStr = YMD[0] + "/0" + YMD[1] + "/0" + YMD[2];
                        else if (YMD[1] < 10)
                            dateStr = YMD[0] + "/0" + YMD[1] + "/" + YMD[2];
                        else if (YMD[2] < 10)
                            dateStr = YMD[0] + "/" + YMD[1] + "/0" + YMD[2];
                        else
                            dateStr = YMD[0] + "/" + YMD[1] + "/" + YMD[2];
                        if (!isTimeExist(dateStr, timeStr)) {
                            helper.insertInfo(write_db, timeStr, 1, dateStr, null, null, "預設", null);
                            updateIDList();
                            alarmTimeList.add(timeStr);
                            alarmDateList.add(dateStr);
                            checkList.add(1);
                            titleList.add(null);
                            noteList.add(null);
                            songList.add("預設");
                            songPathList.add(null);
                            setAlarm(Alarm.this, YMD[0], YMD[1], YMD[2], hourOfDay, minute, alarmIDList.get(alarmIDList.size() - 1));
                            Toast.makeText(Alarm.this, "鬧鐘已設定 時間為" + timeStr, Toast.LENGTH_SHORT).show();
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
                            final LinearLayout space = new LinearLayout(Alarm.this);
                            LinearLayout.LayoutParams  params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,20);
                            space.setLayoutParams(params2);
                            LLV.addView(space);
                            LLV.addView(view_alarm_display);
                        }
                        else {
                            Toast.makeText(Alarm.this, "該時間已設定過", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, nowHour, nowMin, false).show();
            }
        });
        btnBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        fabNewAlarm = (FloatingActionButton)findViewById(R.id.fabNewAlarm);
        LLV = (LinearLayout)findViewById(R.id.LLV);
        btnBack = (Button) findViewById(R.id.btnBack);
    }

    private void setListener(final SQLiteDatabase db, Switch sw, TextView tv, final int index) {
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    int hour = Integer.parseInt(alarmTimeList.get(index).split(":")[0]);
                    int min = Integer.parseInt(alarmTimeList.get(index).split(":")[1]);

                    setDate(hour, min);
                    String dateStr = "";
                    if (YMD[1] < 10 && YMD[2] < 10)
                        dateStr = YMD[0] + "/0" + YMD[1] + "/0" + YMD[2];
                    else if (YMD[1] < 10)
                        dateStr = YMD[0] + "/0" + YMD[1] + "/" + YMD[2];
                    else if (YMD[2] < 10)
                        dateStr = YMD[0] + "/" + YMD[1] + "/0" + YMD[2];
                    else
                        dateStr = YMD[0] + "/" + YMD[1] + "/" + YMD[2];
                    alarmDateList.set(index, dateStr);
                    helper.updateTimeInfo(db, alarmIDList.get(index), alarmTimeList.get(index), 1, dateStr, titleList.get(index), noteList.get(index), songList.get(index), songPathList.get(index));
                    setAlarm(Alarm.this, YMD[0], YMD[1], YMD[2], hour, min, alarmIDList.get(index));
                    viewList.get(index).setEnabled(true);
                    Toast.makeText(Alarm.this, "鬧鐘已設定 時間為" + alarmTimeList.get(index), Toast.LENGTH_SHORT).show();
                }
                else {
                    helper.updateTimeInfo(db, alarmIDList.get(index), alarmTimeList.get(index), 0, alarmDateList.get(index), titleList.get(index), noteList.get(index), songList.get(index), songPathList.get(index));
                    cancelAlarm(Alarm.this, alarmIDList.get(index));
                    viewList.get(index).setEnabled(false);
                    Log.d("Switch", "close");
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Alarm.this,AlarmSetting.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                intent.putExtras(bundle);
                startActivityForResult(intent,100);
            }
        });
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try
                {
                    showAlertDialog(db, alarmTimeList.get(index), alarmIDList.get(index));
                }catch(Exception e)
                {
                    Log.d("TextView Long click", "error");
                    return false;
                }
                return false;
            }
        });
    }

    public void showTime(final SQLiteDatabase write_db) {
        cursor = helper.getInfo(helper.getReadableDatabase());
        for (int i = LLV.getChildCount(); i >= 0; i--) {
            LLV.removeView(LLV.getChildAt(i));
        }
        alarmTimeList.clear();
        alarmDateList.clear();
        alarmIDList.clear();
        viewList.clear();
        switchList.clear();
        tvList.clear();
        checkList.clear();
        titleList.clear();
        noteList.clear();
        songList.clear();
        songPathList.clear();
        if (cursor != null) {
            cursor.moveToFirst();
            //Log.d("getCount() ", cursor.getCount() + "");
            for (int i = 0; i < cursor.getCount(); i++) {
                final int id = cursor.getInt(0);
                final String time = cursor.getString(1);
                int check = cursor.getInt(2);
                String date = cursor.getString(3);
                String title = cursor.getString(4);
                String note = cursor.getString(5);
                String song = cursor.getString(6);
                String songPath = cursor.getString(7);
                Log.d("ID ", id + "");
                Log.d("TIME ", time);
                Log.d("CHECK ", check + "");
                Log.d("DATE ", date + "");
                Log.d("TITLE ", title + "");
                Log.d("SONG ", song + "");
                Log.d("SONGPATH ", songPath + "");
                view_alarm_display = inflater.inflate(R.layout.alarm_display , null, true);
                viewList.add(view_alarm_display);
                alarmTimeList.add(time);
                alarmDateList.add(date);
                alarmIDList.add(id);
                checkList.add(check);
                titleList.add(title);
                noteList.add(note);
                songList.add(song);
                songPathList.add(songPath);
                tvAlarmTime = (TextView) view_alarm_display.findViewById(R.id.tvAlarmTime);
                tvList.add(tvAlarmTime);
                swAlarm = (Switch) view_alarm_display.findViewById(R.id.swAlarm);
                switchList.add(swAlarm);
                LLV_display = (LinearLayout) view_alarm_display.findViewById(R.id.LLV_display);
                if (title != null)
                    tvAlarmTime.setText(time + " " + title);
                else
                    tvAlarmTime.setText(time);
                final LinearLayout space = new LinearLayout(Alarm.this);
                LinearLayout.LayoutParams  params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,20);
                space.setLayoutParams(params2);
                if (check == 1) {
                    swAlarm.setChecked(true);
                    view_alarm_display.setEnabled(true);
                }else{
                    view_alarm_display.setEnabled(false);
                }
                LLV.addView(space);
                LLV.addView(view_alarm_display);
                setListener(write_db, swAlarm, tvAlarmTime, i);
                cursor.moveToNext();
            }
            //Log.d("getCount() ", cursor.getCount() + "");
        }
    }

    public static boolean isTimeExist(String newDate, String newTime) {
        cursor = helper.getInfo(helper.getReadableDatabase());
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String time = cursor.getString(1);
                String date = cursor.getString(3);
                if (newDate.equals(date) && newTime.equals(time))
                    return true;
                cursor.moveToNext();
            }
        }
        return false;
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

    void setDate(int hour, int min) {
        nowYear = Integer.parseInt(today_date.split("/")[0]);
        nowMonth = Integer.parseInt(today_date.split("/")[1]);
        nowDate = Integer.parseInt(today_date.split("/")[2]);
        final Calendar c = Calendar.getInstance();
        nowHour = c.get(Calendar.HOUR_OF_DAY);
        nowMin = c.get(Calendar.MINUTE);

        if (hour < nowHour || (hour == nowHour && min <= nowMin)) {
            if (nowMonth == 1 || nowMonth == 3 || nowMonth == 5 || nowMonth == 7 || nowMonth == 8 || nowMonth == 10 || nowMonth == 12) {
                nowDate = (nowDate % 31) + 1;
                if (nowDate == 1)
                    nowMonth = (nowMonth % 12) + 1;
                if (nowMonth == 1)
                    nowYear++;
            }
            else if (nowMonth == 4 || nowMonth == 6 || nowMonth == 9 || nowMonth == 11) {
                nowDate = (nowDate % 30) + 1;
                if (nowDate == 1)
                    nowMonth = (nowMonth % 12) + 1;
                if (nowMonth == 1)
                    nowYear++;
            }
            else {
                nowDate = (nowDate % 28) + 1;
                if (nowDate == 1)
                    nowMonth = (nowMonth % 12) + 1;
                if (nowMonth == 1)
                    nowYear++;
            }
        }
        YMD[0] = nowYear;
        YMD[1] = nowMonth;
        YMD[2] = nowDate;
    }

    void showAlertDialog(final SQLiteDatabase db, String timeStr, final int id) {
        new AlertDialog.Builder(Alarm.this)
            .setTitle("Want to delele?")
            .setMessage("Want to delete alarm " + timeStr)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    helper.remove_Time(db, id);
                    cancelAlarm(Alarm.this, id);
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
                    alarmDateList.remove(idx);
                    alarmIDList.remove(idx);
                    titleList.remove(idx);
                    noteList.remove(idx);
                    songList.remove(idx);
                    songPathList.remove(idx);
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

    public static void setAlarm(Context context, int year, int month, int date, int hour, int min, int RC) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("msg", "time's_up");
        int index = alarmIDList.indexOf(RC);
        bundle.putInt("index", index);
        intent.putExtras(bundle);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, RC, intent, FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Log.d("YEAR", year + "");
        Log.d("MONTH", month + "");
        Log.d("DATE", date + "");
        Log.d("HOUR", hour + "");
        Log.d("MIN", min + "");
        calendar.set(year, month-1, date, hour, min, 0);
        //calendar.set(Calendar.HOUR_OF_DAY, hour);
        //calendar.set(Calendar.MINUTE, min);
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
                //, alarmIntent);
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), alarmIntent);
        alarmManager.setAlarmClock(alarmClockInfo, alarmIntent);
    }

    public static void cancelAlarm(Context context, int RC) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, RC, intent, FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);
        alarmIntent = null;
        //Toast.makeText(this, "取消鬧鐘", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SQLiteDatabase write_db = helper.getWritableDatabase();
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                showTime(write_db);
            }
        }
    }
}