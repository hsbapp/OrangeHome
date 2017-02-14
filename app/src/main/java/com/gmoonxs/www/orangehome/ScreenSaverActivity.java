package com.gmoonxs.www.orangehome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.SensorDevice;
import com.cg.hsb.SensorDeviceListener;

import java.util.ArrayList;
import java.util.Date;


public class ScreenSaverActivity extends BaseActivity {

	private static PowerManager.WakeLock mWakeLock;
	private TextView tvTime, tvDate;// 显示日期的TextView

	public static int UPDATE_TIME = 0x234;// 更新时间
	private boolean isTime = true;// 是否允许更新时间

	private final static int CONNECTED=1;
	private final static int PM2_5_UPDATE=2;
	private final static int NOSENSOR=3;
	private int pm_2_5=0;
	private int wendu=0;
	private int shidu=0;
	private int ranqi=0;
	boolean hasSensor=false;
	LinearLayout lock_state_board_display_scensor_ready,lock_state_board_display_hsb;
	TextView lock_state_display_inside_pm25,lock_state_display_inside_wendu,lock_state_display_inside_shidu,lock_state_display_inside_ranqi;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (UPDATE_TIME == msg.what) {// 更新时间
				Date date = (Date) msg.obj;
				// 设置当前时间
				tvTime.setText(MyDateUtil.getChangeTimeFormat(date));
				Log.d("tvTimeDate",MyDateUtil.getChangeTimeFormat(date));
				// 设置日期
				tvDate.setText(MyDateUtil.getChangeDateFormat(date) + "\t"
						+ MyDateUtil.getChangeWeekFormat(date));
			}
			switch (msg.what) {
				case CONNECTED:
					deviceReady();
					break;
				case PM2_5_UPDATE:
					dialUpdate();
					break;
				case NOSENSOR:
					setNoSensor();
					break;
			}

		}
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_saver);
		PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
				PowerManager.SCREEN_DIM_WAKE_LOCK |
				PowerManager.ON_AFTER_RELEASE, "SimpleTimer");

		initView();
		setScene();
		setScene1();
		OrangeHomeApplication.getOrangeHomeApplication().setSensorListener(new SensorListener() {
			@Override
			public void sensorOffLine() {
				Message message = new Message();
				message.what = NOSENSOR;
				mHandler.sendMessage(message);
			}

			@Override
			public void sensorOnLine() {
				Message message = new Message();
				message.what = CONNECTED;
				mHandler.sendMessage(message);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		mWakeLock.acquire();
		isTime = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mWakeLock.release();
		isTime = false;
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {

			//监控/拦截菜单键
			finish();
		} else if(keyCode == KeyEvent.KEYCODE_HOME) {
			//由于Home键为系统键，此处不能捕获，需要重写onAttachedToWindow()

			finish();
			//return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		finish();
		return super.onTouchEvent(event);
	}
	//搜索键
	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub

		finish();
		return super.onSearchRequested();
	}


	/**
	 * 初始化视图
	 */

    private void initView() {
        tvTime = (TextView)findViewById(R.id.tv_time);
        tvDate = (TextView)findViewById(R.id.tv_date);
        lock_state_board_display_scensor_ready=(LinearLayout)findViewById(R.id.lock_state_board_display_scensor_ready);
        lock_state_board_display_hsb=(LinearLayout)findViewById(R.id.lock_state_board_display_hsb);
        lock_state_display_inside_pm25=(TextView)findViewById(R.id.lock_state_display_inside_pm25);
        lock_state_display_inside_wendu=(TextView)findViewById(R.id.lock_state_display_inside_wendu);
        lock_state_display_inside_shidu=(TextView)findViewById(R.id.lock_state_display_inside_shidu);
        lock_state_display_inside_ranqi=(TextView)findViewById(R.id.lock_state_display_inside_ranqi);
		getNewTime();
    }


	private void getNewTime() {
		new Thread() {
			public void run() {
				while (isTime) {
					Date date = new Date();
					Message msg = new Message();
					msg.obj = date;
					msg.what = UPDATE_TIME;
					mHandler.sendMessage(msg);
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	private void setScene(){
		if (OrangeHomeApplication.getOrangeHomeApplication().isReady()&&OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity()){
			lock_state_board_display_hsb.setVisibility(View.GONE);
		}
	}

	private void setScene1(){
		new Thread(new Runnable(){
			public void run(){
				try {
					//当网关没有连接就绪 线程挂起
					while ((!OrangeHomeApplication.getOrangeHomeApplication().isReady())||(!OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity())){
						Thread.currentThread().sleep(500);
					}
					Message message = new Message();
					message.what = CONNECTED;
					mHandler.sendMessage(message); //告诉主线程执行任务
				}catch (Exception e){

				}
			}

		}).start();
	}

	private void deviceReady() {



		new Thread(new Runnable() {
			public void run() {
				ArrayList<HsbDevice> hsbDevices = OrangeHomeApplication.getOrangeHomeApplication().getDevList();
				HsbDevice device = null;

				for (int id = 0; id < hsbDevices.size(); id++) {
					device = hsbDevices.get(id);
					switch (device.GetDevType()) {
						case HsbConstant.HSB_DEV_TYPE_SENSOR: {
							hasSensor=true;
							SensorDevice sensor = (SensorDevice) device;
							//getPMOnline(sensor);
							pm_2_5 = sensor.PM25Status();
							wendu=sensor.TemperatureStatus();
							shidu=sensor.HumidityStatus();
							ranqi=sensor.GasStatus();
							Message message = new Message();
							message.what = PM2_5_UPDATE;
							mHandler.sendMessage(message); //告诉主线程执行任务
							sensor.SetListener(new SensorDeviceListener() {
								@Override
								public void onPm25StatusUpdated(int val) {
									Log.d("onPm25StatusUpdated", val + "");
									pm_2_5 = val;
									Message message = new Message();
									message.what = PM2_5_UPDATE;
									mHandler.sendMessage(message); //告诉主线程执行任务
								}
								public void onTempStatusUpdated(int val) {
									wendu=val;
									Message message = new Message();
									message.what = PM2_5_UPDATE;
									mHandler.sendMessage(message);
								}

								public void onHumidityStatusUpdated(int val) {
									shidu=val;
									Message message = new Message();
									message.what = PM2_5_UPDATE;
									mHandler.sendMessage(message);
								}

								public void onGasStatusUpdated(int val) {
									ranqi=val;
									Message message = new Message();
									message.what = PM2_5_UPDATE;
									mHandler.sendMessage(message);
								}
							});
							break;
						}
						default:
							break;

					}
				}
				if (!hasSensor){
					Message message = new Message();
					message.what = NOSENSOR;
					mHandler.sendMessage(message);
				}
			}
		}).start();
	}

	private void dialUpdate() {
		lock_state_board_display_hsb.setVisibility(View.VISIBLE);
		lock_state_board_display_scensor_ready.setVisibility(View.VISIBLE);
		lock_state_display_inside_pm25.setText(pm_2_5+"");
		lock_state_display_inside_wendu.setText(wendu+"");
		lock_state_display_inside_shidu.setText(shidu + "");
		lock_state_display_inside_ranqi.setText(ranqi + "");
	}

	private void setNoSensor(){
		lock_state_board_display_scensor_ready.setVisibility(View.GONE);
		lock_state_board_display_hsb.setVisibility(View.GONE);
	}


}
