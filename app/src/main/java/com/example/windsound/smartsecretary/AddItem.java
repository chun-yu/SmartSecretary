package com.example.windsound.smartsecretary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddItem extends Activity {
    private Button back_btn,date_view,finish_add_item,btn_clock,btn_clock_view,photo_btn,voice_btn;
    private Switch alarm_switch;
    private static final String TAG = AddItem.class.getSimpleName();             //從這邊往下數五行       應該是要找路徑可是我不知道有沒有寫對
    public static final String TESS_DATA = "/tessdata";

    private Uri outputFileDir;
    private String mCurrentPhotoPath;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
    private TextInputLayout title_layout,content_layout ;
    private TextInputEditText title_text,content_text;
    private DBHelper helper = null;

    protected static final int RESULT_SPEECH = 1;
    protected static final int PHOTO = 3;
    protected static final int DO_TESS = 4;
    private String train_language = "chi_tra";
    private int putin_null_dbID;
    int int_chose_time;int int_now;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        photo_btn= (Button) findViewById(R.id.photo_btn);
        photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            //按button開啟相機
                checkPermission();
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
                    chose_now_time_compare();
                    if(int_now>=int_chose_time){
                        alarm_switch.setChecked(false);
                        alarm_switch.setText("off");
                        Toast.makeText(AddItem.this, getString(R.string.brfore_time)+"\n"+getString(R.string.reset_time), Toast.LENGTH_SHORT).show();
                    }else{
                        alarm_switch.setText("on");
                        Toast.makeText(AddItem.this, getString(R.string.open_Alaem), Toast.LENGTH_SHORT).show();
                    }
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
                showDatePickerDialog(date_view,AddItem.this);
                chose_now_time_compare();
                if(int_now>=int_chose_time){
                    alarm_switch.setChecked(false);
                    alarm_switch.setText("off");
                    Toast.makeText(AddItem.this, getString(R.string.brfore_time), Toast.LENGTH_SHORT).show();
                }else{
                    alarm_switch.setText("on");
                    Toast.makeText(AddItem.this, getString(R.string.open_Alaem), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_clock = (Button) findViewById(R.id.btn_clock);
        btn_clock_view = (Button) findViewById(R.id.btn_clock_view);
        btn_clock_view.setText(getNewTime());
        btn_clock_view.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog(btn_clock_view,AddItem.this);
                chose_now_time_compare();
                if(int_now>=int_chose_time){
                    alarm_switch.setChecked(false);
                    alarm_switch.setText("off");
                    Toast.makeText(AddItem.this, getString(R.string.brfore_time), Toast.LENGTH_SHORT).show();
                }else{
                    alarm_switch.setText("on");
                    Toast.makeText(AddItem.this, getString(R.string.open_Alaem), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_clock.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog(btn_clock_view,AddItem.this);
                chose_now_time_compare();
                if(int_now>=int_chose_time){
                    alarm_switch.setChecked(false);
                    alarm_switch.setText("off");
                    Toast.makeText(AddItem.this, getString(R.string.brfore_time), Toast.LENGTH_SHORT).show();
                }else{
                    alarm_switch.setText("on");
                    Toast.makeText(AddItem.this, getString(R.string.open_Alaem), Toast.LENGTH_SHORT).show();
                }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    content_text.setText(text.get(0));
                }
                break;
            }case DO_TESS: {
                if (resultCode == Activity.RESULT_OK) {
                    prepareTessData();
                    startOCR(outputFileDir);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
                }
                break;
            }case PHOTO: {
                if (resultCode == Activity.RESULT_OK) {
                    prepareTessData();
                    Uri uri = data.getData();
                    String[] proj = { MediaStore.Images.Media.DATA };
                    Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);
                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    actualimagecursor.moveToFirst();
                    mCurrentPhotoPath = actualimagecursor.getString(actual_image_column_index);
                    startOCR(outputFileDir);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void checkPermission() {
        String language[] = {"中文", "English"};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItem.this);
        builder.setTitle("照片選取方式");
        builder.setSingleChoiceItems(language, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //根据which决定选择了哪一个子项
                        if(which==0){
                            train_language = "chi_tra";
                        }else{
                            train_language = "eng";
                        }
                    }
                })
                .setPositiveButton("相機拍照", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dispatchTakePictureIntent();
                    }
                })
                .setNegativeButton("相簿選取", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO);
                    }
                });
        AlertDialog logout_dialog = builder.create();
        logout_dialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AddItem.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, DO_TESS);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void prepareTessData(){                                             //一樣不確定路徑有沒有抓到
        try{
            File dir = getExternalFilesDir(TESS_DATA);
            if(!dir.exists()){
                if (!dir.mkdir()) {
                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String fileList[] = getAssets().list("");
            for(String fileName : fileList){
                String pathToDataFile = dir + "/" + fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
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
    private void startOCR(Uri imageUri){//OCR       **這裡有些問題**
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
            String result = this.getText(bitmap);
            content_text.setText(result);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }//這行不確定有沒有打對
    }

    private String getText(Bitmap bitmap) {                                         //最後的部分
        final TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String dataPath = getExternalFilesDir("/").getPath() + "/";
        tessBaseAPI.init(dataPath,train_language);                                  //這行應該是抓中文辨識吧?
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        retStr = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();
        return retStr;
    }


    public static  void showDatePickerDialog(final Button button, final Context context) {
        // 設定初始日期
        Calendar c = Calendar.getInstance();
        String nowDate = button.getText().toString();
        try {
            c.setTime(sdf.parse(nowDate));
            // 跳出日期選擇器
            DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // 完成選擇，顯示日期
                    if (monthOfYear <= 8 && dayOfMonth <= 9) {
                        button.setText(year + "/0" + (monthOfYear + 1) + "/0" + dayOfMonth);
                    } else if (monthOfYear <= 8 && dayOfMonth > 9) {
                        button.setText(year + "/0" + (monthOfYear + 1) + "/" + dayOfMonth);
                    } else if (monthOfYear > 8 && dayOfMonth <= 9) {
                        button.setText(year + "/" + (monthOfYear + 1) + "/0" + dayOfMonth);
                    } else {
                        button.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                    }
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        } catch (Exception e) {
            button.setText(getToday());
        }
    }
    public static String getToday() {
        return sdf.format(Calendar.getInstance().getTime());
    }
    public static String getNewTime() {
        return sdf2.format(Calendar.getInstance().getTime());
    }
    public static void showTimePickerDialog(final Button button, final Context context) {
        Calendar c = Calendar.getInstance();
        String nowTime = button.getText().toString();
        try {
            c.setTime(sdf2.parse(nowTime));
            // 跳出日期選擇器
            new TimePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog ,new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String timeStr = hourOfDay + ":" + minute;
                    if (minute < 10)
                        timeStr = hourOfDay + ":0" + minute;
                    if (hourOfDay < 10)
                        timeStr = "0" + timeStr;
                    button.setText(timeStr);
                }
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        } catch (Exception e) {
            button.setText(getNewTime());
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

    private void check_if_null() {
        final SQLiteDatabase write_db = helper.getWritableDatabase();
        String s1 = title_text.getText().toString();
        String s2 = content_text.getText().toString();
        String[] splitarrary = new String[3];
        String[] splitarrary2 = new String[2];
        splitarrary = date_view.getText().toString().split("/");
        splitarrary2 = btn_clock_view.getText().toString().split(":");
        if (s1.equals("") && !s2.equals("")) {
            Toast.makeText(AddItem.this, getString(R.string.please_title), Toast.LENGTH_LONG).show();
        } else if (!s1.equals("") && s2.equals("")) {
            Toast.makeText(AddItem.this, getString(R.string.content), Toast.LENGTH_LONG).show();
        } else if (s1.equals("") && s2.equals("")) {
            Toast.makeText(AddItem.this, getString(R.string.please_title) + "\n" + getString(R.string.content), Toast.LENGTH_LONG).show();
        } else {
            if (s1.length() > 10 || s2.length() > 150) {
                Toast toast = Toast.makeText(AddItem.this, getString(R.string.input_outline), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                if (find_if_have()) {
                    if (alarm_switch.isChecked()) {
                        Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString() + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.open_Alaem), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        helper.updateTimeInfo(write_db, putin_null_dbID, btn_clock_view.getText().toString(), 1, date_view.getText().toString(), title_text.getText().toString(), content_text.getText().toString(), "預設", null);
                        Alarm.setAlarm(AddItem.this, Integer.parseInt(splitarrary[0]), Integer.parseInt(splitarrary[1]), Integer.parseInt(splitarrary[2]), Integer.parseInt(splitarrary2[0]), Integer.parseInt(splitarrary2[1]), putin_null_dbID);
                        finish();
                    } else {
                        Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString() + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.close_Alaem), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        helper.updateTimeInfo(write_db, putin_null_dbID, btn_clock_view.getText().toString(), 0, date_view.getText().toString(), title_text.getText().toString(), content_text.getText().toString(), "預設", null);
                        finish();
                    }
                } else {
                    if (alarm_switch.isChecked()) {
                        Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString() + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.open_Alaem), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        helper.insertInfo(write_db, btn_clock_view.getText().toString(), 1, date_view.getText().toString(), title_text.getText().toString(), content_text.getText().toString(), "預設", null);
                        Alarm.setAlarm(AddItem.this, Integer.parseInt(splitarrary[0]), Integer.parseInt(splitarrary[1]), Integer.parseInt(splitarrary[2]), Integer.parseInt(splitarrary2[0]), Integer.parseInt(splitarrary2[1]), helper.getDBcount());
                        finish();
                    } else {
                        Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString() + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.close_Alaem), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        helper.insertInfo(write_db, btn_clock_view.getText().toString(), 0, date_view.getText().toString(), title_text.getText().toString(), content_text.getText().toString(), "預設", null);
                        finish();
                    }
                }
                /*
                if (alarm_switch.isChecked()) {
                    Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString() + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.open_Alaem), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    helper.insertInfo(write_db, btn_clock_view.getText().toString(), 1, date_view.getText().toString(), title_text.getText().toString(), content_text.getText().toString(), "預設", null);
                    finish();
                } else {
                    Toast toast = Toast.makeText(AddItem.this, title_text.getText().toString() + "  : " + getString(R.string.new_success) + "\n" + getString(R.string.close_Alaem), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    helper.insertInfo(write_db, btn_clock_view.getText().toString(), 0, date_view.getText().toString(), title_text.getText().toString(), content_text.getText().toString(), "預設", null);
                    finish();
                }
                */
            }
        }
    }
    private boolean find_if_have(){
        Cursor res = helper.getInfoData();
        while (res.moveToNext()) {
            String text1 = res.getString(4);
            String text2 = res.getString(5);
            String db_time = res.getString(1);
            String clock_time = btn_clock_view.getText().toString();
            if((text1 == null || text2 == null) && clock_time.equals(db_time)){
                putin_null_dbID = res.getInt(0);
                return true;
            }
        }
        return false;
    }
    private void chose_now_time_compare(){
        String[] splitarrary = new String[3];String[] splitarrary2 = new String[2];
        splitarrary = sdf.format(Calendar.getInstance().getTime()).split("/");
        splitarrary2 = sdf2.format(Calendar.getInstance().getTime()).split(":");
        int_now= Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                +Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
        splitarrary = date_view.getText().toString().split("/");
        splitarrary2 = btn_clock_view.getText().toString().split(":");
        int_chose_time= Integer.parseInt(splitarrary[0]) * 100000000 + Integer.parseInt(splitarrary[1]) * 1000000 + Integer.parseInt(splitarrary[2]) * 10000
                +Integer.parseInt(splitarrary2[0]) * 100 + Integer.parseInt(splitarrary2[1]);
    }
}