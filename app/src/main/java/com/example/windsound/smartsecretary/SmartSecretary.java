package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.request.RequestOptions;

public class SmartSecretary extends Activity {
    private Button new_button;
    private Button my_alarm_button;
    private Button my_note_button;
    private Button voice_button;
    static DBHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_secretary);
        new_button = (Button) findViewById(R.id.new_button);
        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SmartSecretary.this, AddItem.class);
                startActivity(intent);
            }
        });
        my_alarm_button = (Button) findViewById(R.id.my_alarm_button);
        my_alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SmartSecretary.this, Alarm.class);
                startActivity(intent);
            }
        });
        my_note_button = (Button) findViewById(R.id.my_note_button);
        my_note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SmartSecretary.this, memo.class);
                startActivity(intent);
            }
        });
        voice_button = (Button) findViewById(R.id.voice_button);
        voice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "恭維啊～");
                try{
                    startActivityForResult(intent,1);
                }catch (ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Calendar c = Calendar.getInstance();
        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();

        int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH) + 1, date = c.get(Calendar.DAY_OF_MONTH), hour = c.get(Calendar.HOUR_OF_DAY), min = c.get(Calendar.MINUTE);
        String title = null, note = null;
        boolean containMonth = false;

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.get(0).contains("月")) {
                    containMonth = true;
                    String resultSplit[] = result.get(0).split("月");
                    month = Integer.parseInt(getNumbers(resultSplit[0]));
                    date = Integer.parseInt(getNumbers(resultSplit[1]));

                    String resultSplit2[] = result.get(0).split(date + "");
                    String resultSplit3[] = resultSplit2[1].split("點");
                    if (resultSplit3.length != 1) {
                        hour = Integer.parseInt(getNumbers(resultSplit3[0]));
                        min = Integer.parseInt(getNumbers(resultSplit3[1]));
                        if (resultSplit3[1].charAt(0) == '半')
                            min = 30;
                    }
                    /*
                    String resultSplit4[];
                    if (min != 0)
                        resultSplit4 = resultSplit3[0].split(min + "");
                    else
                        resultSplit4 = resultSplit3[0].split("點");
                    if (result.get(0).contains("鬧鐘") || result.get(0).contains("叫我")) {
                        title = null;
                        note = null;
                    }
                    else {
                        title = resultSplit4[1];
                        note = resultSplit4[1];
                    }
                    */
                }
                if (result.get(0).contains("明天")) {
                    date++;
                }
                if (result.get(0).contains("後天")) {
                    date += 2;
                }
                if (result.get(0).contains("大後天")) {
                    date += 3;
                }
                if (result.get(0).contains("點")) {
                    String resultSplit[] = result.get(0).split("點");
                    //hour = Integer.parseInt(getNumbers(resultSplit[0]));
                    if (!containMonth) {
                        hour = Integer.parseInt(getNumbers(resultSplit[0]));
                        min = Integer.parseInt(getNumbers(resultSplit[1]));
                    }
                    if (resultSplit[1].charAt(0) == '半')
                        min = 30;
                    if ((date <= c.get(Calendar.DAY_OF_MONTH) && hour < c.get(Calendar.HOUR_OF_DAY) && hour < 12) || result.get(0).contains("下午"))
                        hour += 12;
                    String resultSplit2[];
                    if (min != 0) {
                        resultSplit2 = result.get(0).split(min + "");
                        if (resultSplit2.length == 1)
                            resultSplit2 = result.get(0).split("半");
                    }
                    else
                        resultSplit2 = result.get(0).split("點");
                    if (result.get(0).contains("鬧鐘") || result.get(0).contains("叫我")) {
                        title = null;
                        note = null;
                    }
                    else if (resultSplit2.length != 1) {
                        title = resultSplit2[1];
                        note = resultSplit2[1];
                    }
                }
                if (result.get(0).contains("分鐘")) {
                    String resultSplit[] = result.get(0).split("分鐘");
                    min += Integer.parseInt(getNumbers(resultSplit[0]));
                    if (min >= 60) {
                        hour++;
                        min -= 60;
                        if (hour >= 24) {
                            date++;
                            hour -= 24;
                        }
                    }
                    if (result.get(0).contains("鬧鐘") || result.get(0).contains("叫我")) {
                        title = null;
                        note = null;
                    }
                    else if (resultSplit.length != 1) {
                        title = resultSplit[1];
                        note = resultSplit[1];
                    }
                }
                final int fYear = year, fMonth = month, fDate = date, fHour = hour, fMin = min;
                final String fTitle = title, fNote = note;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("語音輸入結果")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int idd = helper.insertInfo(write_db, setTimeFormat(fHour, fMin), 1, setDateFormat(fYear, fMonth, fDate), fTitle, fNote, "預設", null);
                            Alarm.setAlarm(SmartSecretary.this, fYear, fMonth, fDate, fHour, fMin, idd,fTitle);
                            if (result.get(0).contains("提醒")) {
                                Toast.makeText(SmartSecretary.this, "提醒已設定 時間為" + setTimeFormat(fHour, fMin), Toast.LENGTH_SHORT).show();
                            }
                            else if (result.get(0).contains("鬧鐘") || result.get(0).contains("叫我")) {
                                Toast.makeText(SmartSecretary.this, "鬧鐘已設定 時間為" + setTimeFormat(fHour, fMin), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                String builderTitle = title, builderNote = note;
                if (builderTitle == null)
                    builderTitle = "無標題";
                if (builderNote == null)
                    builderNote = "無內容";
                //builder.setMessage(result.get(0));
                builder.setMessage("時間 : " + setDateFormat(year, month, date) + " " + setTimeFormat(hour, min) + "\n" +
                        "標題 : " + builderTitle + "\n" +
                        "內容 : " + builderNote);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCancelable(false);
            }
        }
    }

    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "0";
    }

    String setTimeFormat(int hour, int min) {
        String timeStr = hour + ":" + min;
        if (min < 10)
            timeStr = hour + ":0" + min;
        if (hour < 10)
            timeStr = "0" + timeStr;
        return timeStr;
    }

    String setDateFormat(int year, int month, int date) {
        String dateStr = year + "/" + month + "/" + date;
        if (month < 10 && date < 10)
            dateStr = year + "/0" + month + "/0" + date;
        else if (month < 10)
            dateStr = year + "/0" + month + "/" + date;
        else if (date < 10)
            dateStr = year + "/" + month + "/0" + date;
        return dateStr;
    }
}
