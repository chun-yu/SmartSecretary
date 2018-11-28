package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class memo extends Activity {

    private DBHelper helper = null;
    private Button back_btn2;
    private FloatingActionButton addmemo_fbtn;
    private ImageButton search_btn;
    private LinearLayout memo_show;
    private Cursor res;
    PopupWindow popupWindow;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    final Context context = this;
    private EditText search_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);
        helper = new DBHelper(this);
        back_btn2 = (Button) findViewById(R.id.back_btn2);
        back_btn2.setOnClickListener( new View.OnClickListener(){
            public void onClick (View v){
                if(back_btn2.getText().equals(getString(R.string.back)))
                    finish();
                else{
                    show_memo_to_click();
                    back_btn2.setText(getString(R.string.back));
                }
            }
        });
        memo_show = (LinearLayout) findViewById(R.id.memo_show);
        addmemo_fbtn = (FloatingActionButton) findViewById(R.id.addmemo_fbtn);
        addmemo_fbtn.setOnClickListener( new View.OnClickListener(){
            public void onClick (View v){
                show_new_memo(v);
            }
        });
        search_btn = (ImageButton) findViewById(R.id.search_btn);
        search_btn.setOnClickListener( new View.OnClickListener(){
            public void onClick (final View v){
                LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.search_title, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);
                search_title = (EditText) promptsView.findViewById(R.id.search_title);

                alertDialogBuilder.setView(promptsView);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("確定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        search_title_to_click();
                                        InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputMgr.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                                        back_btn2.setText(getString(R.string.cancel));
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
        );
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
            if(datearrary[i-1]>=count){
                big = big -1;
            }else {
                small = i-1; break;
            }
        }
        putin_array_add_show(datearrary,big,small,null);
    }
    private void show_new_memo(View v) {
        String date = AddItem.getToday();
        String time = AddItem.getNewTime();
        PopArticle(v,-1,time,0,date,null,null);
    }

    private void putin_array_add_show(int[] datearrary,int big,int small,String key_word){
        boolean putid = false;
        ArrayList<Integer> today_datearray = new ArrayList<Integer>();
        ArrayList<Integer> future_datearray = new ArrayList<Integer>();
        ArrayList<Integer> future_idarrary = new ArrayList<Integer>();
        ArrayList<Integer> today_idarrary = new ArrayList<Integer>();
        ArrayList<Integer> small_datearray = new ArrayList<Integer>();
        ArrayList<Integer> small_idarrary = new ArrayList<Integer>();
        String[] splitarrary = new String[3];   String[] splitarrary2 = new String[2];
        //找未來的與今天的分界
        String  today = AddItem.getToday();
        splitarrary = today.split("/");
        int today_year = Integer.parseInt(splitarrary[0]);int today_month = Integer.parseInt(splitarrary[1]);int today_day = Integer.parseInt(splitarrary[2]);
        int count = today_year * 10000 + today_month * 100 + today_day;int date_split = big;
        for (int i = big; i < datearrary.length;i++){
            if(datearrary[i] > count){
                date_split = i;
                break;
            }else{
                date_split = i+1;
            }
        }
        //排今天時間
        for (int i = big; i < date_split; i++) {
            int k = datearrary[i];
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                do {
                    String s2 = res.getString(3);
                    String s3 = res.getString(1);
                    splitarrary = s2.split("/");
                    splitarrary2 = s3.split(":");
                    int date = Integer.parseInt(splitarrary[0]) * 10000 + Integer.parseInt(splitarrary[1]) * 100 + Integer.parseInt(splitarrary[2]);
                    int date_time = Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                            + Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
                    String text1 = res.getString(4);
                    String text2 = res.getString(5);
                    if (date == k) {
                        putid = true;
                        for (int j = 0; j < today_datearray.size(); j++) {
                            if (today_datearray.get(j) == res.getInt(0)) {
                                putid = false;
                            }
                        }
                        if (key_word != null) {
                            if (!res.getString(4).contains(key_word)) {
                                putid = false;
                            }
                        }
                        if (putid) {
                            if (text1 != null && text2 != null) {
                                today_datearray.add(date_time);
                            }
                        }
                    }
                }while (res.moveToNext());
            }res.close();
        }
        Collections.sort(today_datearray);
        for (int i = 1; i <= today_datearray.size(); i++) {
            int k = today_datearray.get(i-1);
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                do {
                    String s2 = res.getString(3);
                    String s3 = res.getString(1);
                    splitarrary = s2.split("/");
                    splitarrary2 = s3.split(":");
                    int date_time = Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                            + Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
                    if (date_time == k) {
                        putid = true;
                        for (int j = 0; j < today_idarrary.size(); j++) {
                            if (today_idarrary.get(j) == res.getInt(0)) {
                                putid = false;
                            }
                        }
                        if (key_word != null) {
                            if (!res.getString(4).contains(key_word)) {
                                putid = false;
                            }
                        }
                        if (putid) {
                            today_idarrary.add(res.getInt(0));
                        }
                    }
                }while (res.moveToNext());
            }res.close();
        }
        //排未來時間
        for (int i = date_split; i < datearrary.length; i++) {
            int k = datearrary[i];
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                do {
                    String s2 = res.getString(3);
                    String s3 = res.getString(1);
                    splitarrary = s2.split("/");
                    splitarrary2 = s3.split(":");
                    int date = Integer.parseInt(splitarrary[0]) * 10000 + Integer.parseInt(splitarrary[1]) * 100 + Integer.parseInt(splitarrary[2]);
                    int date_time = Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                            + Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
                    String text1 = res.getString(4);
                    String text2 = res.getString(5);
                    if (date == k) {
                        putid = true;
                        for (int j = 0; j < future_datearray.size(); j++) {
                            if (future_datearray.get(j) == res.getInt(0)) {
                                putid = false;
                            }
                        }
                        if (key_word != null) {
                            if (!res.getString(4).contains(key_word)) {
                                putid = false;
                            }
                        }
                        if (putid) {
                            if (text1 != null && text2 != null) {
                                future_datearray.add(date_time);
                            }
                        }
                    }
                }while (res.moveToNext());
            }res.close();
        }
        Collections.sort(future_datearray);
        for (int i = 1; i <= future_datearray.size(); i++) {
            int k = future_datearray.get(i-1);
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                do {
                    String s2 = res.getString(3);
                    String s3 = res.getString(1);
                    splitarrary = s2.split("/");
                    splitarrary2 = s3.split(":");
                    int date_time = Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                            + Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
                    if (date_time == k) {
                        putid = true;
                        for (int j = 0; j < future_idarrary.size(); j++) {
                            if (future_idarrary.get(j) == res.getInt(0)) {
                                putid = false;
                            }
                        }
                        if (key_word != null) {
                            if (!res.getString(4).contains(key_word)) {
                                putid = false;
                            }
                        }
                        if (putid) {
                            future_idarrary.add(res.getInt(0));
                        }
                    }
                }while (res.moveToNext());
            }res.close();
        }
        //排昨天以前
        for (int i = 0; i <= small; i++) {
            int k = datearrary[i];
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                do {
                    String s2 = res.getString(3);
                    String s3 = res.getString(1);
                    splitarrary = s2.split("/");
                    splitarrary2 = s3.split(":");
                    int date = Integer.parseInt(splitarrary[0]) * 10000 + Integer.parseInt(splitarrary[1]) * 100 + Integer.parseInt(splitarrary[2]);
                    int date_time = Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                            + Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
                    String text1 = res.getString(4);
                    String text2 = res.getString(5);
                    if (date == k) {
                        putid = true;
                        for (int j = 0; j < small_datearray.size(); j++) {
                            if (small_datearray.get(j) == res.getInt(0)) {
                                putid = false;
                            }
                        }
                        if (key_word != null) {
                            if (!res.getString(4).contains(key_word)) {
                                putid = false;
                            }
                        }
                        if (putid) {
                            if (text1 != null && text2 != null) {
                                small_datearray.add(date_time);
                            }
                        }
                    }
                }while (res.moveToNext());
            }res.close();
        }
        Collections.sort(small_datearray);
        for (int i = small_datearray.size(); i > 0; i--) {
            int k = small_datearray.get(i-1);
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                do {
                    String s2 = res.getString(3);
                    String s3 = res.getString(1);
                    splitarrary = s2.split("/");
                    splitarrary2 = s3.split(":");
                    int date_time = Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                            + Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
                    if (date_time == k) {
                        putid = true;
                        for (int j = 0; j < small_idarrary.size(); j++) {
                            if (small_idarrary.get(j) == res.getInt(0)) {
                                putid = false;
                            }
                        }
                        if (key_word != null) {
                            if (!res.getString(4).contains(key_word)) {
                                putid = false;
                            }
                        }
                        if (putid) {
                            small_idarrary.add(res.getInt(0));
                        }
                    }
                }while (res.moveToNext());
            }res.close();
        }
        for (int i = 0; i < today_idarrary.size(); i++) {
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                if (i == 0) {
                    LinearLayout before_text = new LinearLayout(this);
                    before_text.setBackgroundColor(getResources().getColor(R.color.holo_blue_dark));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
                    params.setMargins(30, 20, 30, 0);
                    before_text.setLayoutParams(params);
                    TextView tv = new TextView(this);
                    tv.setTextSize(16);
                    tv.setText("今日");
                    tv.setTextColor(getResources().getColor(R.color.gray));
                    tv.setPadding(25, 0, 0, 0);
                    before_text.addView(tv);
                    memo_show.addView(before_text);
                }
                do{
                    if (today_idarrary.get(i) == res.getInt(0)) {
                        String am_pm = res.getString(1);
                        splitarrary2 = am_pm.split(":");
                        if((Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1])) >= 1200)
                            add_table_show(res.getInt(0), getResources().getColor(R.color.little_holo_blue_dark), " 下 午 ", res.getString(4), res.getString(1), res.getInt(2), res.getString(5));
                        else
                            add_table_show(res.getInt(0), getResources().getColor(R.color.little_holo_blue_dark), " 上 午 ", res.getString(4), res.getString(1), res.getInt(2), res.getString(5));
                    }
                }while (res.moveToNext());
            }res.close();
        }
        for (int i = 0; i < future_idarrary.size(); i++) {
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                if (i == 0) {
                    LinearLayout before_text = new LinearLayout(this);
                    before_text.setBackgroundColor(getResources().getColor(R.color.orange));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
                    params.setMargins(30, 20, 30, 0);
                    before_text.setLayoutParams(params);
                    TextView tv = new TextView(this);
                    tv.setTextSize(16);
                    tv.setText("未來");
                    tv.setTextColor(getResources().getColor(R.color.gray));
                    tv.setPadding(25, 0, 0, 0);
                    before_text.addView(tv);
                    memo_show.addView(before_text);
                }
                do{
                    if (future_idarrary.get(i) == res.getInt(0)) {
                        add_table_show(res.getInt(0), getResources().getColor(R.color.yellow), res.getString(3), res.getString(4), res.getString(1), res.getInt(2), res.getString(5));
                    }
                }while (res.moveToNext());
            }res.close();
        }
        for (int i = 0; i < small_idarrary.size(); i++) {
            res = helper.getInfoData();
            res.moveToFirst();
            if (res.getCount()>0) {
                if (i == 0) {
                    LinearLayout before_text = new LinearLayout(this);
                    before_text.setBackgroundColor(getResources().getColor(R.color.dark_trans_red));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
                    params.setMargins(30, 20, 30, 0);
                    before_text.setLayoutParams(params);
                    TextView tv = new TextView(this);
                    tv.setTextSize(16);
                    tv.setText("昨日以前");
                    tv.setTextColor(getResources().getColor(R.color.gray));
                    tv.setPadding(25, 0, 0, 0);
                    before_text.addView(tv);
                    memo_show.addView(before_text);
                }
                do{
                    if (small_idarrary.get(i) == res.getInt(0)) {
                        add_table_show(res.getInt(0), getResources().getColor(R.color.trans_red), res.getString(3), res.getString(4), res.getString(1), res.getInt(2), res.getString(5));
                    }
                }while (res.moveToNext());
            }res.close();
        }
    }

    private void add_table_show(int _article_id, int color,String _date, String _title, String _time,int _check,String _note) {
        final int article_id = _article_id;
        final String date = _date;
        final String title = _title;
        final String note = _note;
        final String time = _time;
        final int check = _check;
        final LinearLayout tr = new LinearLayout(this);
        tr.setClickable(true);
        tr.setOrientation(LinearLayout.VERTICAL);
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(memo.this, title + "     時間 :" + time , Toast.LENGTH_SHORT).show();
                PopArticle(view,article_id,time,check,date,title,note);
            }
        });
        tr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try
                {
                    showAlertDialog(article_id,title,date,time);
                }catch(Exception e)
                {
                    Log.d("Long click", "error");
                    return false;
                }
                return false;
            }
        });
        tr.setBackgroundColor(color);
        LinearLayout.LayoutParams  params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,240);
        params.setMargins(30,20,30,15);
        tr.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 68);
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
        if(date.equals(" 下 午 ") || date.equals(" 上 午 ")) //今天的話做日期判斷
            tv2.setText(getDay_of_week(AddItem.getToday()));
        else
            tv2.setText(getDay_of_week(date));
        tv2.setTextColor(getResources().getColor(R.color.holo_blue_dark));
        tv2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 11));
        TextView tv3 = new TextView(this);
        tv3.setTextSize(20);
        tv3.setText(title);
        tv3.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        tv3.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 3));
        TextView tv4 = new TextView(this);
        tv4.setTextSize(16);
        if(check==0)tv4.setText("提醒  未開啟");
        else    tv4.setText("提醒  已開啟");
        if(color==getResources().getColor(R.color.trans_red))   tv4.setText("已過期");
        tv4.setGravity(Gravity.RIGHT);
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
        space.setBackgroundColor(getResources().getColor(R.color.light_gray));
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

    private  void PopArticle(View v,final int _article_id,String _time,int _check,String _date,String _title,String _note){
        final String date = _date;
        final String time = _time;
        final String title = _title;
        final String note = _note;
        final int check = _check;
            View popWindow_view = getLayoutInflater().inflate(R.layout.article_display,null);
            LinearLayout text_linear_article = (LinearLayout) popWindow_view.findViewById(R.id.text_linear_article);
            final Button article_date = (Button) popWindow_view.findViewById(R.id.article_date);
            final Button article_time = (Button) popWindow_view.findViewById(R.id.article_time);
            final Button article_close = (Button) popWindow_view.findViewById(R.id.article_close);
            final TextInputEditText title_text_article = (TextInputEditText) popWindow_view.findViewById(R.id.title_text_article);
            final TextInputEditText content_text_article = (TextInputEditText) popWindow_view.findViewById(R.id.content_text_article);

            FrameLayout.LayoutParams para = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.FILL_PARENT);
            popWindow_view.setLayoutParams(para);
            text_linear_article.setPadding(20,30,20,20);
            popupWindow = new PopupWindow(popWindow_view,1000,1500, true);
            popupWindow.setAnimationStyle(R.style.AnimationFade);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            popupWindow.showAtLocation(v, Gravity.CENTER,0,0);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
            article_date.setText(date);
            article_time.setText(time);
            title_text_article.setText(title);
            content_text_article.setText(note);
            article_date.setOnClickListener( new View.OnClickListener(){
                public void onClick (View v){
                    AddItem.showDatePickerDialog(article_date,v.getContext());
                }
            });
            article_time.setOnClickListener( new View.OnClickListener(){
                public void onClick (View v){
                    AddItem.showTimePickerDialog(article_time,v.getContext());
                }
            });
            article_close.setOnClickListener( new View.OnClickListener(){
                public void onClick (View v){
                    String s1 = title_text_article.getText().toString();
                    String s2 = content_text_article.getText().toString();
                    String time2 = article_time.getText().toString();
                    String date2 = article_date.getText().toString();
                    check_update_correct(_article_id,s1,s2,check,time2,date2);
                }
            });
    }

    private void check_update_correct(int id,String s1,String s2,int check,String time,String date){
        final SQLiteDatabase write_db = helper.getWritableDatabase();
        if(s1.equals("") && !s2.equals("")){
            Toast.makeText(memo.this,getString(R.string.please_title), Toast.LENGTH_LONG).show();
        }else if(!s1.equals("") && s2.equals("")){
            Toast.makeText(memo.this,getString(R.string.content), Toast.LENGTH_LONG).show();
        }else if(s1.equals("") && s2.equals("")){
            Toast.makeText(memo.this,getString(R.string.please_title)+"\n"+getString(R.string.content), Toast.LENGTH_LONG).show();
        }else{
            if(s1.length()>10 || s2.length()>150){
                Toast.makeText(memo.this,getString(R.string.input_outline), Toast.LENGTH_LONG).show();
            }else {
                if (check > 0) {
                    Toast toast = Toast.makeText(memo.this, s1 + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.open_Alaem), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    if (id<0){
                        helper.insertInfo(write_db,time,0,date,s1,s2,"預設",null);
                    }else{
                        helper.updateTimeInfo(write_db, id, time, 1, date, s1, s2, "預設", null);
                    }
                    popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    popupWindow.dismiss();
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                    show_memo_to_click();
                } else {
                    Toast toast = Toast.makeText(memo.this, s1 + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.close_Alaem), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    if (id<0){
                        helper.insertInfo(write_db,time,0,date,s1,s2,"預設",null);
                    }else{
                        helper.updateTimeInfo(write_db, id, time, 0, date, s1, s2, "預設", null);
                    }
                    popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    popupWindow.dismiss();
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                    show_memo_to_click();
                }
            }
        }
    }

    void showAlertDialog(final int id,String title,String date,String time) {
        new AlertDialog.Builder(memo.this)
                .setTitle(getString(R.string.sure_delete) + "       " + title)
                .setMessage("\n"+getString(R.string.time) +" : " + time +"\n\n"
                        + getString(R.string.date) + " : " + date + "           " + getDay_of_week(date))
                .setPositiveButton(getString(R.string.determine), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.remove_Note(id);
                        show_memo_to_click();
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

    private void search_title_to_click(){
        for (int i = memo_show.getChildCount(); i >= 0; i--) {
            memo_show.removeView(memo_show.getChildAt(i));
        }
        String s1 = search_title.getText().toString();
        Cursor cursor = helper.getInfoData();
        int searchid[] = new int[100];
        String[] splitarrary = new String[3];
        int n = 0;
        if (cursor.getCount()>0){    // 若有資料
            cursor.moveToFirst();    // 移到第 1 筆資料
            do{        // 逐筆讀出資料
                String title = cursor.getString(4);
                if (title!=null){
                    if (title.contains(s1)){
                        String s2 = cursor.getString(3);
                        splitarrary = s2.split("/");
                        int year = Integer.parseInt(splitarrary[0]);
                        int month = Integer.parseInt(splitarrary[1]);
                        int day = Integer.parseInt(splitarrary[2]);
                        searchid[n] = year * 10000 + month * 100 + day;
                        n++;
                    }
                }

            } while(cursor.moveToNext());    // 有一下筆就繼續迴圈

        }
        Arrays.sort(searchid);
        String  today = AddItem.getToday();
        splitarrary = today.split("/");
        int today_year = Integer.parseInt(splitarrary[0]);
        int today_month = Integer.parseInt(splitarrary[1]);
        int today_day = Integer.parseInt(splitarrary[2]);
        n = today_year * 10000 + today_month * 100 + today_day;
        int big = searchid.length;
        int small = 0;
        for (int i = searchid.length; i > 0;i--){
            if(searchid[i-1]>=n){
                big = big -1;
            }else {
                small = i-1; break;
            }
        }
        putin_array_add_show(searchid, big, small,s1);
    }


}
