package com.example.community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class My_notice extends Activity {
    public String username;
    private RecyclerView recyclerView;
    public ImageView back;
    public notice_adapter notice_adapter;
    public static Handler handler=new Handler();
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notice);
        recyclerView= findViewById(R.id.recyclerView3);
        back= findViewById(R.id.back2);
        SharedPreferences sp =getSharedPreferences("state", MODE_PRIVATE);
        username=sp.getString("username","");
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        back.setClickable(true);
        back.setOnClickListener(listener);
        dialog = new ProgressDialog(My_notice.this);
        dialog.setTitle("正在获取数据");
        dialog.setMessage("请稍后");
        dialog.setCancelable(false);//设置可以通过back键取消
        dialog.show();
        new Thread(new MyThread()).start();
    }
    View.OnClickListener listener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.back2){
                finish();
            }
        }
    };
    public class MyThread implements Runnable{

        @Override
        public void run() {
            try {
                String data = "name=" + URLEncoder.encode(username, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_getnotice/?"+data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //提交数据的方式
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //设置超时时间
                connection.setConnectTimeout(8000);//连接超时
                //读取超时
                connection.setReadTimeout(8000);
                Log.i("name", username);
                connection.connect();
                InputStream inputStream=null;
                BufferedReader reader=null;
                if (connection.getResponseCode() == 200) {
                    inputStream=connection.getInputStream();
                    //转换成一个加强型的buffered流
                    reader=new BufferedReader(new InputStreamReader(inputStream));
                    //把读到的内容赋值给result
                    String result = reader.readLine();
                    JSONArray json_test = new JSONArray(result);
                    List<user_entity1> datas=new ArrayList<>();
                    //打印json 数据
                    for(int i=0; i<json_test.length();i++){
                        JSONObject jsonobject = json_test.getJSONObject(i);
                        String name =jsonobject.getString("username");
                        String info = jsonobject.getString("info");
                        String iurl = jsonobject.getString("face");
                        String collect_num =jsonobject.getString("collect_num");
                        String comment_num =jsonobject.getString("comment_num");
                        String love_num =jsonobject.getString("love_num");
                        String fan =jsonobject.getString("fan");
                        Log.i("name",name );
                        user_entity1 user=new user_entity1();
                        user.setName(name);
                        user.setIurl(iurl);
                        user.setInfo(info);
                        user.setCollect_num(Integer.parseInt(collect_num));
                        user.setComment_num(Integer.parseInt(comment_num));
                        user.setFan_num(Integer.parseInt(fan));
                        user.setNotice_num(Integer.parseInt(love_num));
                        datas.add(user);
                    }
//                    notice_adapter=new notice_adapter(My_notice.this,datas);
//                    recyclerView.setAdapter(notice_adapter);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            notice_adapter adapter=new notice_adapter(My_notice.this,datas);
                            recyclerView.setAdapter(adapter);
                            dialog.dismiss();
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