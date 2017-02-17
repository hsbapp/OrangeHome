package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.hsb.ACDevice;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbDeviceListener;
import com.cg.hsb.HsbListener;

public class AirCRemoteCActivity extends BaseActivity {
    LinearLayout air_conditioner_off,air_conditioner_on;
    TextView air_c_remote_c_name,air_c_remote_c_temperature,air_c_remote_c_air_c_remote_c_mode,air_c_remote_c_air_c_remote_c_wend_speed,air_c_remote_c_name_off;
    ImageView air_c_remote_c_air_c_remote_c_switch,air_c_remote_c_air_c_remote_c_mode1,air_c_remote_c_air_c_remote_c_tem_add,air_c_remote_c_air_c_remote_c_tem_sub;
    ImageView air_c_remote_c_air_c_remote_c_wind_speed,air_c_remote_c_air_c_remote_c_wend_light,air_c_remote_c_air_c_remote_c_wend_voice_r;
    ACDevice airConditioner;
    private static final int AC_UPDATE=1;
    int device_id;
    private Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_cremote_c);
        device_id=getIntent().getExtras().getInt("dev_id");
        handler=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AC_UPDATE:
                        setLeftShow();
                        break;
                }
            }

        };
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_air_cremote_c, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView(){

        air_c_remote_c_name=(TextView)findViewById(R.id.air_c_remote_c_name);
        air_c_remote_c_temperature=(TextView)findViewById(R.id.air_c_remote_c_temperature);
        air_c_remote_c_air_c_remote_c_mode=(TextView)findViewById(R.id.air_c_remote_c_air_c_remote_c_mode);
        air_c_remote_c_air_c_remote_c_wend_speed=(TextView)findViewById(R.id.air_c_remote_c_air_c_remote_c_wend_speed);
        air_c_remote_c_name_off=(TextView)findViewById(R.id.air_c_remote_c_name_off);
        air_c_remote_c_air_c_remote_c_switch=(ImageView)findViewById(R.id.air_c_remote_c_air_c_remote_c_switch);
        air_c_remote_c_air_c_remote_c_mode1=(ImageView)findViewById(R.id.air_c_remote_c_air_c_remote_c_mode1);
        air_c_remote_c_air_c_remote_c_tem_add=(ImageView)findViewById(R.id.air_c_remote_c_air_c_remote_c_tem_add);
        air_c_remote_c_air_c_remote_c_tem_sub=(ImageView)findViewById(R.id.air_c_remote_c_air_c_remote_c_tem_sub);
        air_c_remote_c_air_c_remote_c_wind_speed=(ImageView)findViewById(R.id.air_c_remote_c_air_c_remote_c_wind_speed);
        air_c_remote_c_air_c_remote_c_wend_light=(ImageView)findViewById(R.id.air_c_remote_c_air_c_remote_c_wend_light);
        air_c_remote_c_air_c_remote_c_wend_voice_r=(ImageView)findViewById(R.id.air_c_remote_c_air_c_remote_c_wend_voice_r);
        air_conditioner_off=(LinearLayout)findViewById(R.id.air_conditioner_off);
        air_conditioner_off.setVisibility(View.GONE);
        air_conditioner_on=(LinearLayout)findViewById(R.id.air_conditioner_on);
        airConditioner=(ACDevice)OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(device_id);

        airConditioner.SetListener(new HsbDeviceListener()
        {
            @Override
            public void onDeviceUpdated(HsbDevice device)
            {
                Message message = new Message();
                message.what = AC_UPDATE;
                handler.sendMessage(message);
            }
        });

        setLeftShow();
        air_c_remote_c_air_c_remote_c_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airConditioner.SetPower(airConditioner.GetPower() ? false : true);
/*
                if (airConditioner.Power()==0){
                    OrangeHomeApplication.getOrangeHomeApplication().addToLogsDB(getBaseContext().getResources().getString(R.string.log_info_ac_power_off)+airConditioner.GetName());
                }else if (airConditioner.Power()==1){
                    OrangeHomeApplication.getOrangeHomeApplication().addToLogsDB(getBaseContext().getResources().getString(R.string.log_info_ac_power_on)+airConditioner.GetName());
                }
*/
            }
        });
        air_c_remote_c_air_c_remote_c_mode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airConditioner.NextWorkMode();
            }
        });
        air_c_remote_c_air_c_remote_c_tem_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airConditioner.AddTemperature();
            }
        });

        air_c_remote_c_air_c_remote_c_tem_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airConditioner.DecTemperature();
            }
        });
        air_c_remote_c_air_c_remote_c_wind_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airConditioner.NextWindSpeed();
            }
        });

        air_c_remote_c_air_c_remote_c_wend_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airConditioner.SetLight(airConditioner.GetLight() ? false : true);
            }
        });

        air_c_remote_c_air_c_remote_c_wend_voice_r.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
/*
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Toast.makeText(AirCRemoteCActivity.this,getBaseContext().getResources().getText(R.string.start_voice_recognizer),Toast.LENGTH_LONG).show();
                    OrangeHomeApplication.getOrangeHomeApplication().getmProto().StartVoiceRecognizer();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    OrangeHomeApplication.getOrangeHomeApplication().getmProto().StopVoiceRecognizer();
                }
*/
                return false;
            }
        });


    }



    private void setLeftShow(){
        if(airConditioner.GetPower()){
            air_conditioner_off.setVisibility(View.GONE);
            air_conditioner_on.setVisibility(View.VISIBLE);
            air_c_remote_c_name.setText(airConditioner.GetName());
            air_c_remote_c_temperature.setText(airConditioner.GetTemperature()+" ");
            air_c_remote_c_air_c_remote_c_mode.setText(airConditioner.GetWorkMode());
            air_c_remote_c_air_c_remote_c_wend_speed.setText(airConditioner.GetWindSpeed());
        }
        else if(!airConditioner.GetPower()){
            air_conditioner_on.setVisibility(View.GONE);
            air_conditioner_off.setVisibility(View.VISIBLE);
            air_c_remote_c_name_off.setText(airConditioner.GetName());
        }
    }

    @Override
    protected void onStop(){
        airConditioner.SetListener(null);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        airConditioner.SetListener(new HsbDeviceListener() {
            @Override
            public void onDeviceUpdated(HsbDevice device) {
                Message message = new Message();
                message.what = AC_UPDATE;
                handler.sendMessage(message);
            }
        });

        super.onRestart();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            airConditioner.SetListener(null);
        }
        return super.dispatchKeyEvent(event);
    }
}
