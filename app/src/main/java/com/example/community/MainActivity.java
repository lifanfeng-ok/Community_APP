package com.example.community;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {
    public TextView view;
    public Button login;
    public Button register;
    public EditText name;
    public EditText pass;
    public ProgressDialog dialog;
    public static Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view=(TextView)findViewById(R.id.community);
        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);
        name=(EditText)findViewById(R.id.ed_name);
        pass=(EditText)findViewById(R.id.ed_pass);
        login.setOnClickListener(listener);
        register.setOnClickListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId()==R.id.login) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("正在登陆");
                dialog.setMessage("请稍后");
                dialog.setCancelable(false);//设置可以通过back键取消
                dialog.show();
                new Thread(new MyThread()).start();
            }
            if (view.getId()==R.id.register){
                To_register();
            }
        }
    };
    public class MyThread implements Runnable{

        @Override
        public void run() {
            String name2=name.getText().toString().trim();
            String pass2=pass.getText().toString().trim();
            try {
                String data = "name=" + URLEncoder.encode(name2, "UTF-8") +
                        "&password=" + URLEncoder.encode(pass2, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_login/?"+data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //提交数据的方式
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //设置超时时间
                connection.setConnectTimeout(8000);//连接超时
                //读取超时
                connection.setReadTimeout(8000);
                Log.i("name", name2);
                Log.i("password", pass2);
                connection.connect();
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
                                dialog.dismiss();
                                SharedPreferences sp = getSharedPreferences("state", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username", name2);
                                editor.apply();
                                Intent intent=new Intent(MainActivity.this, index.class);
                                startActivity(intent);
                            }
                            else {
                                dialog.dismiss();
                                Toast toast = Toast.makeText(MainActivity.this,"账号名或密码错误", Toast.LENGTH_SHORT);
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

    public void To_register(){
           Intent intent2=new Intent(MainActivity.this,register.class);
           startActivity(intent2);
    };

}