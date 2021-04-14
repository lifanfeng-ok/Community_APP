package com.example.community;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class do_regular extends Activity {
    private Spinner myspinner;
    public String select_item;
    public ImageView back;
    public RadioGroup type;
    public EditText content;
    public Button clock;
    public boolean ischecked;
    public String clock_type;
    public String clock_content;
    public static Handler handler=new Handler();
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_regular);
        SharedPreferences sp =getSharedPreferences("state", MODE_PRIVATE);
        username=sp.getString("username","");
        myspinner=(Spinner)findViewById(R.id.spinner);
        clock=(Button)findViewById(R.id.clock_login);
        back=(ImageView) findViewById(R.id.back666);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.back666){
                    finish();
                }
            }
        });
        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_item=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        type=(RadioGroup)findViewById(R.id.radioGroup);
        content=(EditText)findViewById(R.id.content);
        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ischecked=true;
            }
        } );
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.clock_login){
                    if(select_item!=null) {
                if(ischecked){
                     int i=type.getCheckedRadioButtonId();
                     RadioButton radio22=(RadioButton)findViewById(i);
                     clock_type=radio22.getText().toString().trim();
                     clock_content=content.getText().toString().trim();
                     new Thread(new MyThread()).start();
                 }else {
              Toast toast=Toast.makeText(do_regular.this, "请选择打卡运动类型", Toast.LENGTH_SHORT);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
                  }
                 }else {
                Toast toast=Toast.makeText(do_regular.this, "请选择本次运动时间", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                }
                }
            }
        });
    }
    public  class MyThread implements Runnable{
        @Override
        public void run() {
            try {
                String data = "name=" + URLEncoder.encode(username, "UTF-8") +
                        "&type=" + URLEncoder.encode(clock_type, "UTF-8") +
                        "&time=" + URLEncoder.encode(select_item,"UTF-8") +
                        "&content=" + URLEncoder.encode(clock_content,"UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_addclock/?"+data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //提交数据的方式
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //设置超时时间
                connection.setConnectTimeout(8000);//连接超时
                //读取超时
                connection.setReadTimeout(8000);
                Log.i("type",clock_type);
                InputStream inputStream=null;
                BufferedReader reader=null;
                if (connection.getResponseCode() == 200) {
                    inputStream=connection.getInputStream();
                    //转换成一个加强型的buffered流
                    reader=new BufferedReader(new InputStreamReader(inputStream));
                    //把读到的内容赋值给result
                    String result = reader.readLine();
                    JSONObject json_test = new JSONObject(result);
                    //打印json 数据
                    Log.e("json", json_test.get("ok").toString());
                    final String[] info = {json_test.get("ok").toString()};
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(info[0] == String.valueOf(1)){
                                Toast toast = Toast.makeText(do_regular.this,"打卡成功", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                finish();
                            }
                            else {
                                Toast toast = Toast.makeText(do_regular.this,"打卡失败", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    });
                    reader.close();
                    inputStream.close();
                    connection.disconnect();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}