package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class memo extends Activity {

    private DBHelper helper = null;
    private Button back_btn2,addmemo_fbtn;
    private ImageButton search_btn;
    private LinearLayout memo_show;
    private Cursor res;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);
        helper = new DBHelper(this);
        back_btn2 = (Button) findViewById(R.id.back_btn2);
        back_btn2.setOnClickListener( new View.OnClickListener(){
            public void onClick (View v){
                finish();
            }
        });
        memo_show = (LinearLayout) findViewById(R.id.memo_show);
        show_memo_to_click();
    }

    private void show_memo_to_click() {
        res = helper.getInfoData();
        int[] datearrary = new int[100];
        int count = 0;
        String[] splitarrary = new String[3];
        for (int i = memo_show.getChildCount(); i >= 0; i--) {
            memo_show.removeView(memo_show.getChildAt(i));
        }
        while (res.moveToNext()) {
            String s2 = res.getString(3);
            splitarrary = s2.split("/");
            int year = Integer.parseInt(splitarrary[0]);
            int month = Integer.parseInt(splitarrary[1]);
            int day = Integer.parseInt(splitarrary[2]);
            datearrary[count] = year * 10000 + month * 100 + day;
            count++;
        }
        Arrays.sort(datearrary);
        String  today = AddItem.getToday();
        splitarrary = today.split("/");
        int today_year = Integer.parseInt(splitarrary[0]);
        int today_month = Integer.parseInt(splitarrary[1]);
        int today_day = Integer.parseInt(splitarrary[2]);
        count = today_year * 10000 + today_month * 100 + today_day;
        int big = datearrary.length;
        int small = 0;
        for (int i = datearrary.length; i > 0;i--){
            if(datearrary[i-1]==count){
                big = big -1;
            }else if(datearrary[i-1]<count){
                small=i-1;break;
            }else{
                big = big -1;
            }
        }
        putin_array_add_show(datearrary,big,small);
    }
    private void putin_array_add_show(int[] datearrary,int big,int small){
        boolean putid = false;
        ArrayList<Integer> big_idarrary = new ArrayList<Integer>();
        ArrayList<Integer> small_idarrary = new ArrayList<Integer>();
        String[] splitarrary = new String[3];
        for (int i = big; i < datearrary.length; i++) {
            int k = datearrary[i];
            res = helper.getInfoData();
            while (res.moveToNext()) {
                String s2 = res.getString(3);
                splitarrary = s2.split("/");
                int year = Integer.parseInt(splitarrary[0]);
                int month = Integer.parseInt(splitarrary[1]);
                int day = Integer.parseInt(splitarrary[2]);
                int date = year * 10000 + month * 100 + day;
                String text1 = res.getString(4);
                String text2 = res.getString(5);
                if (date == k) {
                    putid = true;
                    for (int j = 0; j < big_idarrary.size(); j++) {
                        if (big_idarrary.get(j) == res.getInt(0)) {
                            putid = false;
                        }
                    }
                    if (putid) {
                        if(text1 != null && text2 != null){
                            big_idarrary.add(res.getInt(0));
                        }
                    }
                }
            }
        }
        for (int i = 0; i <= small; i++) {
            int k = datearrary[i];
            res = helper.getInfoData();
            while (res.moveToNext()) {
                String s2 = res.getString(3);
                splitarrary = s2.split("/");
                int year = Integer.parseInt(splitarrary[0]);
                int month = Integer.parseInt(splitarrary[1]);
                int day = Integer.parseInt(splitarrary[2]);
                int date = year * 10000 + month * 100 + day;
                String text1 = res.getString(4);
                String text2 = res.getString(5);
                if (date == k) {
                    putid = true;
                    for (int j = 0; j < small_idarrary.size(); j++) {
                        if (small_idarrary.get(j) == res.getInt(0)) {
                            putid = false;
                        }
                    }
                    if (putid) {
                        if(text1 != null && text2 != null){
                            small_idarrary.add(res.getInt(0));
                        }
                    }
                }
            }
        }
        for (int i = 0; i < big_idarrary.size(); i++) {
            res = helper.getInfoData();
            while (res.moveToNext()) {
                if (big_idarrary.get(i) == res.getInt(0)) {
                    add_table_show(getResources().getColor(R.color.little_holo_blue_dark),res.getString(3), res.getString(4), res.getString(1),res.getInt(2));
                }
            }
        }
        for (int i = 0; i < small_idarrary.size(); i++) {
            res = helper.getInfoData();
            while (res.moveToNext()) {
                if (small_idarrary.get(i) == res.getInt(0)) {
                    add_table_show(getResources().getColor(R.color.trans_red),res.getString(3), res.getString(4), res.getString(1),res.getInt(2));
                }
            }
        }
    }
    private void add_table_show(int color,String _datee, String _title,String _time,  int _check) {
        final String date = _datee;
        final String title = _title;
        final String time = _time;
        final int check = _check;
        final LinearLayout tr = new LinearLayout(this);
        tr.setClickable(true);
        tr.setOrientation(LinearLayout.VERTICAL);
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(memo.this, title + "     時間 :" + time , Toast.LENGTH_SHORT).show();
            }
        });

        tr.setBackgroundColor(color);
        LinearLayout.LayoutParams  params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,240);
        params.setMargins(30,20,30,15);
        tr.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 65);
        params.setMargins(25,20,25,15);
        LinearLayout ttr1 = new LinearLayout(this);
        ttr1.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        params.setMargins(25,20,25,5);
        LinearLayout ttr2 = new LinearLayout(this);
        ttr2.setLayoutParams(params);
        /* Create a Button to be the row-content. */
        TextView tv0 = new TextView(this);
        tv0.setTextSize(20);
        tv0.setText(date);
        tv0.setTextColor(getResources().getColor(R.color.holo_blue_dark));
        tv0.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 8));
        TextView tv1 = new TextView(this);
        tv1.setTextSize(20);
        tv1.setText(time);
        tv1.setTextColor(getResources().getColor(R.color.holo_blue_dark));
        tv1.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 9));
        TextView tv2 = new TextView(this);
        tv2.setTextSize(20);
        tv2.setText(getDay_of_week(date));
        tv2.setTextColor(getResources().getColor(R.color.holo_blue_dark));
        tv2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 11));
        TextView tv3 = new TextView(this);
        tv3.setTextSize(25);
        tv3.setText(title);
        tv3.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        tv3.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 3));
        TextView tv4 = new TextView(this);
        tv4.setTextSize(20);
        if(check==0)tv4.setText("提醒  未開啟");
        else    tv4.setText("提醒  已開啟");
        tv4.setTextColor(getResources().getColor(R.color.colorAccent));
        tv4.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 6));
        /* Add Button to row. */
        ttr1.addView(tv0);
        ttr1.addView(tv1);
        ttr1.addView(tv2);
        ttr2.addView(tv3);
        ttr2.addView(tv4);
        tr.addView(ttr1);
        tr.addView(ttr2);

        final LinearLayout space = new LinearLayout(this);
        space.setBackgroundColor(getResources().getColor(R.color.gray));
        LinearLayout.LayoutParams  params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,10);
        params2.setMargins(30,0,30,0);
        space.setLayoutParams(params2);

        /* Add row to TableLayout. */
        memo_show.addView(tr);
        memo_show.addView(space);
    }
    private String getDay_of_week(String date){
        Calendar c = Calendar.getInstance();
        int t;
        try {
            c.setTime(sdf.parse(date));
            t = c.get(Calendar.DAY_OF_WEEK);
        } catch (Exception e) {
            t = 8;
        }
        switch (t){
            case 1:
                return getString(R.string.sunday);
            case 2:
                return getString(R.string.monday);
            case 3:
                return getString(R.string.tuesday);
            case 4:
                return getString(R.string.wednesday);
            case 5:
                return getString(R.string.thursday);
            case 6:
                return getString(R.string.friday);
            case 7:
                return getString(R.string.saturday);
            case 8:
                return getString(R.string.unknow);
            default:
                return getString(R.string.unknow);
        }
    }
}
