package com.example.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class myRegular_fragment extends Fragment implements MyCalendarView.OnItemClickListener{
    private MyCalendarView mMyCalendarView;//自定义的日历控件
    //ClockStates：考勤状态
    private List<ClockStates> lists;
    private List<ClockStates> lists2;
    private TextView tv;
    private Button clock;
    public static Handler handler=new Handler();
    public String username;
    public Boolean able_clock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_my_regular_fragment, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        lists.clear();
        lists2.clear();
        initData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv = ((TextView)view.findViewById(R.id.tv));
        clock=view.findViewById(R.id.clock);
        SharedPreferences sp =getActivity().getSharedPreferences("state", Context.MODE_PRIVATE);
        username=sp.getString("username","");
        mMyCalendarView = ((MyCalendarView)view.findViewById(R.id.calender));
        //点击日历单元格的回调
        mMyCalendarView.setOnItemClickListener(this);
        initData();
    }
    View.OnClickListener listener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.clock){
                Intent intent=new Intent(getActivity(),do_regular.class);
                startActivity(intent);
            }
        }
    };
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            lists.clear();
            lists2.clear();
            initData();
        }
    }

    @Override
    public void OnItemClick(Date date) {
        String selectDate = getStringByFormat(date, "yyyy-MM-dd");//2017-03-31
        int selectYear = Integer.parseInt(selectDate.split("-")[0]);
        int selectMonth = Integer.parseInt(selectDate.split("-")[1]);
        Toast.makeText(getActivity(), selectDate, Toast.LENGTH_SHORT).show();
//        if (mMyCalendarView.isLastMonth(selectYear, selectMonth)) {//点击的那一天是当天显示月的上一个月
////            mMyCalendarView.clickLeftMonth();
////            tv.setText(mMyCalendarView.getYearAndmonth());
////            //TODO 实际开发中我是联网加载上个月的考勤，得到lists，再设置给日历
////            lists.clear();//这里因为没有联网数据，那么我们清空掉之前造的数据，看看效果
////            mMyCalendarView.setClockStates(lists);
//        } else if (mMyCalendarView.isNextMonth(selectYear, selectMonth)) {//是下一个月
////            mMyCalendarView.clickRightMonth();
////            tv.setText(mMyCalendarView.getYearAndmonth());
////            //TODO 实际开发中我是联网加载下个月的考勤，得到lists，再设置给日历
////            lists.clear();//这里因为没有联网数据，那么我们清空掉之前造的数据，看看效果
////            mMyCalendarView.setClockStates(lists);
//        }
    }
    private void initData() {
        lists = new ArrayList<>();
        lists2 = new ArrayList<>();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data = "name=" + URLEncoder.encode(username, "UTF-8");
                            URL url = new URL("http://172.22.93.185:5000/android_isclock/?"+data);
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
                                JSONArray json_test = new JSONArray(result);
                                for(int i=0; i<json_test.length();i++) {
                                    JSONObject jsonobject = json_test.getJSONObject(i);
                                    String date = jsonobject.getString("date");
                                    String date_type = jsonobject.getString("date_type");
                                    Log.e("date", date);
                                    lists2.add(new ClockStates(date,date_type));
                                }
                                lists=changeList(lists2);
                                able_clock = able_clock(lists);
                                //打印json 数据
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMyCalendarView.setClockStates(lists);
                                        if (able_clock) {
                                            clock.setOnClickListener(listener);
                                        } else {
                                            clock.setText("今天已打卡");
                                            clock.setTextColor(Color.parseColor("#FF000000"));
                                            clock.setClickable(false);
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
//        mMyCalendarView.setClockStates(lists);
    }
    public List<ClockStates> changeList( List<ClockStates> lists){
        List<ClockStates> lists3 = new ArrayList<>();
        List<ClockStates> lists4 = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        String str_month;
        String str_date;
        int month = (cal.get(Calendar.MONTH)) + 1;
        int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
        if (month<10){
             str_month = "0"+String.valueOf(month);
        } else {
            str_month = String.valueOf(month);
        }
        if (day_of_month < 10){
             str_date = "0"+String.valueOf(day_of_month);
        } else {
             str_date = String.valueOf(day_of_month);
        }
        Log.e("month", str_month);
        Log.e("date", str_date);
        for (int i=0; i<lists.size(); i++){
            if (lists.get(i).getDate().substring(5, 7).equals(str_month)) {
                lists4.add(lists.get(i));
            }
        }
        outer:for (int i=1; i<= day_of_month; i++){
            if (i<10){
            for (int k=0; k<lists4.size(); k++){
                if (lists4.get(k).getDate().substring(8, 10).equals("0" + String.valueOf(i))){
                    String date =  "2021-"+str_month+"-0"+String.valueOf(i);
                    String date_type = "1";
                    lists3.add(new ClockStates(date,date_type));
                    Log.e("clock","1" );
                    continue outer;
                }
            }
                String date =  "2021-"+str_month+"-0"+String.valueOf(i);
                String date_type = "2";
                lists3.add(new ClockStates(date,date_type));
            } else {
                for (int j=0; j<lists4.size(); j++){
                    if (lists4.get(j).getDate().substring(8, 10).equals(String.valueOf(i))){
                        String date = "2021-"+str_month+"-"+String.valueOf(i);
                        String date_type = "1";
                        lists3.add(new ClockStates(date,date_type));
                        Log.e("clock","1" );
                        continue outer;
                    }
                }
                String date = "2021-"+str_month+"-"+String.valueOf(i);
                String date_type = "2";
                lists3.add(new ClockStates(date,date_type));
            }
        }
        return lists3;
    }

    public Boolean able_clock(List<ClockStates> lists){
        String str_date;
        Calendar cal = Calendar.getInstance();
        int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
        if (day_of_month < 10){
            str_date = "0"+String.valueOf(day_of_month);
        } else {
            str_date = String.valueOf(day_of_month);
        }
        int length=lists.size();
        if (lists.get(length - 1).getDateType().equals("1")){
            return false;
        } else {
            return true;
        }
    }
    public String getStringByFormat(Date date, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        String strDate = null;
        try {
            strDate = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }
}