package com.example.community;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class edit_info extends Activity {
    public ImageView back;
    public EditText name;
    public EditText phone;
    public EditText password;
    public EditText re_password;
    public EditText info;
    public String username;
    public String user_phone;
    public Button edit;
    String pass2;
    String re_pass;
    String phone2;
    String user_info;
    public static Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        back= findViewById(R.id.edit_back);
        name= findViewById(R.id.edit_name);
        phone= findViewById(R.id.edit_phone);
        password= findViewById(R.id.edit_password);
        re_password= findViewById(R.id.edit_re_password);
        edit= findViewById(R.id.edit_button);
        info= findViewById(R.id.edit_info);
        back.setClickable(true);
        back.setOnClickListener(listener);
        Intent it=getIntent();
        username=it.getStringExtra("name");
        user_phone=it.getStringExtra("phone");
        name.setText(username);
        phone.setText(user_phone);
        edit.setOnClickListener(listener);
    }
    View.OnClickListener listener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.edit_back){
                finish();
            }
            if(v.getId()==R.id.edit_button){
                 phone2= phone.getText().toString().trim();
                 pass2= password.getText().toString().trim();
                 re_pass= re_password.getText().toString().trim();
                 user_info= info.getText().toString().trim();
                if(pass2.length()<6){
                    Toast toast = Toast.makeText(edit_info.this,"请输入至少6位数密码", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(!pass2.equals(re_pass)){
                    Toast toast = Toast.makeText(edit_info.this,"两次输入的密码不一致", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(phone2.length()!=11){
                    Toast toast = Toast.makeText(edit_info.this,"请输入11位手机号", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else if (!isMobile(phone2)){
                    Toast toast = Toast.makeText(edit_info.this,"请输入正确的手机号", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                new Thread(new MyThread()).start();
            }
        }
    };
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
    public class MyThread implements Runnable{

        @Override
        public void run() {
            try {
                String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(pass2, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone2,"UTF-8") +
                        "&info=" + URLEncoder.encode(user_info,"UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_edit/?"+data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //提交数据的方式
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //设置超时时间
                connection.setConnectTimeout(8000);//连接超时
                //读取超时
                connection.setReadTimeout(8000);
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
                    final String[] info = {json_test.get("ok").toString()};
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(info[0] == String.valueOf(1)){
                                Intent intent=new Intent(edit_info.this, MainActivity.class);
                                startActivity(intent);
                                Toast toast = Toast.makeText(edit_info.this,"修改成功，请重新登录", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }
                            else {
                                Toast toast = Toast.makeText(edit_info.this,"修改失败", Toast.LENGTH_LONG);
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