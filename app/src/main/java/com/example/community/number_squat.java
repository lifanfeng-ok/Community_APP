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

public class number_squat extends Activity {
    public Button train;
    public TextView running_time;
    public TextView time_running;
    public TextView situp_number;
    public ImageView number_logo;
    public TextView number;
    public EditText select_number;
    public RelativeLayout rl5;
    public RelativeLayout rl6;
    public VideoView video;
    public ImageView back_logo;
    public String num;
    public int real_num;
    public Timer timer = null;
    public TimerTask task = null;
    public int re_time;
    public int ing_number=0;
    public int time=0;
    public MediaController mediaController;
    public SensorManager sensormanager;
    public Sensor sensor;
    public Boolean check_back=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_squat);
        sensormanager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if(sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            sensor=sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }else {
            Toast.makeText(number_squat.this, "没有加速度计传感器", Toast.LENGTH_LONG).show();
        }
        video= (VideoView)findViewById(R.id.video4);
        back_logo = (ImageView)findViewById(R.id.number_back2);
        mediaController = new MediaController(this);
        String uri="android.resource://" + getPackageName() + "/" + R.raw.a5;
        video.setMediaController(mediaController);
        mediaController.setPadding(0,0,0,0);
        video.setVideoURI(Uri.parse(uri));
        train =(Button)findViewById(R.id.train4);
        running_time = (TextView)findViewById(R.id.running_time2);
        rl5 = (RelativeLayout)findViewById(R.id.rl_5);
        rl6 = (RelativeLayout)findViewById(R.id.rl_6);
        time_running = (TextView)findViewById(R.id.time_running2);
        situp_number = (TextView)findViewById(R.id.squat_number_number);
        number = (TextView)findViewById(R.id.number4);
        select_number = (EditText)findViewById(R.id.select_number2);
        number_logo = (ImageView)findViewById(R.id.number_logo2);
        running_time.setVisibility(View.GONE);
        time_running.setVisibility(View.GONE);
        situp_number.setVisibility(View.GONE);
        number.setVisibility(View.GONE);
        train.setOnClickListener(listener);
        back_logo.setOnClickListener(listener);
    }
    SensorEventListener sensor_listener= new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values=event.values;
            if(!check_back){
                if(values[1] < 7.3) {
                    ing_number=ing_number+1;
                    number.setText(ing_number+"个");
                    check_back =true;
                }
            }
            else {
                if(values[1]>9.6){
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
            if(v.getId()==R.id.number_back2){
                finish();
            }
            if(v.getId() == R.id.train4){
                num = select_number.getText().toString().trim();
                if(num.length() == 0){
                    Toast toast = Toast.makeText(number_squat.this,"请选择运动个数", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    real_num = Integer.parseInt(num);
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
                                        if(time == 0){
                                            sensormanager.registerListener(sensor_listener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
                                            select_number.setVisibility(View.GONE);
                                            number_logo.setVisibility(View.GONE);
                                            train.setVisibility(View.GONE);
                                            rl5.setVisibility(View.GONE);
                                            rl6.setVisibility(View.GONE);
                                            running_time.setVisibility(View.VISIBLE);
                                            time_running.setVisibility(View.VISIBLE);
                                            time_running.setText(time+"秒");
                                            situp_number.setVisibility(View.VISIBLE);
                                            number.setVisibility(View.VISIBLE);
                                            video.start();
                                        }
                                        time++;
                                        time_running.setText(time+"秒");
                                        if(ing_number == real_num){
                                            sensormanager.unregisterListener(sensor_listener);
                                            video.seekTo(0);
                                            video.pause();
                                            timer.cancel();
                                            task.cancel();
                                            running_time.setVisibility(View.GONE);
                                            time_running.setVisibility(View.GONE);
                                            situp_number.setVisibility(View.GONE);
                                            number.setVisibility(View.GONE);
                                            timer=null;
                                            task=null;
                                            rl5.setVisibility(View.VISIBLE);
                                            rl6.setVisibility(View.VISIBLE);
                                            select_number.setVisibility(View.VISIBLE);
                                            number_logo.setVisibility(View.VISIBLE);
                                            train.setVisibility(View.VISIBLE);
                                            new AlertDialog.Builder(number_squat.this).setTitle("运动结果")//设置对话框标题

                                                    .setMessage("运动时长："+String.valueOf(time)+"秒， 深蹲个数："+ String.valueOf(real_num))
                                                    .setPositiveButton("了解", new DialogInterface.OnClickListener() {//添加确定按钮

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                                            dialog.dismiss();
                                                        }
                                                    }).show();
                                            time=0;
                                            time_running.setText(time+"秒");
                                            ing_number=0;
                                            number.setText(ing_number+"个");
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
        }
    };
}