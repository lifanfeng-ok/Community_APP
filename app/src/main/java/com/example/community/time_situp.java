package com.example.community;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class time_situp extends Activity {
    public Button train;
    public TextView time_rest;
    public TextView rest_time;
    public TextView situp_number;
    public ImageView back;
    public TextView number;
    public int real_number=0;
    public EditText select_time;
    public RelativeLayout rl1;
    public RelativeLayout rl2;
    public VideoView video;
    public ImageView logo;
    public String time;
    public Integer real_time;
    public Timer timer = null;
    public TimerTask task = null;
    public int re_time;
    public MediaController mediaController;
    public SensorManager sensormanager;
    public Sensor sensor;
    public Boolean check_back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_situp);
        sensormanager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if(sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            sensor=sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }else {
            Toast.makeText(time_situp.this, "没有加速度计传感器", Toast.LENGTH_LONG).show();
        }
        video= (VideoView)findViewById(R.id.video);
        back = (ImageView)findViewById(R.id.fit_back);
        mediaController = new MediaController(this);
        String uri="android.resource://" + getPackageName() + "/" + R.raw.a2;
        video.setMediaController(mediaController);
        mediaController.setPadding(0,0,0,0);
        video.setVideoURI(Uri.parse(uri));
        train =(Button)findViewById(R.id.train);
        time_rest = (TextView)findViewById(R.id.time_rest);
        rl1 = (RelativeLayout)findViewById(R.id.rl1);
        rl2 = (RelativeLayout)findViewById(R.id.rl2);
        rest_time = (TextView)findViewById(R.id.rest_time);
        situp_number = (TextView)findViewById(R.id.situp_number);
        number = (TextView)findViewById(R.id.number);
        select_time = (EditText)findViewById(R.id.select_time);
        logo = (ImageView)findViewById(R.id.time_logo);
        time_rest.setVisibility(View.GONE);
        rest_time.setVisibility(View.GONE);
        situp_number.setVisibility(View.GONE);
        number.setVisibility(View.GONE);
        train.setOnClickListener(listener);
        back.setOnClickListener(listener);

    }
    SensorEventListener sensor_listener= new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values=event.values;
            if(!check_back){
                if(values[2] < 0.5) {
                    real_number=real_number+1;
                    number.setText(real_number+"个");
                    check_back =true;
                }
            }
            else {
                if(values[2]>=9.7){
                    check_back =false;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    View.OnClickListener listener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.train){
                time = select_time.getText().toString().trim();
                if(time.length() == 0){
                    Toast toast = Toast.makeText(time_situp.this,"请选择运动时长", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                  real_time = Integer.parseInt(time);
                  re_time = real_time;
                if(timer==null){
                    timer=new Timer();
                }
                if(task==null){
                    task = new TimerTask() {
                    @Override
                    public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    if(re_time==real_time){
                        sensormanager.registerListener(sensor_listener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
                        select_time.setVisibility(View.GONE);
                        logo.setVisibility(View.GONE);
                        train.setVisibility(View.GONE);
                        rl1.setVisibility(View.GONE);
                        rl2.setVisibility(View.GONE);
                        time_rest.setVisibility(View.VISIBLE);
                        rest_time.setVisibility(View.VISIBLE);
                        rest_time.setText(re_time+"秒");
                        situp_number.setVisibility(View.VISIBLE);
                        number.setVisibility(View.VISIBLE);
                        video.start();
                    }
                    re_time--;
                    rest_time.setText(re_time+"秒");
                    if(re_time < 0){
                        sensormanager.unregisterListener(sensor_listener);
                        video.seekTo(0);
                        video.pause();
                        timer.cancel();
                        task.cancel();
                        rest_time.setVisibility(View.GONE);
                        time_rest.setVisibility(View.GONE);
                        situp_number.setVisibility(View.GONE);
                        number.setVisibility(View.GONE);
                        timer=null;
                        task=null;
                        rl1.setVisibility(View.VISIBLE);
                        rl2.setVisibility(View.VISIBLE);
                        select_time.setVisibility(View.VISIBLE);
                        logo.setVisibility(View.VISIBLE);
                        train.setVisibility(View.VISIBLE);
                        new AlertDialog.Builder(time_situp.this).setTitle("运动结果")//设置对话框标题

                                .setMessage("运动时长："+time+"秒， 仰卧起坐个数："+String.valueOf(real_number))
                                .setPositiveButton("了解", new DialogInterface.OnClickListener() {//添加确定按钮

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                              dialog.dismiss();
                                    }
                                }).show();
                            real_number=0;
                            number.setText(real_number+"个");
                    }
                }
            });
        }
    };
                }
                if(timer != null&& task!= null ){
                    timer.schedule(task, 0, 1000);
                }
                }
            }
            if(v.getId()==R.id.fit_back){
                finish();
            }
        }
    };
}