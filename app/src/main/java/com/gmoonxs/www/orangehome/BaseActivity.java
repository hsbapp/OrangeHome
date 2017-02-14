package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class BaseActivity extends Activity{

    private Context mContext;
    Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(new HsbOffLineListener() {
            @Override
            public void hsbOffLine() {
               startActivity(new Intent(BaseActivity.this,IndexActivity.class));
            }
        });
        super.onCreate(savedInstanceState);
       /* mContext = this;
        runnable = new Runnable() {
            @Override
            public void run() {
                // 用户5秒没操作了

                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(mContext, ScreenSaverActivity.class);
                mContext.startActivity(i);

            }
        };*/
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

       /* handler.postDelayed(runnable, 1000 * Constant.LOCK_SCREEN_TIME);*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(null);
        /*handler.removeCallbacks(runnable);*/
    }



  /*  public boolean onTouchEvent(android.view.MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: { // 手指下来的时候,取消之前绑定的Runnable
                Log.d("LOCK_SCREEN_TIME_DOMN", "LOCK_SCREEN_TIME");
                handler.removeCallbacks(runnable);
                break;
            }
            case MotionEvent.ACTION_UP: { // 手指离开屏幕，发送延迟消息 ，5秒后执行

                handler.postDelayed(runnable, 1000 * Constant.LOCK_SCREEN_TIME);

                break;
            }
        }
        return super.onTouchEvent(event);
    };*/



}
