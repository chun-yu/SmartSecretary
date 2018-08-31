package com.example.windsound.smartsecretary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddItem extends Activity {
    private Button back_btn,date_view,finish_add_item,btn_clock,btn_clock_view,photo_btn,voice_btn;
    private Switch alarm_switch;
    private static final String TAG = AddItem.class.getSimpleName();             //從這邊往下數五行       應該是要找路徑可是我不知道有沒有寫對
    public static final String TESS_DATA = "/tessdata";
    private TessBaseAPI tessbaseAPI;
    private Uri outputfileDir;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
    private TextInputLayout title_layout,content_layout ;
    private TextInputEditText title_text,content_text;
    private DBHelper helper = null;

    protected static final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        this.findViewById(R.id.photo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            //按button開啟相機
                startCameraActivity();
            }
        });
        helper = new DBHelper(this);
        voice_btn = (Button) findViewById(R.id.voice_btn);
        voice_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try { startActivityForResult(intent, RESULT_SPEECH);
                    content_text.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),"Opps! Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        content_text = (TextInputEditText) findViewById(R.id.content_text);                     //找ID
        content_text.addTextChangedListener(mTextWatcher);
        finish_add_item = (Button) findViewById(R.id.finish_add_item);
        finish_add_item.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                check_if_null();
            }
        });
    }
    private void startCameraActivity(){                                         //相機        "/imgs"是不確定的用法
        try{
            String imagePath = DATA_PATH + "/imgs";
            File dir = new File(imagePath);
            if(!dir.exists()){
                dir.mkdir();
            }
            String imageFilePath = imagePath + "/ocr.jpg";
            outputfileDir = Uri.fromFile(new File(imageFilePath));
            final Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,outputfileDir);
            if(pictureIntent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(pictureIntent,100);
            }
        }catch(Exception e){
            Log.e(TAG,e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){                 //從這邊開始
            prepareTessData();
            startOCR(outputfileDir);
        }else Toast.makeText(getApplicationContext(), "Image problem", Toast.LENGTH_SHORT).show();   //到這邊
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    content_text.setText(text.get(0));
                }
                break;
            }
        }
    }



    private void prepareTessData(){                                             //一樣不確定路徑有沒有抓到
        try{
            File dir = new File(DATA_PATH + TESS_DATA);
            if(!dir.exists()){
                dir.mkdir();
            }
            String fileList[] = getAssets().list("");
            for(String fileName : fileList){
                String pathToDataFile = DATA_PATH + TESS_DATA + "/" + fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff))>0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
                }
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    private void startOCR(Uri imageUri){                                                //OCR
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 7;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),options);
            String result = this.getText(bitmap);
            content_text.setText(result);                                               //這行不確定有沒有打對
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    private String getText(Bitmap bitmap) {                                         //最後的部分
        try{
            tessbaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        tessbaseAPI.init(DATA_PATH,"chi_tra");                                  //這行應該是抓中文辨識吧?
        tessbaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessbaseAPI.getUTF8Text();
        }catch(Exception e){
            Log.e(TAG,e.getMessage());
        }
        tessbaseAPI.end();
        return retStr;
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
    public static String getToday() {
        return sdf.format(Calendar.getInstance().getTime());
    }
    public static String getNewTime() {
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


    private void check_if_null(){
        final SQLiteDatabase write_db = helper.getWritableDatabase();
        String s1 = title_text.getText().toString();
        String s2 = content_text.getText().toString();
        if(s1.equals("") && !s2.equals("")){
            Toast.makeText(AddItem.this,getString(R.string.please_title), Toast.LENGTH_LONG).show();
        }else if(!s1.equals("") && s2.equals("")){
            Toast.makeText(AddItem.this,getString(R.string.content), Toast.LENGTH_LONG).show();
        }else if(s1.equals("") && s2.equals("")){
            Toast.makeText(AddItem.this,getString(R.string.please_title)+"\n"+getString(R.string.content), Toast.LENGTH_LONG).show();
        }else{
            if(alarm_switch.isChecked()){
                Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString()+"  : "+ getString(R.string.new_success) +"\n"+getString(R.string.open_Alaem), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                helper.insertInfo(write_db, btn_clock_view.getText().toString(), 1,date_view.getText().toString(),title_text.getText().toString(), content_text.getText().toString());
                finish();
            }else{
                Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString()+"  : "+ getString(R.string.new_success) +"\n"+getString(R.string.close_Alaem), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                helper.insertInfo(write_db, btn_clock_view.getText().toString(), 0,date_view.getText().toString(),title_text.getText().toString(), content_text.getText().toString());
                finish();
            }
        }
    }
}