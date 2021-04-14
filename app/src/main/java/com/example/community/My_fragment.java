package com.example.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;


public class My_fragment extends Fragment {
    public TextView name;
    public TextView logout;
    public TextView notice;
    public TextView fan;
    public TextView collect;
    public TextView comment;
    public TextView information;
    public TextView search_collect;
    public TextView search_notice;
    public TextView edit_info;
    public String username;
    public String phone;
    public static Handler handler=new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_my_fragment, container, false);
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp =getActivity().getSharedPreferences("state", Context.MODE_PRIVATE);
        username=sp.getString("username","");
        Log.e("username",username);
        name=view.findViewById(R.id.my_name);
        logout=view.findViewById(R.id.logout);
        notice=view.findViewById(R.id.notice);
        fan=view.findViewById(R.id.fan);
        edit_info=view.findViewById(R.id.edit_information);
        collect=view.findViewById(R.id.collect);
        comment=view.findViewById(R.id.comment);
        information=view.findViewById(R.id.information);
        search_collect=view.findViewById(R.id.my_collect);
        search_notice=view.findViewById(R.id.my_notice);
        name.setText(username);
        logout.setClickable(true);
        logout.setOnClickListener(listener);
        search_collect.setClickable(true);
        edit_info.setClickable(true);
        search_notice.setClickable(true);
        edit_info.setOnClickListener(listener);
        search_collect.setOnClickListener(listener);
        search_notice.setOnClickListener(listener);
        new Thread(new MyThread()).start();
    }
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.logout){
                SharedPreferences sp = getActivity().getSharedPreferences("state",Context.MODE_PRIVATE );
                SharedPreferences.Editor edit = sp.edit();
                edit.remove("username");
                edit.apply();
                Intent intent2=new Intent(getActivity(),MainActivity.class);
                startActivity(intent2);
            }
            if(v.getId()==R.id.my_collect){
                Intent intent3=new Intent(getActivity(),My_collect.class);
                startActivity(intent3);
            }
            if(v.getId()==R.id.my_notice){
                Intent intent4=new Intent(getActivity(),My_notice.class);
                startActivity(intent4);
            }
            if(v.getId()==R.id.edit_information){
                Intent intent5=new Intent(getActivity(),edit_info.class);
                intent5.putExtra("name", username);
                intent5.putExtra("phone",phone);
                startActivity(intent5);
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            new Thread(new MyThread()).start();
        }
    }
    public class MyThread implements Runnable{

        @Override
        public void run() {
            try {
                String data = "name=" + URLEncoder.encode(username, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_getuser/?"+data);
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
                    JSONObject user=json_test.getJSONObject(0);
                    //打印json 数据
                    Log.e("phone", user.get("phone").toString());
                    phone=user.get("phone").toString();
                    String Notice= user.get("love_num").toString();
                    String Fan= user.get("fan").toString();
                    String Collect= user.get("collect_num").toString();
                    String Comment= user.get("comment_num").toString();
                    String Information= user.getString("info");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                           notice.setText(Notice);
                           fan.setText(Fan);
                           collect.setText(Collect);
                           comment.setText(Comment);
                           information.setText(Information);
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