package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDeviceAction;
import com.cg.hsb.TVDevice;
import com.gmoonxs.www.orangehome.R;

public class CC9201RemoteCActivity extends BaseActivity {

    ImageView cc9201_remote_switch,cc9201_remote_mute,up_remote_control,down_remote_control,left_remote_control,right_remote_control;
    ImageView cc9201_remote_channel_add,cc9201_remote_channel_sub,cc9201_remote_voice_add,cc9201_remote_voice_sub;
    TextView remote_control_1,remote_control_2,remote_control_3,remote_control_4,remote_control_5,remote_control_6,remote_control_7,remote_control_8,remote_control_9,remote_control_0;
    ImageView voice_remote_control;
    TextView ok_remote_control,return_remote_control,channel_remote_control;
    LinearLayout cc9201_remote_left_center;
    TVDevice cc9201;
    int device_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc9201_remote_c);
        initView();
        device_id=getIntent().getExtras().getInt("dev_id");
        cc9201=(TVDevice)OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(device_id);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cc9201_remote_c, menu);
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
        cc9201_remote_switch=(ImageView)findViewById(R.id.cc9201_remote_switch);
        cc9201_remote_mute=(ImageView)findViewById(R.id.cc9201_remote_mute);
        up_remote_control=(ImageView)findViewById(R.id.up_remote_control);
        down_remote_control=(ImageView)findViewById(R.id.down_remote_control);
        left_remote_control=(ImageView)findViewById(R.id.left_remote_control);
        right_remote_control=(ImageView)findViewById(R.id.right_remote_control);
        cc9201_remote_channel_add=(ImageView)findViewById(R.id.cc9201_remote_channel_add);
        cc9201_remote_channel_sub=(ImageView)findViewById(R.id.cc9201_remote_channel_sub);
        return_remote_control=(TextView)findViewById(R.id.return_remote_control);
        cc9201_remote_voice_add=(ImageView)findViewById(R.id.cc9201_remote_voice_add);
        cc9201_remote_voice_sub=(ImageView)findViewById(R.id.cc9201_remote_voice_sub);
        remote_control_1=(TextView)findViewById(R.id.remote_control_1);
        remote_control_2=(TextView)findViewById(R.id.remote_control_2);
        remote_control_3=(TextView)findViewById(R.id.remote_control_3);
        remote_control_4=(TextView)findViewById(R.id.remote_control_4);
        remote_control_5=(TextView)findViewById(R.id.remote_control_5);
        remote_control_6=(TextView)findViewById(R.id.remote_control_6);
        remote_control_7=(TextView)findViewById(R.id.remote_control_7);
        remote_control_8=(TextView)findViewById(R.id.remote_control_8);
        remote_control_9=(TextView)findViewById(R.id.remote_control_9);
        remote_control_0=(TextView)findViewById(R.id.remote_control_0);
        ok_remote_control=(TextView)findViewById(R.id.ok_remote_control);
        channel_remote_control=(TextView)findViewById(R.id.channel_remote_control);
        voice_remote_control=(ImageView)findViewById(R.id.voice_r_remote_control);
        cc9201_remote_left_center=(LinearLayout)findViewById(R.id.cc9201_remote_left_center);
        cc9201_remote_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    cc9201.PressKey(HsbConstant.HSB_TV_KEY_ON_OFF);
            }
        });
        cc9201_remote_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_MUTE);
            }
        });
        up_remote_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_UP);
            }
        });
        down_remote_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_DOWN);
            }
        });
        left_remote_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_LEFT);
            }
        });
        right_remote_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_RIGHT);
            }
        });
        cc9201_remote_channel_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_UP);
            }
        });
        cc9201_remote_channel_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_DOWN);
            }
        });
        return_remote_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_BACK);
            }
        });
        cc9201_remote_voice_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_ADD_VOL);
            }
        });
        cc9201_remote_voice_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_DEC_VOL);
            }
        });
        ok_remote_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_OK);
            }
        });
        remote_control_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_1);
            }
        });
        remote_control_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_2);
            }
        });
        remote_control_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_3);
            }
        });
        remote_control_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_4);
            }
        });
        remote_control_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_5);
            }
        });
        remote_control_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_6);
            }
        });
        remote_control_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_7);
            }
        });
        remote_control_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_8);
            }
        });
        remote_control_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_9);
            }
        });
        remote_control_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.PressKey(HsbConstant.HSB_TV_KEY_0);
            }
        });
        channel_remote_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CC9201RemoteCActivity.this,ChannelListActivity.class);
                intent.putExtra("dev_id",device_id);
                startActivity(intent);
            }
        });

        voice_remote_control.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
/*
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Toast.makeText(CC9201RemoteCActivity.this, getBaseContext().getResources().getText(R.string.start_voice_recognizer), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
