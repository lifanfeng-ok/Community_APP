package com.example.community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Player extends Activity {
    public ImageView back;
    public ImageView img_col;
    public VideoView videoView;
    private RecyclerView recyclerView;
    public MediaController mediaController;
    public TextView title1;
    public TextView info1;
    public TextView type1;
    public TextView comment1;
    public TextView collect1;
    public TextView play1;
    public EditText content1;
    public String id;
    public String username;
    public String collect;
    public String comment;
    public static Handler handler=new Handler();
    public InputTextMsgDialog inputTextMsgDialog;
    public List<comment_entity> datas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        back= findViewById(R.id.back3);
        title1= findViewById(R.id.this_title);
        info1= findViewById(R.id.this_info);
//        type1= findViewById(R.id.this_type);
        comment1= findViewById(R.id.this_comment);
        collect1= findViewById(R.id.this_collect);
        content1= findViewById(R.id.content);
        play1= findViewById(R.id.this_play);
        img_col=findViewById(R.id.img_collect2);
        recyclerView= findViewById(R.id.recyclerView5);
        back.setClickable(true);
        back.setOnClickListener(listener);
        videoView = (VideoView)findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        SharedPreferences sp =getSharedPreferences("state", MODE_PRIVATE);
        username=sp.getString("username","");
        Intent it=getIntent();
        id=it.getStringExtra("id");
        String name=it.getStringExtra("video_name");
        String url=it.getStringExtra("url");
        collect=it.getStringExtra("collect");
        String info=it.getStringExtra("info");
        comment=it.getStringExtra("comment");
        String play=it.getStringExtra("play");
        String iurl=it.getStringExtra("iurl");
//        String type=it.getStringExtra("type");
        title1.setText(name);
//        type1.setText(type);
        info1.setText(info);
        collect1.setText(collect);
        comment1.setText(comment);
        play1.setText(play);
        int i =getResource2ByReflect(url);
        String uri="android.resource://" + getPackageName() + "/" + i;
        videoView.setMediaController(mediaController);
        mediaController.setPadding(0,0,0,0);
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
        inputTextMsgDialog = new InputTextMsgDialog(Player.this, R.style.dialog_center);
        content1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.content){
                    inputTextMsgDialog.show();
                    inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
                        @Override
                        public void onTextSend(String msg) {
                            //点击发送按钮后，回调此方法，msg为输入的值
                            new Thread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                                                        "&vid=" + URLEncoder.encode(id, "UTF-8") +
                                                        "&content=" + URLEncoder.encode(msg, "UTF-8");
                                                URL url = new URL("http://172.22.93.185:5000/android_addcom/?"+data);
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
                                                    Log.e("json", json_test.get("ok").toString());
                                                    final String[] info = {json_test.get("ok").toString()};
                                                    String face= json_test.getString("iurl");
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(info[0] == String.valueOf(1)){
                                                                comment_entity new_comment= new comment_entity();
                                                                new_comment.setInfo(msg);
                                                                new_comment.setName(username);
                                                                new_comment.setIurl(face);
                                                                datas.add(new_comment);
                                                                comment_adapter adapter2=new comment_adapter(Player.this,datas);
                                                                recyclerView.setAdapter(adapter2);
                                                                Toast toast = Toast.makeText(Player.this,"评论成功", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();
                                                                comment=String.valueOf(Integer.parseInt(comment)+1);
                                                                comment1.setText(comment);
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
                            ).start();
                        }
                    });
                }
            }
        });
        new Thread(new MyThread()).start();
        new Thread(new MyThread3()).start();
    }
    public class MyThread implements Runnable{
        @Override
        public void run() {
            try {
                String data = "id=" + URLEncoder.encode(id, "UTF-8");
                Log.i("id",id );
                URL url = new URL("http://172.22.93.185:5000/android_getcomment/?"+data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //提交数据的方式
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //设置超时时间
                connection.setConnectTimeout(8000);//连接超时
                //读取超时
                connection.setReadTimeout(8000);
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
                    //打印json 数据
                    for(int i=0; i<json_test.length();i++){
                        JSONObject jsonobject = json_test.getJSONObject(i);
                        String username =jsonobject.getString("username");
                        String info = jsonobject.getString("info");
                        String iurl = jsonobject.getString("iurl");
                        Log.i("title",username );
                        comment_entity comment_en=new comment_entity();
                        comment_en.setName(username);
                        comment_en.setIurl(iurl);
                        comment_en.setInfo(info);
                        datas.add(comment_en);
                    }
//                    adapter=new video_col_adapter(My_collect.this,datas);
//                    recyclerView.setAdapter(adapter);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            comment_adapter adapter=new comment_adapter(Player.this,datas);
                            recyclerView.setAdapter(adapter);
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
    public class MyThread3 implements Runnable{

        @Override
        public void run() {
            try {
                String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&vid=" + URLEncoder.encode(id, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_iscol/?"+data);
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
                    Log.e("json", json_test.get("ok").toString());
                    final String[] info = {json_test.get("ok").toString()};
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(info[0] == String.valueOf(1)){
                                img_col.setClickable(true);
                                img_col.setOnClickListener(listener);
                            }
                            else {
                                img_col.setClickable(false);
                                img_col.setImageResource(R.mipmap.collect_select);
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
    View.OnClickListener listener= new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.back3){
                finish();
            }
            if(v.getId()==R.id.img_collect2){
                new Thread(new MyThread2()).start();
            }
        }
    };
    public class MyThread2 implements Runnable{

        @Override
        public void run() {
            try {
                String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&vid=" + URLEncoder.encode(id, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_addcol/?"+data);
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
                    Log.e("json", json_test.get("ok").toString());
                    final String[] info = {json_test.get("ok").toString()};
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(info[0] == String.valueOf(1)){
                                Toast toast = Toast.makeText(Player.this,"收藏成功", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                collect=String.valueOf(Integer.parseInt(collect)+1);
                                collect1.setText(collect);
                                img_col.setClickable(false);
                                img_col.setImageResource(R.mipmap.collect_select);
                            }
                            else {
                                Toast toast = Toast.makeText(Player.this,"您已收藏", Toast.LENGTH_LONG);
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
    public int getResourceByReflect(String imageName){
        Class drawable  =  R.drawable.class;
        Field field = null;
        String imagename=imageName.substring(0,2);
        int r_id ;
        try {
            field = drawable.getField(imagename);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id=R.drawable.a1;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }

    public int getResource2ByReflect(String videoname){
        Class raw  =  R.raw.class;
        Field field = null;
//        String video_name=videoname.substring(0,2);
        int r_id ;
        try {
            String video_name=videoname.substring(0,2);
            field = raw.getField(video_name);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id=R.raw.a1;
            Log.e("ERROR", "Video NOT　FOUND！");
        }
        return r_id;
    }
}