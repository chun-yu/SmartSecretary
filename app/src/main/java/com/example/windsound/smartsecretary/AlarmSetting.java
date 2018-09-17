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
    String oriTime = "", oriDate = "", time = "", date = "", song = "", songPath = "";
    int year = 0, month = 0, day = 0, hour = 0, min = 0;

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
                startActivity(intent);
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
                if ((time == oriTime && date == oriDate) || !Alarm.isTimeExist(date, time)) {
                    helper.updateTimeInfo(write_db, Alarm.alarmIDList.get(index), time, 1, date, Alarm.titleList.get(index), Alarm.noteList.get(index), song, songPath);
                    Alarm.cancelAlarm(AlarmSetting.this, Alarm.alarmIDList.get(index));
                    Alarm.setAlarm(AlarmSetting.this, year, month+1, day, hour, min, Alarm.alarmIDList.get(index));
                    Toast.makeText(AlarmSetting.this, "時間已修改為 " + time, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AlarmSetting.this, Alarm.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(AlarmSetting.this, "該時間已設定過", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //Bundle bundle = new Bundle();
                //bundle.putInt("index", index);
                //intent.putExtras(bundle);
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
