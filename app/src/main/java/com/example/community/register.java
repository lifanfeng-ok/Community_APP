package com.example.community;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class register extends Activity {
    public EditText name;
    public EditText password;
    public EditText re_pass;
    public EditText phone;
    public RadioGroup sex;
    public RadioButton radio1,radio2;
    public Button login,register;
    public ProgressDialog dialog;
    public boolean ischecked;
    String name2;
    String pass2;
    String re_password;
    String phone2;
    String user_sex;
    public static Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=(EditText)findViewById(R.id.input_identity_text);
        password=(EditText)findViewById(R.id.input_password_text);
        re_pass=(EditText)findViewById(R.id.re_password_text);
        phone=(EditText)findViewById(R.id.phone);
        sex=(RadioGroup)findViewById(R.id.radioGroup);
        login=(Button)findViewById(R.id.login2);
        register=(Button)findViewById(R.id.register2);
        register.setOnClickListener(listener);
        login.setOnClickListener(listener);
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                  ischecked=true;
            }
        } );
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId()==R.id.register2) {
                dialog = new ProgressDialog(register.this);
                dialog.setTitle("????????????");
                dialog.setMessage("?????????");
                dialog.setCancelable(false);//??????????????????back?????????
                dialog.show();
                name2=name.getText().toString().trim();
                pass2=password.getText().toString().trim();
                re_password=re_pass.getText().toString().trim();
                if(ischecked){
                int i=sex.getCheckedRadioButtonId();
                RadioButton radio22=(RadioButton)findViewById(i);
                user_sex=radio22.getText().toString().trim();
                phone2=phone.getText().toString().trim();
                Log.i("name",name2);
                if(pass2.length()<6){
                        dialog.dismiss();
//                        password.setError("??????6????????????");
                        Toast toast = Toast.makeText(register.this,"???????????????6????????????", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                if(!pass2.equals(re_password)){
                    dialog.dismiss();
//                    re_pass.setError("?????????????????????");
                    Toast toast = Toast.makeText(register.this,"??????????????????????????????", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                    if(phone2.length()!=11){
                        dialog.dismiss();
//                        phone.setError("?????????11????????????");
                        Toast toast = Toast.makeText(register.this,"?????????11???????????????", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    else if (!isMobile(phone2)){
                        dialog.dismiss();
//                        phone.setError("???????????????????????????");
                        Toast toast = Toast.makeText(register.this,"???????????????????????????", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                new Thread(new MyThread()).start();
            }
                else {
                    dialog.dismiss();
                    Toast toast=Toast.makeText(register.this, "???????????????", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            if (view.getId()==R.id.login2){
                To_login();
            }
        }
    };

    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][0-9]{10}$"); // ???????????????
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    public  class MyThread implements Runnable{

        @Override
        public void run() {
          try {
              String data = "name=" + URLEncoder.encode(name2, "UTF-8") +
                      "&password=" + URLEncoder.encode(pass2, "UTF-8") +
                      "&phone=" + URLEncoder.encode(phone2,"UTF-8") +
                      "&sex=" + URLEncoder.encode(user_sex,"UTF-8");
              URL url = new URL("http://172.22.93.185:5000/android_register/?"+data);
              HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              //?????????????????????
              connection.setRequestMethod("POST");
              connection.setDoOutput(true);
              //??????????????????
              connection.setConnectTimeout(8000);//????????????
              //????????????
              connection.setReadTimeout(8000);
              Log.i("name",name2);
              InputStream inputStream=null;
              BufferedReader reader=null;
              if (connection.getResponseCode() == 200) {
                  inputStream=connection.getInputStream();
                  //???????????????????????????buffered???
                  reader=new BufferedReader(new InputStreamReader(inputStream));
                  //???????????????????????????result
                  String result = reader.readLine();
                  JSONObject json_test = new JSONObject(result);
                  //??????json ??????
                  Log.e("json", json_test.get("ok").toString());
                  Log.e("json", json_test.get("data").toString());
                  final String[] info = {json_test.get("ok").toString()};
                  handler.post(new Runnable() {
                      @Override
                      public void run() {
                          if(info[0] == String.valueOf(1)){
                              dialog.dismiss();
                              Intent intent=new Intent(register.this, MainActivity.class);
                              startActivity(intent);
                              Toast toast = Toast.makeText(register.this,"????????????", Toast.LENGTH_LONG);
                              toast.setGravity(Gravity.CENTER, 0, 0);
                              toast.show();

                          }
                          else {
                              dialog.dismiss();
                              Toast toast = Toast.makeText(register.this,"??????????????????", Toast.LENGTH_LONG);
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

    public void To_login(){
        Intent intent2=new Intent(register.this,MainActivity.class);
        startActivity(intent2);
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}