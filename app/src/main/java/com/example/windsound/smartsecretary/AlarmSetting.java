package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmSetting extends Activity {

    TextView tvAlarmDate, _tvAlarmTime, tvAlarmSound,back_bottom,determine_bottom;
    Button btnBack, btnDetermine;
    private DBHelper helper = null;
    String oriTime = "", oriDate = "", time = "", date = "", song = "", songPath = "";
    int year = 0, month = 0, day = 0, hour = 0, min = 0;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
    int int_chose_time;int int_now;
    TimePicker timePicker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting);

        initView();
        Bundle bundle = this.getIntent().getExtras();
        final int index = bundle.getInt("index");
        Log.e("INDEX", index + "");
        Log.e("TEST", bundle.getString("test") + "");

        if (bundle.getString("song") != null)
            song = bundle.getString("song");
        else
            song = Alarm.songList.get(index);
        if (bundle.getString("songPath") != null)
            songPath = bundle.getString("songPath");
        else
            songPath = Alarm.songPathList.get(index);

        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();

        date = oriDate = Alarm.alarmDateList.get(index);
        time = oriTime = Alarm.alarmTimeList.get(index);

        year = Integer.parseInt(oriDate.split("/")[0]);
        month = Integer.parseInt(oriDate.split("/")[1]);
        month--;
        day = Integer.parseInt(oriDate.split("/")[2]);
        hour = Integer.parseInt(oriTime.split(":")[0]);
        min = Integer.parseInt(oriTime.split(":")[1]);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
        tvAlarmDate.setText(oriDate);
        tvAlarmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AlarmSetting.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _day) {
                        year = _year;
                        month = _month;
                        day = _day;
                        if (month < 9 && day < 10)
                            date = year + "/0" + (month+1) + "/0" + day;
                        else if (month < 9)
                            date = year + "/0" + (month+1) + "/" + day;
                        else if (day < 10)
                            date = year + "/" + (month+1) + "/0" + day;
                        else
                            date = year + "/" + (month+1) + "/" + day;
                        tvAlarmDate.setText(date);
                    }
                }, year, month, day).show();
            }
        });

        _tvAlarmTime.setText(oriTime);
        _tvAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AlarmSetting.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timePicker.setCurrentHour(hourOfDay);
                        timePicker.setCurrentMinute(minute);
                        if (minute < 10)
                            time = hourOfDay + ":0" + minute;
                        else
                            time = hourOfDay + ":" + minute;
                        if (hourOfDay < 10)
                            time = "0" + time;
                        hour = hourOfDay;
                        min = minute;
                        _tvAlarmTime.setText(time);
                    }
                }, hour, min, false).show();
            }
        });

        tvAlarmSound.setText(song);
        tvAlarmSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmSetting.this, MusicList.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                intent.putExtras(bundle);
                startActivityForResult(intent,100);
            }
        });

        btnBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back_bottom.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDetermine.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(is_past_time_return()){
                    showAlertDialog(write_db,index,time,date);
                }else{
                    if ((time == oriTime && date == oriDate) || !Alarm.isTimeExist(date, time)) {
                        helper.updateTimeInfo(write_db, Alarm.alarmIDList.get(index), time, 1, date, Alarm.titleList.get(index), Alarm.noteList.get(index), song, songPath);
                        Alarm.cancelAlarm(AlarmSetting.this, Alarm.alarmIDList.get(index));
                        Alarm.setAlarm(AlarmSetting.this, year, month+1, day, hour, min, Alarm.alarmIDList.get(index),Alarm.titleList.get(index));
                        Toast.makeText(AlarmSetting.this, "時間已修改為 " + time, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AlarmSetting.this, "該時間已設定過", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        determine_bottom.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(is_past_time_return()){
                    showAlertDialog(write_db,index,time,date);
                }else{
                    if ((time == oriTime && date == oriDate) || !Alarm.isTimeExist(date, time)) {
                        helper.updateTimeInfo(write_db, Alarm.alarmIDList.get(index), time, 1, date, Alarm.titleList.get(index), Alarm.noteList.get(index), song, songPath);
                        Alarm.cancelAlarm(AlarmSetting.this, Alarm.alarmIDList.get(index));
                        Alarm.setAlarm(AlarmSetting.this, year, month+1, day, hour, min, Alarm.alarmIDList.get(index),Alarm.titleList.get(index));
                        Toast.makeText(AlarmSetting.this, "時間已修改為 " + time, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AlarmSetting.this, "該時間已設定過", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int _hour, int _min) {
                if (_min < 10)
                    time = _hour + ":0" + _min;
                else
                    time = _hour + ":" + _min;
                if (_hour < 10)
                    time = "0" + time;
                hour = _hour;
                min = _min;
                _tvAlarmTime.setText(time);
            }
        });
    }

    void initView() {
        tvAlarmDate = (TextView) findViewById(R.id.tvAlarmDate);
        _tvAlarmTime = (TextView) findViewById(R.id._tvAlarmTime);
        tvAlarmSound = (TextView) findViewById(R.id.tvAlarmSound);
        back_bottom = (TextView) findViewById(R.id.back_bottom);
        determine_bottom = (TextView) findViewById(R.id.determine_bottom);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnDetermine = (Button) findViewById(R.id.btnDetermine);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
    }

    private boolean is_past_time_return(){
        chose_now_time_compare();
        if(int_now>=int_chose_time){
            return true;
        }else{
            return false;
        }
    }
    private void chose_now_time_compare(){
        String[] splitarrary = new String[3];String[] splitarrary2 = new String[2];
        splitarrary = sdf.format(Calendar.getInstance().getTime()).split("/");
        splitarrary2 = sdf2.format(Calendar.getInstance().getTime()).split(":");
        int_now= Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                +Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
        splitarrary = tvAlarmDate.getText().toString().split("/");
        splitarrary2 = _tvAlarmTime.getText().toString().split(":");
        int_chose_time= Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                +Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
    }
    void showAlertDialog(final SQLiteDatabase write_db,final int index,final String _time,final String _date) {
        new AlertDialog.Builder(AlarmSetting.this)
                .setTitle(getString(R.string.brfore_time)+"\n"+getString(R.string.sure_change))
                .setMessage("\n"+getString(R.string.time) +" : " + time +"\n\n"
                        + getString(R.string.date) + " : " + date + "           ")
                .setPositiveButton(getString(R.string.sure_determine), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.updateTimeInfo(write_db, Alarm.alarmIDList.get(index), _time, 0, _date, Alarm.titleList.get(index), Alarm.noteList.get(index), song, songPath);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = new Bundle();
        int index = bundle.getInt("index");
        if (resultCode == RESULT_OK) {
            if (bundle.getString("song") != null)
                song = bundle.getString("song");
            else
                song = Alarm.songList.get(index);
            if (bundle.getString("songPath") != null)
                songPath = bundle.getString("songPath");
            else
                songPath = Alarm.songPathList.get(index);
        }
        tvAlarmSound.setText(song);
    }
}
