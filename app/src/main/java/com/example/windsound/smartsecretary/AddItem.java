package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddItem extends Activity {
    private Button back_btn,date_view,finish_add_item,btn_clock,btn_clock_view,phoho_btn,voice_btn;
    private Switch alarm_switch;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
    private TextInputLayout title_layout,content_layout ;
    private TextInputEditText title_text,content_text;
    private DBHelper helper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        helper = new DBHelper(this);
        final SQLiteDatabase write_db = helper.getWritableDatabase();

        back_btn = (Button) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        alarm_switch = (Switch) findViewById(R.id.alarm_switch);
        alarm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    alarm_switch.setText("on");
                    Toast.makeText(AddItem.this, getString(R.string.open_Alaem), Toast.LENGTH_SHORT).show();
                } else {
                    alarm_switch.setText("off");
                    Toast.makeText(AddItem.this, getString(R.string.close_Alaem), Toast.LENGTH_SHORT).show();
                }
            }
        });
        date_view = (Button) findViewById(R.id.date_view);
        date_view.setText(getToday());
        date_view.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btn_clock = (Button) findViewById(R.id.btn_clock);
        btn_clock_view = (Button) findViewById(R.id.btn_clock_view);
        btn_clock_view.setText(getNewTime());
        btn_clock_view.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        btn_clock.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        title_layout= (TextInputLayout) findViewById(R.id.title_layout);
        title_text = (TextInputEditText) findViewById(R.id.title_text);
        title_text.addTextChangedListener(mTextWatcher);
        content_layout= (TextInputLayout) findViewById(R.id.content_layout);
        content_text = (TextInputEditText) findViewById(R.id.content_text);
        content_text.addTextChangedListener(mTextWatcher);
        finish_add_item = (Button) findViewById(R.id.finish_add_item);
        finish_add_item.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(alarm_switch.isChecked()){
                    helper.insertInfo(write_db, btn_clock_view.getText().toString(), 1,title_text.getText().toString(), content_text.getText().toString());
                }else{
                    helper.insertInfo(write_db, btn_clock_view.getText().toString(), 0,title_text.getText().toString(), content_text.getText().toString());
                }
            }
        });
    }

    public void showDatePickerDialog() {
        // 設定初始日期
        Calendar c = Calendar.getInstance();
        String nowDate = date_view.getText().toString();
        try {
            c.setTime(sdf.parse(nowDate));
            // 跳出日期選擇器
            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // 完成選擇，顯示日期
                    if (monthOfYear <= 8 && dayOfMonth <= 9) {
                        date_view.setText(year + "/0" + (monthOfYear + 1) + "/0" + dayOfMonth);
                    } else if (monthOfYear <= 8 && dayOfMonth > 9) {
                        date_view.setText(year + "/0" + (monthOfYear + 1) + "/" + dayOfMonth);
                    } else if (monthOfYear > 8 && dayOfMonth <= 9) {
                        date_view.setText(year + "/" + (monthOfYear + 1) + "/0" + dayOfMonth);
                    } else {
                        date_view.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                    }
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        } catch (Exception e) {
            date_view.setText(getToday());
        }
    }
    private String getToday() {
        return sdf.format(Calendar.getInstance().getTime());
    }
    private String getNewTime() {
        return sdf2.format(Calendar.getInstance().getTime());
    }
    public void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        String nowTime = btn_clock_view.getText().toString();
        try {
            c.setTime(sdf2.parse(nowTime));
            // 跳出日期選擇器
            new TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog ,new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String timeStr = "";
                    if (minute < 10){
                        timeStr = hourOfDay + ":0" + minute;
                        btn_clock_view.setText(timeStr);}
                    else{
                        timeStr = hourOfDay + ":" + minute;
                        btn_clock_view.setText(timeStr);}
                }
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        } catch (Exception e) {
            btn_clock_view.setText(getNewTime());
        }
    }
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (title_layout.getEditText().getText().length() > title_layout.getCounterMaxLength())
                title_layout.setError("輸入內容超過上限");//show
            else
                title_layout.setError(null);//hide
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}