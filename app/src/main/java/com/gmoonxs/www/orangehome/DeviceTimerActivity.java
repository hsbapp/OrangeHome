package com.gmoonxs.www.orangehome;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbDeviceAction;
import com.cg.hsb.HsbDeviceListener;
import com.cg.hsb.HsbDeviceState;
import com.cg.hsb.HsbDeviceTimer;
import com.gmoonxs.www.orangehome.R;
import com.gmoonxs.www.orangehome.datepicker.OnWheelScrollListener;
import com.gmoonxs.www.orangehome.datepicker.WheelView;
import com.gmoonxs.www.orangehome.datepicker.adapter.NumericWheelAdapter;

import java.util.Calendar;
import java.util.List;

public class DeviceTimerActivity extends BaseActivity {

    TextView device_timer_device_name,device_timer_activity_time,device_timer_activity_on_time,device_timer_activity_off_time;
    CheckBox device_timer_activity_timer_operation,device_timer_activity_timer_repeat;
    Spinner device_timer_activity_operation;
    ImageView device_timer_activity_right,device_timer_activity_on_time_right,device_timer_activity_off_time_right,device_timer_activity_confirm_button,device_timer_activity_cancel_button;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView min;
    private WheelView time;
    private ArrayAdapter<String> adapter;
    private static final int DATE_AND_TIME=1;
    private static final int ONLY_TIME_ON=2;
    private static final int ONLY_TIME_OFF=3;
    private List<String> device_operation= java.util.Arrays.asList(Constant.DEVICE_OPERATION);
    //为了选择方便 0是开 1是关
    private int deviceOperation=0;
    private HsbDevice hsbDevice;
    private Calendar dateTimeCalendar=Calendar.getInstance();
    private Calendar onTimeCalendar=Calendar.getInstance();
    private Calendar offTimeCalendar=Calendar.getInstance();
    private Handler handler;
    private static final int DATE_TIME_UPDATE=11;
    private static final int ON_TIME_UPDATE=12;
    private static final int OFF_TIME_UPDATE=13;

    private HsbDeviceTimer dateTimeTimer;
    private HsbDeviceTimer onTimeTimer;
    private HsbDeviceTimer offTimeTimer;

    //是否是修改之前的
    private boolean isModify=false;
    private boolean isChanged=false;

    private boolean hasDateTime=false;
    private boolean hasRepeat=false;

    private int on_off=0;

