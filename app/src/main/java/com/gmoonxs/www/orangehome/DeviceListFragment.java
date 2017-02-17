package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;

import android.os.Handler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView device_list;

    private OnFragmentInteractionListener mListener;

    private DeviceAdapter deviceAdapter;

    private ImageView delete_device_button,add_device_button,edit_device_button;

    private LinearLayout device_list_loading,device_list_ready;

    private Handler handler;

    private final static int CONNECTED = 1;
    private final static int REFRESH = 2;
    private final static int HSBOFFLINE = 3;

    private View view;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceListFragment newInstance(String param1, String param2) {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DeviceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceAdapter = new DeviceAdapter(this.getActivity().getApplicationContext(),this.getActivity(),this);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECTED:
                        deviceReady();
                        break;
                    case REFRESH:
                        deviceAdapter.notifyDataSetChanged();
                        break;
                    case HSBOFFLINE:
                        initView(view);
                        break;
                }
            }

        };
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(new HsbOffLineListener() {
            @Override
            public void hsbOffLine() {
                Message message = new Message();
                message.what = HSBOFFLINE;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_device_list, container, false);
        initView(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.deviceListFragmentInteraction(uri);
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
        public void deviceListFragmentInteraction(Uri uri);
    }

    private void initView(View view){
        device_list=(ListView)view.findViewById(R.id.device_list);
        device_list_loading=(LinearLayout)view.findViewById(R.id.device_list_loading);
        device_list_loading.setVisibility(View.VISIBLE);
        device_list_ready=(LinearLayout)view.findViewById(R.id.device_list_ready);
        device_list_ready.setVisibility(View.GONE);
        delete_device_button=(ImageView)view.findViewById(R.id.delete_device_button);
        delete_device_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceAdapter.isDelete()) {
                    deviceAdapter.setIsDelete(false);
                } else {
                    deviceAdapter.setIsDelete(true);
                }
                deviceAdapter.notifyDataSetChanged();
            }
        });

        edit_device_button=(ImageView)view.findViewById(R.id.edit_device_button);
        edit_device_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deviceAdapter.isEdit()){
                    deviceAdapter.setIsEdit(false);
                }else {
                    deviceAdapter.setIsEdit(true);
                }
                deviceAdapter.notifyDataSetChanged();
            }
        });

        add_device_button=(ImageView)view.findViewById(R.id.add_device_button);
        add_device_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                startActivityForResult(intent, 0);
            }


        });
        new Thread(new Runnable(){

            public void run(){
                try {
                    //当网关没有连接就绪 线程挂起
                    while ((!OrangeHomeApplication.getOrangeHomeApplication().isReady())||(!OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity())){
                        Thread.currentThread().sleep(1000);
                    }
                    Message message = new Message();
                    message.what = CONNECTED;
                    handler.sendMessage(message); //告诉主线程执行任务
                }catch (Exception e){

                }
            }

        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AddDeviceActivity.RESULT_CODE) {
            deviceAdapter.setDevList(OrangeHomeApplication.getOrangeHomeApplication().getDevList());
            device_list.setAdapter(deviceAdapter);
            refreshView();
        }
        if (resultCode==EditDeviceActivity.RESULT_CODE){
            deviceAdapter.setDevList(OrangeHomeApplication.getOrangeHomeApplication().getDevList());
            deviceAdapter.setIsEdit(false);
            device_list.setAdapter(deviceAdapter);
            deviceAdapter.notifyDataSetChanged();
        }
    }


    private void deviceReady(){
        device_list_loading.setVisibility(View.GONE);
        device_list_ready.setVisibility(View.VISIBLE);
        deviceAdapter.setDevList(OrangeHomeApplication.getOrangeHomeApplication().getDevList());
        deviceAdapter.notifyDataSetChanged();
        device_list.setAdapter(deviceAdapter);

        refreshView();
    }

    private void refreshView(){
        new Thread(new Runnable(){
            public void run(){
                try {
                    while (OrangeHomeApplication.getOrangeHomeApplication().isReady()){
                        Thread.currentThread().sleep(2000);
                        Message message = new Message();
                        message.what = REFRESH;
                        handler.sendMessage(message); //告诉主线程执行任务
                    }
                }catch (Exception e){

                }
            }

        }).start();
    }

    @Override
    public void onStart(){

        super.onStart();
    }

    @Override
    public void onResume(){
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(new HsbOffLineListener() {
            @Override
            public void hsbOffLine() {
                Message message = new Message();
                message.what = HSBOFFLINE;
                handler.sendMessage(message);
            }
        });
        if ((!OrangeHomeApplication.getOrangeHomeApplication().isReady())||(!OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity())){
            if(OrangeHomeApplication.getOrangeHomeApplication().getHsbOffLineListener()!=null){
                OrangeHomeApplication.getOrangeHomeApplication().getHsbOffLineListener().hsbOffLine();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(null);
        super.onPause();
    }

}
