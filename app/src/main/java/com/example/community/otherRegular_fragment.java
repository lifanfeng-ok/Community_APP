package com.example.community;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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


public class otherRegular_fragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    public String username;
    public static Handler handler=new Handler();
    public List<clock_entity> datas=new ArrayList<>();
    public List<max_entity> datas2=new ArrayList<>();
    public LinearLayoutManager manager;
    public LinearLayoutManager manager2;
    public ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_other_regular_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.clock_record);
        recyclerView2=view.findViewById(R.id.max_record);
        manager=new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager2=new LinearLayoutManager(getActivity());
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        recyclerView2.setLayoutManager(manager2);
        SharedPreferences sp =getActivity().getSharedPreferences("state", Context.MODE_PRIVATE);
        username=sp.getString("username","");
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("??????????????????");
        dialog.setMessage("?????????");
        dialog.setCancelable(false);//??????????????????back?????????
        dialog.show();
        new Thread(new MyThread()).start();
        new Thread(new MyThread2()).start();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            datas.clear();
            datas2.clear();
            new Thread(new MyThread()).start();
            new Thread(new MyThread2()).start();
        }
    }
    public class MyThread2 implements Runnable{

        @Override
        public void run() {
            try {
                String data = "username=" + URLEncoder.encode(username, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_max_other_clock/?"+data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //?????????????????????
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //??????????????????
                connection.setConnectTimeout(8000);//????????????
                //????????????
                connection.setReadTimeout(8000);
                Log.i("name", username);
                connection.connect();
                InputStream inputStream=null;
                BufferedReader reader=null;
                if (connection.getResponseCode() == 200) {
                    inputStream=connection.getInputStream();
                    //???????????????????????????buffered???
                    reader=new BufferedReader(new InputStreamReader(inputStream));
                    //???????????????????????????result
                    String result = reader.readLine();
                    JSONArray json_test = new JSONArray(result);
                    //??????json ??????
                    for(int i=0; i<json_test.length();i++){
                        JSONObject jsonobject = json_test.getJSONObject(i);
                        String name =jsonobject.getString("username");
                        String iurl = jsonobject.getString("iurl");
                        String clock_num =jsonobject.getString("clock_num");
                        String order = jsonobject.getString("order");
                        Log.i("name",name );
                        max_entity m=new max_entity();
                        m.setName(name);
                        m.setIurl(iurl);
                        m.setClock_num(Integer.parseInt(clock_num));
                        m.setOrder(Integer.parseInt(order));
                        datas2.add(m);
                    }
//                    adapter=new video_col_adapter(My_collect.this,datas);
//                    recyclerView.setAdapter(adapter);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            max_adapter adapter=new max_adapter(getActivity(),datas2);
                            recyclerView2.setAdapter(adapter);
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
    public class MyThread implements Runnable{

        @Override
        public void run() {
            try {
                String data = "username=" + URLEncoder.encode(username, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_get_other_clock/?"+data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //?????????????????????
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //??????????????????
                connection.setConnectTimeout(8000);//????????????
                //????????????
                connection.setReadTimeout(8000);
                connection.connect();
                InputStream inputStream=null;
                BufferedReader reader=null;
                if (connection.getResponseCode() == 200) {
                    inputStream=connection.getInputStream();
                    //???????????????????????????buffered???
                    reader=new BufferedReader(new InputStreamReader(inputStream));
                    //???????????????????????????result
                    String result = reader.readLine();
                    JSONArray json_test = new JSONArray(result);
//                    List<video_entity1> datas=new ArrayList<>();
                    //??????json ??????
                    for(int i=0; i<json_test.length();i++){
                        JSONObject jsonobject = json_test.getJSONObject(i);
                        String id =jsonobject.getString("id");
                        String name =jsonobject.getString("name");
                        String iurl = jsonobject.getString("iurl");
                        String good_num =jsonobject.getString("good_num");
                        String type =jsonobject.getString("type");
                        String time =jsonobject.getString("time");
                        String content =jsonobject.getString("content");
                        String publish = jsonobject.getString("publish_time");
                        clock_entity rl=new clock_entity();
                        rl.setId(Integer.parseInt(id));
                        rl.setName(name);
                        rl.setIurl(iurl);
                        rl.setContent(content);
                        rl.setPublish_time(publish);
                        rl.setTime(time);
                        rl.setType(type);
                        rl.setGood_count(Integer.parseInt(good_num));
                        datas.add(rl);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            clock_adapter adapter=new clock_adapter(getActivity(),datas);
                            recyclerView.setAdapter(adapter);
                            dialog.dismiss();
                            adapter.setItemClickListener(new clock_adapter.MyItemClickListener(){

                                @Override
                                public void onItemClick(View view, int position) {
                                    String id= String.valueOf(datas.get(position).getId());
//                                    Toast.makeText(getActivity(), "?????????" + name, Toast.LENGTH_SHORT).show();
                                    new Thread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                                                                "&clock_id=" + URLEncoder.encode(id, "UTF-8");
                                                        URL url = new URL("http://172.22.93.185:5000/android_good_other_clock/?"+data);
                                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                        //?????????????????????
                                                        connection.setRequestMethod("POST");
                                                        connection.setDoOutput(true);
                                                        //??????????????????
                                                        connection.setConnectTimeout(8000);//????????????
                                                        //????????????
                                                        connection.setReadTimeout(8000);
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
                                                            final String[] info = {json_test.get("ok").toString()};
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if(info[0] == String.valueOf(1)){
                                                                        Toast toast = Toast.makeText(getActivity(),"????????????", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        View view = manager.findViewByPosition(position);
                                                                        assert view != null;
                                                                        TextView dz = view.findViewById(R.id.clock_good_num);
                                                                        String dz_count= dz.getText().toString();
                                                                        Integer dz_num = Integer.parseInt(dz_count)+1;
                                                                        dz.setText(String.valueOf(dz_num));
                                                                    }
                                                                    else {
                                                                        Toast toast = Toast.makeText(getActivity(),"????????????", Toast.LENGTH_LONG);
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
                                    ).start();
                                }
                            });
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