package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.SensorDevice;
import com.cg.hsb.SensorDeviceListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FirstPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Weather weatherNow;
    TextView weather_city_name,weather_update_time,state_board_display_aqi,state_board_display_quality,state_board_display_wendu,state_board_display_shidu;
    DialView dialView_2_5;
    TextView state_display_inside_pm25,state_display_inside_shidu,state_display_inside_wendu,state_display_inside_ranqi;
    ImageView lock_screen,enter_voice_recognition;
    //北京城市ID
    String cityCode="101010100";

    private OnFragmentInteractionListener mListener;

    LinearLayout state_board_display_loading,state_board_display_ready,state_board_display_scensor_ready;

    private Handler handler;

    private final static int CONNECTED=1;
    private final static int PM2_5_UPDATE=2;
    private final static int NOSENSOR=3;
    private final static int HSBOFFLINE=4;

    private int pm_2_5=0;
    private int wendu=0;
    private int shidu=0;
    private int ranqi=0;


    private int pm_2_51=10;


    LogsDB logsDB;
    private Cursor logsCursor;
    public LogsAdapter logsAdapter;


    ListView state_board_display_logs;

    boolean hasSensor=false;

    SensorListenerForFirstPageFragment sensorListenerForFirstPageFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstPageFragment newInstance(String param1, String param2) {
        FirstPageFragment fragment = new FirstPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FirstPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler=new Handler() {
            public void handleMessage(Message msg) {
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
                    case HSBOFFLINE:
                        state_board_display_ready.setVisibility(View.GONE);
                        state_board_display_loading.setVisibility(View.VISIBLE);
                        setDial1();
                        break;
                }
            }

        };
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        logsDB=OrangeHomeApplication.getOrangeHomeApplication().getLogsDB();
        OrangeHomeApplication.getOrangeHomeApplication().setSensorListenerForFirstPageFragment(new SensorListenerForFirstPageFragment() {
            @Override
            public void sensorOffLine() {
                Message message = new Message();
                message.what = NOSENSOR;
                handler.sendMessage(message);
            }

            @Override
            public void sensorOnLine() {
                Message message = new Message();
                message.what = CONNECTED;
                handler.sendMessage(message);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_page, container, false);
        initView(view);
        setDial();



        if (OrangeHomeApplication.getOrangeHomeApplication().getWeather()!=null){
            long difference=Calendar.getInstance().getTimeInMillis()-OrangeHomeApplication.getOrangeHomeApplication().getWeather().getUpadteTime().getTimeInMillis();
            long hour=difference/(3600*1000);
            if (hour>=2){
                weatherNow=new Weather();
                queryWeatherByCityCode(cityCode);
            }else {
                weatherNow=OrangeHomeApplication.getOrangeHomeApplication().getWeather();
                setWeatherPara(weatherNow);
            }

        }else {
            weatherNow=new Weather();
            queryWeatherByCityCode(cityCode);
        }

        setDial1();
        setLogsList();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.fistPageOnFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void fistPageOnFragmentInteraction(Uri uri);
    }


    private void initView(View view){
        state_board_display_loading=(LinearLayout)view.findViewById(R.id.state_board_display_loading);
        state_board_display_ready=(LinearLayout)view.findViewById(R.id.state_board_display_ready);
        weather_city_name=(TextView)view.findViewById(R.id.weather_city_name);
        weather_update_time=(TextView)view.findViewById(R.id.weather_update_time);
        state_board_display_aqi=(TextView)view.findViewById(R.id.state_board_display_aqi);
        state_board_display_quality=(TextView)view.findViewById(R.id.state_board_display_quality);
        state_board_display_wendu=(TextView)view.findViewById(R.id.state_board_display_wendu);
        state_board_display_shidu=(TextView)view.findViewById(R.id.state_board_display_shidu);
        dialView_2_5=(DialView)view.findViewById(R.id.dialView_2_5);
        state_board_display_logs=(ListView)view.findViewById(R.id.state_board_display_logs);
        state_board_display_scensor_ready=(LinearLayout)view.findViewById(R.id.state_board_display_scensor_ready);
        state_board_display_scensor_ready.setVisibility(View.GONE);
        state_display_inside_pm25=(TextView)view.findViewById(R.id.state_display_inside_pm25);
        state_display_inside_wendu=(TextView)view.findViewById(R.id.state_display_inside_wendu);
        state_display_inside_shidu=(TextView)view.findViewById(R.id.state_display_inside_shidu);
        state_display_inside_ranqi=(TextView)view.findViewById(R.id.state_display_inside_ranqi);
        lock_screen=(ImageView)view.findViewById(R.id.lock_screen);
        lock_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(getActivity(), ScreenSaverActivity.class);
                getActivity().startActivity(i);
                //OrangeHomeApplication.getOrangeHomeApplication().getmProto().Speaking("欢迎使用橙家智能");


            }
        });

        enter_voice_recognition=(ImageView)view.findViewById(R.id.enter_voice_recognition);
        enter_voice_recognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),VoiceRecognitionActivity.class);
                startActivity(i);

            }
        });
    }

    public void queryWeatherByCityCode(String cityCode){
        final String weatherAddress= Constant.GET_WEATHER_URL+cityCode;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(weatherAddress, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String infos = new String(bytes);
                WeatherXmlParse weatherXmlParse = new WeatherXmlParse();
                weatherNow = weatherXmlParse.parseWeatherXml(infos);
                weatherNow.setUpadteTime(Calendar.getInstance());
                OrangeHomeApplication.getOrangeHomeApplication().setWeather(weatherNow);
                setWeatherPara(weatherNow);

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (throwable.getMessage() != null) {
//                    Log.e("onFailure", throwable.getMessage());
                }
                Toast.makeText(getActivity().getApplicationContext(),
                        (String) getActivity().getBaseContext().getResources().getText(R.string.web_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setWeatherPara(Weather weatherNow){
        weather_city_name.setText(weatherNow.getCityName());
        weather_update_time.setText(getResources().getString(R.string.state_display_updatetime)+weatherNow.getUpdateTime());
        state_board_display_aqi.setText(weatherNow.getAqi());
        state_board_display_quality.setText(weatherNow.getQuality());
        state_board_display_wendu.setText(weatherNow.getWendu());
        //去除掉湿度符号
        state_board_display_shidu.setText(weatherNow.getShidu().substring(0, weatherNow.getShidu().length() - 1));
    }

    private void setDial(){
        if (OrangeHomeApplication.getOrangeHomeApplication().isReady()&&OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity()){
            state_board_display_ready.setVisibility(View.VISIBLE);
            state_board_display_loading.setVisibility(View.GONE);
            if (OrangeHomeApplication.getOrangeHomeApplication().getSensorDevice()!=null){
                SensorDevice sensorDevice=OrangeHomeApplication.getOrangeHomeApplication().getSensorDevice();
                pm_2_5 = sensorDevice.PM25Status();
                wendu=sensorDevice.TemperatureStatus();
                shidu=sensorDevice.HumidityStatus();
                ranqi=sensorDevice.GasStatus();
                dialUpdate();
            }

        }
    }

    private void setDial1(){
        new Thread(new Runnable(){
            public void run(){
                try {
                    //当网关没有连接就绪 线程挂起
                    while ((!OrangeHomeApplication.getOrangeHomeApplication().isReady())||(!OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity())){
                        Thread.currentThread().sleep(500);
                    }
                    Message message = new Message();
                    message.what = CONNECTED;
                    handler.sendMessage(message); //告诉主线程执行任务
                }catch (Exception e){

                }
            }

        }).start();
    }

    private void deviceReady() {
        state_board_display_loading.setVisibility(View.GONE);
        state_board_display_ready.setVisibility(View.VISIBLE);
        try {
            Thread.currentThread().sleep(1000);
        }
        catch (Exception e){
        }

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
                            OrangeHomeApplication.getOrangeHomeApplication().setSensorDevice(sensor);
                            //getPMOnline(sensor);
                            pm_2_5 = sensor.PM25Status();
                            wendu=sensor.TemperatureStatus();
                            shidu=sensor.HumidityStatus();
                            ranqi=sensor.GasStatus();
                            Message message = new Message();
                            message.what = PM2_5_UPDATE;
                            handler.sendMessage(message); //告诉主线程执行任务
                            sensor.SetListener(new SensorDeviceListener() {
                                @Override
                                public void onPm25StatusUpdated(int val) {
                                    Log.d("onPm25StatusUpdated", val + "");
                                    pm_2_5 = val;
                                    Message message = new Message();
                                    message.what = PM2_5_UPDATE;
                                    handler.sendMessage(message); //告诉主线程执行任务
                                }
                                public void onTempStatusUpdated(int val) {
                                    wendu=val;
                                    Message message = new Message();
                                    message.what = PM2_5_UPDATE;
                                    handler.sendMessage(message);
                                }

                                public void onHumidityStatusUpdated(int val) {
                                    shidu=val;
                                    Message message = new Message();
                                    message.what = PM2_5_UPDATE;
                                    handler.sendMessage(message);
                                }

                                public void onGasStatusUpdated(int val) {
                                    ranqi=val;
                                    Message message = new Message();
                                    message.what = PM2_5_UPDATE;
                                    handler.sendMessage(message);
                                }

                            });
                            break;
                        }
                        default:
                            break;

                    }
                }
                if (!hasSensor){
                    OrangeHomeApplication.getOrangeHomeApplication().setSensorDevice(null);
                    Message message = new Message();
                    message.what = NOSENSOR;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private void dialUpdate() {
        state_board_display_scensor_ready.setVisibility(View.VISIBLE);
        dialView_2_5.setBigDialDegrees((int) (pm_2_5 * 0.4));
        state_display_inside_pm25.setText(pm_2_5 + "");
        state_display_inside_wendu.setText(wendu+"");
        state_display_inside_shidu.setText(shidu + "");
        state_display_inside_ranqi.setText(ranqi + "");
        new Thread(new Runnable(){
            public void run(){
                try {
                    dialView_2_5.myDraw();
                }catch (Exception e){
                }
            }
        }).start();

    }

    private void setLogsList(){
        logsCursor = logsDB.select();
        state_board_display_logs.setAdapter(new LogsAdapter(getActivity(), logsCursor));

    }

    private void setNoSensor(){

        state_board_display_scensor_ready.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        OrangeHomeApplication.getOrangeHomeApplication().setIsFristPageMent(true);
        super.onResume();
        setDial1();

    }

    @Override
    public void onPause() {
        super.onPause();
        OrangeHomeApplication.getOrangeHomeApplication().setIsFristPageMent(false);
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(null);

    }

    @Override
    public void onDestroy() {
        OrangeHomeApplication.getOrangeHomeApplication().setIsFristPageMent(false);
        super.onDestroy();
    }

    @Override
    public void onStart(){
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(new HsbOffLineListener() {
            @Override
            public void hsbOffLine() {
                Message message = new Message();
                message.what = HSBOFFLINE;
                handler.sendMessage(message);
            }
        });
        super.onStart();
    }
}