    OnWheelScrollListener scrollListenerDateTime = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {

        }
    };

    OnWheelScrollListener scrollListenerOnTime = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {

        }
    };

    OnWheelScrollListener scrollListenerOffTime = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_timer);
        handler=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DATE_TIME_UPDATE:
                        initDeviceTimerActivityOperation();
                        initDateTime(dateTimeTimer.mYear, dateTimeTimer.mMon, dateTimeTimer.mDay, dateTimeTimer.mHou, dateTimeTimer.mMin);
                        dateTimeCalendar.set(dateTimeTimer.mYear, dateTimeTimer.mMon - 1, dateTimeTimer.mDay, dateTimeTimer.mHou, dateTimeTimer.mMin);
                        setDateTimeSelectView();
                        device_timer_activity_timer_operation.setChecked(true);
                        hasDateTime=true;
                        break;
                    case ON_TIME_UPDATE:
                        initOnTime(onTimeTimer.mHou, onTimeTimer.mMin);
                        onTimeCalendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), onTimeTimer.mHou, onTimeTimer.mMin);
                        setOnTimeSelectView();
                        device_timer_activity_timer_repeat.setChecked(true);
                        hasRepeat=true;
                        break;
                    case OFF_TIME_UPDATE:
                        initOffTime(offTimeTimer.mHou, offTimeTimer.mMin);
                        offTimeCalendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), offTimeTimer.mHou, offTimeTimer.mMin);
                        setOffTimeSelectView();
                        device_timer_activity_timer_repeat.setChecked(true);
                        hasRepeat=true;
                        break;
                }
            }

        };
        init();
        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_timer, menu);
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
        Calendar c = Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        int mMonth=c.get(Calendar.MONTH)+1;//通过Calendar算出的月数要+1
        int mDay=c.get(Calendar.DATE);
        int mTime=c.get(Calendar.HOUR_OF_DAY);
        int mMin=c.get(Calendar.MINUTE);
        device_timer_device_name=(TextView)findViewById(R.id.device_timer_device_name);
        device_timer_activity_time=(TextView)findViewById(R.id.device_timer_activity_time);
        device_timer_activity_on_time=(TextView)findViewById(R.id.device_timer_activity_on_time);
        device_timer_activity_off_time=(TextView)findViewById(R.id.device_timer_activity_off_time);

        device_timer_activity_timer_operation=(CheckBox)findViewById(R.id.device_timer_activity_timer_operation);
        device_timer_activity_timer_repeat=(CheckBox)findViewById(R.id.device_timer_activity_timer_repeat);


        device_timer_activity_operation=(Spinner)findViewById(R.id.device_timer_activity_operation);

        device_timer_activity_right=(ImageView)findViewById(R.id.device_timer_activity_right);
        device_timer_activity_on_time_right=(ImageView)findViewById(R.id.device_timer_activity_on_time_right);
        device_timer_activity_off_time_right=(ImageView)findViewById(R.id.device_timer_activity_off_time_right);
        device_timer_activity_confirm_button=(ImageView)findViewById(R.id.device_timer_activity_confirm_button);
        device_timer_activity_cancel_button=(ImageView)findViewById(R.id.device_timer_activity_cancel_button);
        device_timer_activity_timer_operation.setChecked(false);
        device_timer_activity_timer_repeat.setChecked(false);

        hsbDevice.GetTimer(1);
        hsbDevice.GetTimer(2);
        hsbDevice.GetTimer(3);
        hsbDevice.SetListener(new HsbDeviceListener() {
            @Override
            public void onTimerUpdated(int id, HsbDeviceTimer timer) {
                if (id == 1 && timer.mActive) {
                    dateTimeTimer = timer;
                    Message message = new Message();
                    message.what = DATE_TIME_UPDATE;
                    handler.sendMessage(message);
                    isModify=true;
                }
                if (id == 2 && timer.mActive) {
                    onTimeTimer = timer;
                    Message message = new Message();
                    message.what = ON_TIME_UPDATE;
                    handler.sendMessage(message);
                    isModify=true;
                }
                if (id == 3 && timer.mActive) {
                    offTimeTimer = timer;
                    Message message = new Message();
                    message.what = OFF_TIME_UPDATE;
                    handler.sendMessage(message);
                    isModify=true;
                }
            }
        });
        initDateTime(mYear, mMonth, mDay, mTime, mMin);
        initOnTime(mTime, mMin);
        initOffTime(mTime, mMin);
        dateTimeCalendar.set(mYear, mMonth - 1, mDay, mTime, mMin);
        onTimeCalendar.set(mYear, mMonth - 1, mDay, mTime, mMin);
        onTimeCalendar.set(mYear, mMonth - 1, mDay, mTime, mMin);
        setDateTimeSelectView();
        setOnTimeSelectView();
        setOffTimeSelectView();

        adapter = new ArrayAdapter<String>(this,R.layout.my_spinner_item, device_operation);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        device_timer_activity_operation.setAdapter(adapter);
        device_timer_activity_operation.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                deviceOperation = arg2;
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });



        device_timer_activity_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTimer();
            }
        });

        device_timer_activity_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hsbDevice.SetListener(null);
                DeviceTimerActivity.this.finish();

            }
        });

    }

    private void initDateTimeSelect(final int select,int dYear,int dMonth,int dDate,int dHour,int dMin){
        final View view=getView(select,dYear,dMonth,dDate,dHour,dMin);
        new AlertDialog.Builder(this).setTitle("选择时间")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (select == DATE_AND_TIME) {
                            int n_year = year.getCurrentItem() + 2016;//年
                            int n_month = month.getCurrentItem() + 1;//月
                            int n_day = day.getCurrentItem() + 1;
                            int n_hour = time.getCurrentItem();
                            int n_min = min.getCurrentItem();
                            if (dateTimeCalendar.get(Calendar.YEAR) == n_year && dateTimeCalendar.get(Calendar.MONTH) == n_month - 1 && dateTimeCalendar.get(Calendar.DATE) == n_day && dateTimeCalendar.get(Calendar.HOUR_OF_DAY) == n_hour && dateTimeCalendar.get(Calendar.DATE) == n_min) {
                                isChanged = false;
                            } else {
                                isChanged = true;
                            }
                            dateTimeCalendar.set(n_year, n_month - 1, n_day, n_hour, n_min);
                            initDateTime(n_year, n_month, n_day, n_hour, n_min);
                        } else if (select == ONLY_TIME_ON) {
                            int n_hour = time.getCurrentItem();
                            int n_min = min.getCurrentItem();
                            if (onTimeCalendar.get(Calendar.HOUR_OF_DAY) == n_hour && onTimeCalendar.get(Calendar.MINUTE)==n_min) {
                                isChanged = false;
                            } else {
                                isChanged = true;
                            }
                            onTimeCalendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), n_hour, n_min);
                            initOnTime(n_hour, n_min);
                        } else if (select == ONLY_TIME_OFF) {
                            int n_hour = time.getCurrentItem();
                            int n_min = min.getCurrentItem();
                            if (offTimeCalendar.get(Calendar.HOUR_OF_DAY) == n_hour && offTimeCalendar.get(Calendar.MINUTE)==n_min) {
                                isChanged = false;
                            } else {
                                isChanged = true;
                            }
                            offTimeCalendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), n_hour, n_min);
                            initOffTime(n_hour, n_min);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private View getView(int select,int dYear,int dMonth,int dDate,int dHour,int dMin) {
        Calendar c = Calendar.getInstance();
        int norYear = 2200;
        LayoutInflater inflater = getLayoutInflater();
        int mYear,mMonth,mDay,mTime,mMin;
        final View view;
        if (dYear>2016&&dMonth>0&&dDate>0){
            mYear=dYear;
            mMonth=dMonth;
            mDay=dDate;
        }else {
            mYear=c.get(Calendar.YEAR);
            mMonth=c.get(Calendar.MONTH)+1;//通过Calendar算出的月数要+1
            mDay=c.get(Calendar.DATE);
        }

        if (dHour>=0&&dHour<25&&dMin>=0&&dMin<61){
            mTime=dHour;
            mMin=dMin;
        }
        else {
            mTime=c.get(Calendar.HOUR_OF_DAY);
            mMin=c.get(Calendar.MINUTE);
        }



        int curYear = mYear;
        int curMonth =mMonth;
        int curDate = mDay;

        view = inflater.inflate(R.layout.wheel_date_picker, (ViewGroup) findViewById(R.id.dialog));
        if (select==1){
            year = (WheelView) view.findViewById(R.id.year);
            NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(this,2016, norYear);
            numericWheelAdapter1.setLabel("年");
            year.setViewAdapter(numericWheelAdapter1);
            year.setCyclic(true);//是否可循环滑动
            year.addScrollingListener(scrollListenerDateTime);
            month = (WheelView) view.findViewById(R.id.month);
            NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(this, 1, 12, "%02d");
            numericWheelAdapter2.setLabel("月");
            month.setViewAdapter(numericWheelAdapter2);
            month.setCyclic(true);
            month.addScrollingListener(scrollListenerDateTime);

            day = (WheelView) view.findViewById(R.id.day);
            initDay(curYear, curMonth);
            day.setCyclic(true);
            year.setVisibleItems(7);//设置显示行数
            month.setVisibleItems(7);
            day.setVisibleItems(7);
            year.setCurrentItem(curYear - 2016);
            month.setCurrentItem(curMonth - 1);
            day.setCurrentItem(curDate - 1);
        }
        else {
            year = (WheelView) view.findViewById(R.id.year);
            month = (WheelView) view.findViewById(R.id.month);
            day = (WheelView) view.findViewById(R.id.day);
            year.setVisibility(View.GONE);
            month.setVisibility(View.GONE);
            day.setVisibility(View.GONE);
        }
        time = (WheelView) view.findViewById(R.id.time);
        NumericWheelAdapter numericWheelAdapter3=new NumericWheelAdapter(this, 0, 23, "%02d");
        numericWheelAdapter3.setLabel("时");
        time.setViewAdapter(numericWheelAdapter3);
        time.setCyclic(true);
        min = (WheelView) view.findViewById(R.id.min);
        NumericWheelAdapter numericWheelAdapter4=new NumericWheelAdapter(this, 0, 59, "%02d");
        numericWheelAdapter4.setLabel("分");
        min.setViewAdapter(numericWheelAdapter4);
        min.setCyclic(true);
        time.setVisibleItems(7);
        min.setVisibleItems(7);
        time.setCurrentItem(mTime);
        min.setCurrentItem(mMin);
        switch (select){
            case DATE_AND_TIME:
                year.addScrollingListener(scrollListenerDateTime);
                month.addScrollingListener(scrollListenerDateTime);
                day.addScrollingListener(scrollListenerDateTime);
                time.addScrollingListener(scrollListenerDateTime);
                min.addScrollingListener(scrollListenerDateTime);
                break;
            case ONLY_TIME_ON:
                time.addScrollingListener(scrollListenerOnTime);
                min.addScrollingListener(scrollListenerOnTime);
                break;
            case ONLY_TIME_OFF:
                time.addScrollingListener(scrollListenerOffTime);
                min.addScrollingListener(scrollListenerOffTime);
                break;
            default:
                break;
        }
        return view;
    }



    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(this,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }


    private void initDateTime(int year,int month,int date,int hour,int min){
        device_timer_activity_time.setText(year+"年"+month+"月"+date+"日"+hour+"时"+min+"分");

    }

    private void initOnTime(int hour,int min){
        device_timer_activity_on_time.setText(hour + "时" + min + "分");
    }

    private void initOffTime(int hour,int min){
        device_timer_activity_off_time.setText(hour + "时" + min + "分");
    }

    private void init(){
        int device_id=getIntent().getExtras().getInt("dev_id");
        hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(device_id);
    }

    private void confirmTimer(){
        boolean isTimerOperation=device_timer_activity_timer_operation.isChecked();
        boolean isRepeat=device_timer_activity_timer_repeat.isChecked();
        if (((!hasRepeat)&&isRepeat)||((!hasDateTime)&&isTimerOperation)||(hasRepeat&&(!isRepeat))||(hasDateTime&&(!isTimerOperation))){
            isChanged=true;
        }
        if(deviceOperation!=on_off){
            isChanged=true;
        }

        if (isModify==true&&isChanged==false){
            hsbDevice.SetListener(null);
            DeviceTimerActivity.this.finish();
        }else {
            if (isTimerOperation) {
                HsbDeviceTimer hsbDeviceTimer=new HsbDeviceTimer(hsbDevice.GetDevId());
                hsbDeviceTimer.SetDate(dateTimeCalendar.get(Calendar.YEAR), dateTimeCalendar.get(Calendar.MONTH) + 1, dateTimeCalendar.get(Calendar.DATE));
                hsbDeviceTimer.SetTime(dateTimeCalendar.get(Calendar.HOUR_OF_DAY), dateTimeCalendar.get(Calendar.MINUTE), 0);
                hsbDeviceTimer.SetWeekDay(HsbDeviceTimer.WEEKDAY_ONE_SHOT);
                hsbDeviceTimer.SetActive(true);
                if (deviceOperation==0){
                    HsbDeviceAction hsbDeviceAction=hsbDevice.MakePowerAction(true);
                    hsbDeviceTimer.SetAction(hsbDeviceAction);
                }
                else {
                    HsbDeviceAction hsbDeviceAction=hsbDevice.MakePowerAction(false);
                    hsbDeviceTimer.SetAction(hsbDeviceAction);
                }
                hsbDevice.SetTimer(1, hsbDeviceTimer);


            }else {
                if (isModify==true){
                    hsbDevice.DelTimer(1);
                }
            }
            if (isRepeat){
                HsbDeviceTimer hsbDeviceTimerOn=new HsbDeviceTimer(hsbDevice.GetDevId());
                hsbDeviceTimerOn.SetTime(onTimeCalendar.get(Calendar.HOUR_OF_DAY), onTimeCalendar.get(Calendar.MINUTE), 0);
                hsbDeviceTimerOn.SetWeekDay(HsbDeviceTimer.WEEKDAY_EVERY_DAY);
                hsbDeviceTimerOn.SetActive(true);
                hsbDeviceTimerOn.SetAction(hsbDevice.MakePowerAction(true));
                hsbDevice.SetTimer(2, hsbDeviceTimerOn);

                HsbDeviceTimer hsbDeviceTimerOff=new HsbDeviceTimer(hsbDevice.GetDevId());
                hsbDeviceTimerOff.SetTime(offTimeCalendar.get(Calendar.HOUR_OF_DAY), offTimeCalendar.get(Calendar.MINUTE), 0);
                hsbDeviceTimerOff.SetWeekDay(HsbDeviceTimer.WEEKDAY_EVERY_DAY);
                hsbDeviceTimerOff.SetActive(true);
                hsbDeviceTimerOff.SetAction(hsbDevice.MakePowerAction(false));
                hsbDevice.SetTimer(3, hsbDeviceTimerOff);
            }else {
                if (isModify){
                    hsbDevice.DelTimer(2);
                    hsbDevice.DelTimer(3);
                }

            }
            if(!((!isModify)&&(!isTimerOperation)&&(!isModify))){
                Toast.makeText(DeviceTimerActivity.this,getBaseContext().getResources().getText(R.string.add_timer_success),Toast.LENGTH_LONG).show();
            }
            hsbDevice.SetListener(null);
            DeviceTimerActivity.this.finish();

        }
    }

     private void setDateTimeSelectView(){
        device_timer_activity_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateTimeSelect(DATE_AND_TIME, dateTimeCalendar.get(Calendar.YEAR), dateTimeCalendar.get(Calendar.MONTH) + 1, dateTimeCalendar.get(Calendar.DATE), dateTimeCalendar.get(Calendar.HOUR_OF_DAY), dateTimeCalendar.get(Calendar.MINUTE));
            }
        });

        device_timer_activity_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateTimeSelect(DATE_AND_TIME, dateTimeCalendar.get(Calendar.YEAR), dateTimeCalendar.get(Calendar.MONTH) + 1, dateTimeCalendar.get(Calendar.DATE), dateTimeCalendar.get(Calendar.HOUR_OF_DAY), dateTimeCalendar.get(Calendar.MINUTE));

            }
        });



    }

    //private void setOnTimeSelectView(final int dYear,final int dMonth,final int dDate,final int dHour,final int dMin){
    private void setOnTimeSelectView(){
        device_timer_activity_on_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateTimeSelect(ONLY_TIME_ON, onTimeCalendar.get(Calendar.YEAR), onTimeCalendar.get(Calendar.MONTH) + 1, onTimeCalendar.get(Calendar.DATE), onTimeCalendar.get(Calendar.HOUR_OF_DAY), onTimeCalendar.get(Calendar.MINUTE));
            }
        });

        device_timer_activity_on_time_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateTimeSelect(ONLY_TIME_ON, onTimeCalendar.get(Calendar.YEAR), onTimeCalendar.get(Calendar.MONTH) + 1, onTimeCalendar.get(Calendar.DATE), onTimeCalendar.get(Calendar.HOUR_OF_DAY), onTimeCalendar.get(Calendar.MINUTE));
            }
        });
    }
    private void setOffTimeSelectView(){
        device_timer_activity_off_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateTimeSelect(ONLY_TIME_OFF, offTimeCalendar.get(Calendar.YEAR), offTimeCalendar.get(Calendar.MONTH) + 1, offTimeCalendar.get(Calendar.DATE), offTimeCalendar.get(Calendar.HOUR_OF_DAY), offTimeCalendar.get(Calendar.MINUTE));
            }
        });

        device_timer_activity_off_time_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateTimeSelect(ONLY_TIME_ON, offTimeCalendar.get(Calendar.YEAR), offTimeCalendar.get(Calendar.MONTH) + 1, offTimeCalendar.get(Calendar.DATE), offTimeCalendar.get(Calendar.HOUR_OF_DAY), offTimeCalendar.get(Calendar.MINUTE));
            }
        });
    }

    private void initDeviceTimerActivityOperation(){
        HsbDeviceAction hsbDeviceAction=dateTimeTimer.mAction;
        if(hsbDeviceAction!=null){
            HsbDeviceState state=hsbDevice.GetState(hsbDeviceAction.GetID());
            String desc=state.GetValDesc(hsbDeviceAction.GetParam1());
            if (desc.equals("关")){
                device_timer_activity_operation.setSelection(1);
                on_off=1;
            }else if (desc.equals("开")){
                device_timer_activity_operation.setSelection(0);
                on_off=0;
            }
        }

    }

    @Override
    protected void onStop(){
        hsbDevice.SetListener(null);
        super.onStop();
    }

    @Override
    protected void onRestart(){
        if (hsbDevice!=null){
            hsbDevice.GetTimer(1);
            hsbDevice.GetTimer(2);
            hsbDevice.GetTimer(3);
            hsbDevice.SetListener(new HsbDeviceListener() {
                @Override
                public void onTimerUpdated(int id, HsbDeviceTimer timer) {
                    if (id == 1 && timer.mActive) {

                        dateTimeTimer = timer;
                        Message message = new Message();
                        message.what = DATE_TIME_UPDATE;
                        handler.sendMessage(message);

                    }
                    if (id == 2 && timer.mActive) {
                        onTimeTimer = timer;
                        Message message = new Message();
                        message.what = ON_TIME_UPDATE;
                        handler.sendMessage(message);

                    }
                    if (id == 3 && timer.mActive) {
                        offTimeTimer = timer;
                        Message message = new Message();
                        message.what = OFF_TIME_UPDATE;
                        handler.sendMessage(message);

                    }
                }
            });
        }

        super.onRestart();
    }

}
