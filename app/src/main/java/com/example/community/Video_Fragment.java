package com.example.community;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Video_Fragment extends Fragment {
    private RecyclerView recyclerView;
    public EditText search;
    public ImageView searched;
    public static Handler handler=new Handler();
    public List<video_entity1> datas=new ArrayList<>();
    public List<video_entity1> searched_datas=new ArrayList<>();
    public ProgressDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_video_, container, false);
        recyclerView= v.findViewById(R.id.recyclerView);
        search= v.findViewById(R.id.search);
        searched= v.findViewById(R.id.searched);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus().getWindowToken()!=null){
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
        return v;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            datas.clear();
            new Thread(new MyThread()).start();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
//        video_adapter adapter=new video_adapter(getActivity(),datas);
//        cycleview.setAdapter(adapter);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("正在获取数据");
        dialog.setMessage("请稍后");
        dialog.setCancelable(false);//设置可以通过back键取消
        dialog.show();
        new Thread(new MyThread()).start();
        searched.setClickable(true);
        searched.setOnClickListener(listener);
    }
    View.OnClickListener listener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.searched){
                String value=search.getText().toString().trim();
                searched_datas=search(value,datas);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        video_adapter adapter2=new video_adapter(getActivity(),searched_datas);
                        recyclerView.setAdapter(adapter2);
                        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
            }
        }
    };
    public  ArrayList<video_entity1> search(String name, List<video_entity1> list) {
        ArrayList<video_entity1> results = new ArrayList<video_entity1>();
        Pattern pattern = Pattern.compile(name);
//      如果要求大小写不敏感，改成：
//      Pattern pattern = Pattern.compile(name,Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < list.size(); i++) {
            Matcher matcher = pattern.matcher(((video_entity1)list.get(i)).getTitle());
            //匹配查询
            //matcher.matches()
            if (matcher.find()) {
                results.add(list.get(i));
            }
        }
        return results;
    }

    public class MyThread implements Runnable{

        @Override
        public void run() {
            try {
                URL url = new URL("http://172.22.93.185:5000/android_getvideo/");
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
//                    List<video_entity1> datas=new ArrayList<>();
                    //打印json 数据
                    for(int i=0; i<json_test.length();i++){
                        JSONObject jsonobject = json_test.getJSONObject(i);
                        String id = jsonobject.getString("id");
                        String title =jsonobject.getString("name");
                        String type = jsonobject.getString("videotype");
                        String info = jsonobject.getString("info");
                        String url2 = jsonobject.getString("url");
                        String iurl = jsonobject.getString("iurl");
                        String play_num =jsonobject.getString("watchnum");
                        String collect_num =jsonobject.getString("real_collectnum");
                        String comment_num =jsonobject.getString("collectnum");
                        Log.i("id",id );
                        video_entity1 video=new video_entity1();
                        video.setId(Integer.parseInt(id));
                        video.setTitle(title);
                        video.setIurl(iurl);
                        video.setType(type);
                        video.setInfo(info);
                        video.setUrl(url2);
                        video.setPlay_num(Integer.parseInt(play_num));
                        video.setCollect_num(Integer.parseInt(collect_num));
                        video.setComment_num(Integer.parseInt(comment_num));
                        datas.add(video);
                    }
//                    video_adapter adapter=new video_adapter(getActivity(),datas);
//                    recyclerView.setAdapter(adapter);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            video_adapter adapter=new video_adapter(getActivity(),datas);
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