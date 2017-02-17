package com.gmoonxs.www.orangehome;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class IndexActivity extends FragmentActivity implements FirstPageFragment.OnFragmentInteractionListener,DeviceListFragment.OnFragmentInteractionListener,SceneFragment.OnFragmentInteractionListener
{
    private FirstPageFragment firstpageFragment;
    private DeviceListFragment deviceListFragment;
    private SceneFragment sceneFragment;
    private RelativeLayout function_tab_mainpage,function_tab_device,function_tab_scene,function_tab_setting;
    private FragmentManager contentFragmentManager;
    private int tab;
   /* private Runnable runnable;
    private Context mContext;
    Handler handler = new Handler();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        initView();
        if (getIntent().hasExtra("tab")){
            tab=getIntent().getExtras().getInt("tab");
            if (tab==2){
                transFragmentTo(deviceListFragment);
            }
        }

        OrangeHomeApplication.getOrangeHomeApplication().getSoundPool().play(1, 1, 1, 0, 0, 1);
      /*  mContext = this;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_index, menu);
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
        function_tab_mainpage=(RelativeLayout)findViewById(R.id.function_tab_mainpage);
        function_tab_device=(RelativeLayout)findViewById(R.id.function_tab_device);
        function_tab_scene=(RelativeLayout)findViewById(R.id.function_tab_scene);
        function_tab_setting=(RelativeLayout)findViewById(R.id.function_tab_setting);
        firstpageFragment = FirstPageFragment.newInstance("", "");
        deviceListFragment = DeviceListFragment.newInstance("","");
        sceneFragment=SceneFragment.newInstance("","");
        function_tab_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transFragmentTo(firstpageFragment);
            }
        });

        function_tab_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transFragmentTo(deviceListFragment);
            }
        });

        function_tab_scene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transFragmentTo(sceneFragment);
            }
        });


        contentFragmentManager=this.getSupportFragmentManager();
        transFragmentTo(firstpageFragment);

    }


    private void transFragmentTo(Fragment newFragment)
    {
       /* handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000 * Constant.LOCK_SCREEN_TIME);*/
        FragmentTransaction transaction = contentFragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout_content, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void fistPageOnFragmentInteraction(Uri uri) {

    }

    @Override
    public void deviceListFragmentInteraction(Uri uri){

    }

    @Override
    public void scenceFragmentInteraction(Uri uri){

    }


    /**
     * 返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, getBaseContext().getResources().getText(R.string.exit_reminder), Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //handler.postDelayed(runnable, 1000 * Constant.LOCK_SCREEN_TIME);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //handler.removeCallbacks(runnable);
        super.onPause();

    }



  /*  @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: { // 手指下来的时候,取消之前绑定的Runnable
                Log.d("LOCK_SCREEN_TIME_DOMN","LOCK_SCREEN_TIME");
                handler.removeCallbacks(runnable);
                break;
            }
            case MotionEvent.ACTION_UP: { // 手指离开屏幕，发送延迟消息 ，5秒后执行
                Log.d("LOCK_SCREEN_TIME_UP","LOCK_SCREEN_TIME");
                handler.postDelayed(runnable, 1000 * Constant.LOCK_SCREEN_TIME);

                break;
            }
        }
        return false;
        //return super.onTouchEvent(event);
    };*/

}
