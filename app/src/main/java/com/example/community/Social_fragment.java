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

public class Social_fragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    public static Handler handler=new Handler();
    public List<recommend_entity> datas=new ArrayList<>();
    public List<recommend_entity> datas2=new ArrayList<>();
    public String username;
    public ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_social_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp =getActivity().getSharedPreferences("state", Context.MODE_PRIVATE);
        username=sp.getString("username","");
        recyclerView=view.findViewById(R.id.common_list);
        recyclerView2=view.findViewById(R.id.potential_list);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        LinearLayoutManager manager2=new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView2.setLayoutManager(manager2);
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
                URL url = new URL("http://172.22.93.185:5000/android_getrecommend2/?"+data);
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
                        String name =jsonobject.getString("username");
                        String iurl = jsonobject.getString("iurl");
                        String notice_num =jsonobject.getString("notice");
                        String collect_num =jsonobject.getString("collect");
                        String comment_num =jsonobject.getString("comment");
                        String common_num =jsonobject.getString("common2");
                        recommend_entity rl=new recommend_entity();
                        rl.setName(name);
                        rl.setIurl(iurl);
                        rl.setNotice_num(Integer.parseInt(notice_num));
                        rl.setCollect_num(Integer.parseInt(collect_num));
                        rl.setComment_num(Integer.parseInt(comment_num));
                        rl.setCommon_video_num(Integer.parseInt(common_num));
                        datas2.add(rl);
                    }
//                    video_adapter adapter=new video_adapter(getActivity(),datas);
//                    recyclerView.setAdapter(adapter);
                    handler.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         recommend2_adapter adapter2 = new recommend2_adapter(getActivity(), datas2);
                                         recyclerView2.setAdapter(adapter2);
                                         dialog.dismiss();
                                         adapter2.setItemClickListener(new recommend2_adapter.MyItemClickListener(){

                                @Override
                                public void onItemClick(View view, int position) {
                                    String name=datas2.get(position).getName();
//                                    Toast.makeText(getActivity(), "?????????" + name, Toast.LENGTH_SHORT).show();
                                    new Thread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                                                                "&his_name=" + URLEncoder.encode(name, "UTF-8");
                                                        URL url = new URL("http://172.22.93.185:5000/android_addlove/?"+data);
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
    public class MyThread implements Runnable{

        @Override
        public void run() {
            try {
                String data = "username=" + URLEncoder.encode(username, "UTF-8");
                URL url = new URL("http://172.22.93.185:5000/android_getrecommend1/?"+data);
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
                        String name =jsonobject.getString("name");
                        String iurl = jsonobject.getString("iurl");
                        String notice_num =jsonobject.getString("notice");
                        String collect_num =jsonobject.getString("collect");
                        String comment_num =jsonobject.getString("comment");
                        String common_num =jsonobject.getString("common");
                        recommend_entity rl=new recommend_entity();
                        rl.setName(name);
                        rl.setIurl(iurl);
                        rl.setNotice_num(Integer.parseInt(notice_num));
                        rl.setCollect_num(Integer.parseInt(collect_num));
                        rl.setComment_num(Integer.parseInt(comment_num));
                        rl.setCommon_notice_num(Integer.parseInt(common_num));
                        datas.add(rl);
                    }
//                    video_adapter adapter=new video_adapter(getActivity(),datas);
//                    recyclerView.setAdapter(adapter);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recommend_adapter adapter=new recommend_adapter(getActivity(),datas);
                            recyclerView.setAdapter(adapter);
                            dialog.dismiss();
                            adapter.setItemClickListener(new recommend_adapter.MyItemClickListener(){

                                @Override
                                public void onItemClick(View view, int position) {
                                    String name=datas.get(position).getName();
//                                    Toast.makeText(getActivity(), "?????????" + name, Toast.LENGTH_SHORT).show();
                                    new Thread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                                                                "&his_name=" + URLEncoder.encode(name, "UTF-8");
                                                        URL url = new URL("http://172.22.93.185:5000/android_addlove/?"+data);
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