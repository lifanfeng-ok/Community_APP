package com.example.community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

public class My_collect extends Activity {
    public String username;
    private RecyclerView recyclerView;
    public ImageView back;
    public video_col_adapter adapter;
    public static Handler handler=new Handler();
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        back= findViewById(R.id.back);
        recyclerView= findViewById(R.id.recyclerView2);
        SharedPreferences sp =getSharedPreferences("state", MODE_PRIVATE);
        username=sp.getString("username","");
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        back.setClickable(true);
        back.setOnClickListener(listener);
//        adapter=new video_col_adapter(this);
//        recyclerView.setAdapter(adapter);
        dialog = new ProgressDialog(My_collect.this);
        dialog.setTitle("正在获取数据");
        dialog.setMessage("请稍后");
        dialog.setCancelable(false);//设置可以通过back键取消
        dialog.show();
        new Thread(new MyThread()).start();
    }
    View.OnClickListener listener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.back){
                finish();
            }
        }
    };
    public class MyThread implements Runnable{

        @Override
        public void run() {
            try {
                String data = "name=" + URLEncoder.encode(username, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_getcol/?"+data);
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
                    List<video_entity1> datas=new ArrayList<>();
                    //打印json 数据
                    for(int i=0; i<json_test.length();i++){
                        JSONObject jsonobject = json_test.getJSONObject(i);
                        String title =jsonobject.getString("title");
                        String type = jsonobject.getString("type");
                        String iurl = jsonobject.getString("iurl");
                        String collect_num =jsonobject.getString("collect_num");
                        String comment_num =jsonobject.getString("comment_num");
                        String addtime =jsonobject.getString("col_addtime");
                        Log.i("title",title );
                        Log.i("col_addtime",addtime );
                        video_entity1 video=new video_entity1();
                        video.setTitle(title);
                        video.setIurl(iurl);
                        video.setType(type);
                        video.setCollect_num(Integer.parseInt(collect_num));
                        video.setComment_num(Integer.parseInt(comment_num));
                        video.setAddtime(addtime);
                        datas.add(video);
                    }
//                    adapter=new video_col_adapter(My_collect.this,datas);
//                    recyclerView.setAdapter(adapter);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            video_col_adapter adapter=new video_col_adapter(My_collect.this,datas);
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