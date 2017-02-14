package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cg.hsb.CC9201;
import com.cg.hsb.GrayAirConditioner;
import com.cg.hsb.HsbConst;
import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbDeviceAction;
import com.cg.hsb.HsbListener;
import com.cg.hsb.HsbScene;
import com.cg.hsb.HsbSceneAction;
import com.cg.hsb.HsbSceneListener;
import com.cg.hsb.PlugDevice;
import com.cg.hsb.Protocol;
import com.cg.hsb.SensorDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 威 on 2016/7/3.
 */
public class DeviceAdapter extends BaseAdapter {
    private ArrayList<HsbDevice> devList;
    private LayoutInflater inflater;
    private Context mContext;
    private boolean isDelete=false;
    private boolean isEdit=false;
    private DeviceListFragment deviceListFragment;
    private Protocol protocol;
    private Handler handler;
    private final static int SCENES_GET=1;
    private final static int DEL_SCENE=2;
    private int deleteDevicePosition=-1;

    public DeviceAdapter(Context context,Context activityContext,DeviceListFragment deviceListFragment){
        mContext=activityContext;
        inflater=LayoutInflater.from(context);
        devList= new ArrayList<HsbDevice>();
        this.deviceListFragment=deviceListFragment;
        handler=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SCENES_GET:
                        deleteScenefromList(msg.getData().getInt("hsbDeviceId"));
                        break;
                    case DEL_SCENE:
                        delScene();
                        break;
                }
            }
        };
    }
    public void addData(List<HsbDevice> infos){
        if(null == infos){
            return;
        }
        for(int i = 0 ; i < infos.size(); i ++){
            this.devList.add(infos.get(i));
        }
    }

    public ArrayList<HsbDevice> getDevList() {
        return devList;
    }

    public void setDevList(ArrayList<HsbDevice> devList) {
        this.devList = devList;
    }

    @Override
    public int getCount() {
        if(null == devList){
            return 0;
        }
        return devList.size();
    }

    @Override
    public HsbDevice getItem(int position) {
        return devList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DeviceListViewHolder deviceListViewHolder;
        View view=convertView;
        if(view ==null){
            view=inflater.inflate(R.layout.device_item_layout,parent,false);
            deviceListViewHolder=new DeviceListViewHolder();
            assert view!=null;
            deviceListViewHolder.device_list_item_device_delete=(ImageView)view.findViewById(R.id.device_list_item_device_delete);
            deviceListViewHolder.device_list_item_device_edit=(ImageView)view.findViewById(R.id.device_list_item_device_edit);
            deviceListViewHolder.device_list_item_device_name=(TextView)view.findViewById(R.id.device_list_item_device_name);
            deviceListViewHolder.device_list_item_device_location=(TextView)view.findViewById(R.id.device_list_item_device_location);
            deviceListViewHolder.device_list_item_device_status=(TextView)view.findViewById(R.id.device_list_item_device_status);
            deviceListViewHolder.device_list_item_switch_power=(ToggleButton)view.findViewById(R.id.device_list_item_switch_power);
            deviceListViewHolder.device_list_item_timer=(ImageView)view.findViewById(R.id.device_list_item_timer);
            deviceListViewHolder.device_list_item_remote_control=(ImageView)view.findViewById(R.id.device_list_item_remote_control);
            view.setTag(deviceListViewHolder);
        } else{
            deviceListViewHolder= (DeviceListViewHolder) view.getTag();
        }
        if (!isDelete){
            deviceListViewHolder.device_list_item_device_delete.setVisibility(View.GONE);
        }
        else{
            deviceListViewHolder.device_list_item_device_delete.setVisibility(View.VISIBLE);
            deviceListViewHolder.device_list_item_device_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mContext).setTitle("系统提示")//设置对话框标题

                            .setMessage("确定删除设备? （与设备相关的情景模式也将一并删除）")//设置显示的内容

                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮


                                @Override

                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    DeviceAdapter.this.deleteDevicePosition=position;
                                    deleteRelatedSecne(devList.get(position).GetDevId());
                                    // TODO Auto-generated method stub
                                   // devList.remove(position);
                                }

                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮

                        @Override

                        public void onClick(DialogInterface dialog, int which) {//响应事件
                            // TODO Auto-generated method stub
                        }

                    }).show();//在按键响应事件中显示此对话框
                }
            });
        }
        if (!isEdit){
            deviceListViewHolder.device_list_item_device_edit.setVisibility(View.GONE);
        }
        else {
            deviceListViewHolder.device_list_item_device_edit.setVisibility(View.VISIBLE);
            deviceListViewHolder.device_list_item_device_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext,EditDeviceActivity.class);
                    intent.putExtra("dev_id",devList.get(position).GetDevId());
                    ((DeviceListFragment)deviceListFragment).startActivityForResult(intent, 1);
                }
            });
        }

        HsbDevice hsbDevice=devList.get(position);
        deviceListViewHolder.device_list_item_device_name.setText(hsbDevice.GetName());
        deviceListViewHolder.device_list_item_device_location.setText(hsbDevice.GetLocation());
        //填写状态栏和操作栏
        switch (hsbDevice.GetDevType()) {
            case HsbConstant.HSB_DEV_TYPE_PLUG:{

                final PlugDevice plug = (PlugDevice) hsbDevice;
                if( plug.PowerStatus()){
                    deviceListViewHolder.device_list_item_device_status.setText("开");
                    deviceListViewHolder.device_list_item_switch_power.setChecked(true);
                }
                else{
                    deviceListViewHolder.device_list_item_device_status.setText("关");
                    deviceListViewHolder.device_list_item_switch_power.setChecked(false);
                }
                deviceListViewHolder.device_list_item_switch_power.setVisibility(View.VISIBLE);
                deviceListViewHolder.device_list_item_remote_control.setVisibility(View.INVISIBLE);
                deviceListViewHolder.device_list_item_timer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, DeviceTimerActivity.class);
                        intent.putExtra("dev_id", plug.GetDevId());
                        mContext.startActivity(intent);
                    }
                });
                deviceListViewHolder.device_list_item_switch_power.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //如果状态为开
                        if (plug.PowerStatus()){
                            plug.SetPowerStatus(false);
                        }
                        else {
                            plug.SetPowerStatus(true);
                        }
                        DeviceAdapter.this.notifyDataSetChanged();
                    }
                });

                break;
            }
            case HsbConstant.HSB_DEV_TYPE_SENSOR: {
                final SensorDevice sensor = (SensorDevice) hsbDevice;
                deviceListViewHolder.device_list_item_device_status.setText("pm2.5:" + sensor.PM25Status());
                deviceListViewHolder.device_list_item_timer.setVisibility(View.INVISIBLE);
                deviceListViewHolder.device_list_item_remote_control.setVisibility(View.INVISIBLE);
                deviceListViewHolder.device_list_item_switch_power.setVisibility(View.INVISIBLE);
                break;
            }
            case HsbConstant.HSB_DEV_TYPE_STB_CC9201: {
                final CC9201 stb = (CC9201) hsbDevice;
                // get tv channel id
                deviceListViewHolder.device_list_item_timer.setVisibility(View.INVISIBLE);
                deviceListViewHolder.device_list_item_switch_power.setVisibility(View.INVISIBLE);
                deviceListViewHolder.device_list_item_remote_control.setVisibility(View.VISIBLE);
                deviceListViewHolder.device_list_item_device_status.setText("频道:"+stb.ChannelStatus()+"");
                deviceListViewHolder.device_list_item_remote_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, CC9201RemoteCActivity.class);
                        intent.putExtra("dev_id", stb.GetDevId());
                        mContext.startActivity(intent);
                    }
                });
                deviceListViewHolder.device_list_item_timer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, DeviceTimerActivity.class);
                        intent.putExtra("dev_id", stb.GetDevId());
                        mContext.startActivity(intent);
                    }
                });

                break;
            }
            case HsbConstant.HSB_DEV_TYPE_GRAY_AC: {


                final GrayAirConditioner ac = (GrayAirConditioner) hsbDevice;
                deviceListViewHolder.device_list_item_timer.setVisibility(View.VISIBLE);
                deviceListViewHolder.device_list_item_switch_power.setVisibility(View.VISIBLE);
                deviceListViewHolder.device_list_item_remote_control.setVisibility(View.VISIBLE);
                if (ac.WorkMode()>=0&&ac.WorkMode()<Constant.AC_WORK_MODE.length){
                    deviceListViewHolder.device_list_item_device_status.setText(Constant.AC_WORK_MODE[ac.WorkMode()]+":"+ac.Temperature()+"度");
                }
                if(ac.Power()==1){
                    deviceListViewHolder.device_list_item_switch_power.setChecked(true);
                }
                else if(ac.Power()==0){
                    deviceListViewHolder.device_list_item_switch_power.setChecked(false);
                }

                deviceListViewHolder.device_list_item_switch_power.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ac.SetPower(ac.Power() == 0 ? 1 : 0);
                        DeviceAdapter.this.notifyDataSetChanged();
                    }
                });

                deviceListViewHolder.device_list_item_remote_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, AirCRemoteCActivity.class);
                        intent.putExtra("dev_id", ac.GetDevId());
                        mContext.startActivity(intent);
                    }
                });

                deviceListViewHolder.device_list_item_timer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, DeviceTimerActivity.class);
                        intent.putExtra("dev_id", ac.GetDevId());
                        mContext.startActivity(intent);
                    }
                });

                break;


            }
            case HsbConstant.HSB_DEV_TYPE_REMOTE_CTL:
                // nothing to do
                deviceListViewHolder.device_list_item_device_status.setText("---");
                deviceListViewHolder.device_list_item_remote_control.setVisibility(View.GONE);
                deviceListViewHolder.device_list_item_timer.setVisibility(View.GONE);
                deviceListViewHolder.device_list_item_switch_power.setVisibility(View.GONE);
                break;
            default:
                deviceListViewHolder.device_list_item_device_status.setText("---");
                deviceListViewHolder.device_list_item_remote_control.setVisibility(View.GONE);
                deviceListViewHolder.device_list_item_timer.setVisibility(View.GONE);
                deviceListViewHolder.device_list_item_switch_power.setVisibility(View.GONE);
                break;
        }
        return view;

    }



    private boolean isHasRemoteCtl(String location){
        HsbDevice device = null;

        for (int id = 0; id < devList.size(); id++) {
            device = devList.get(id);
            switch (device.GetDevType()) {
                case HsbConstant.HSB_DEV_TYPE_REMOTE_CTL:
                    if (device.GetLocation().compareTo(location)==0){
                        return true;
                    }
                    break;
                default:
                    break;
            }

        }
        return false;
    }

    private void deleteRelatedSecne(final int hsbDeviceId){
        protocol=OrangeHomeApplication.getOrangeHomeApplication().getmProto();
        protocol.SetSceneListener(new HsbSceneListener() {
            @Override
            public void onGetSceneResult(int errcode) {
                if (errcode == HsbConst.HSB_E_OK) {
                    Message message = new Message();
                    message.what = SCENES_GET;
                    Bundle bundle = new Bundle();
                    bundle.putInt("hsbDeviceId", hsbDeviceId);
                    message.setData(bundle);
                    handler.sendMessage(message); //告诉主线程执行任务
                } else {
                }
            }

            @Override
            public void onDelSceneResult(int errcode) {
                if (errcode == HsbConst.HSB_E_OK) {
                    Message message = new Message();
                    message.what = DEL_SCENE;
                    handler.sendMessage(message); //告诉主线程执行任务
                } else {
                }
            }
        });
        protocol.GetScene();
    }

    private void deleteScenefromList(int hsbDeviceId){
        Protocol protocol=OrangeHomeApplication.getOrangeHomeApplication().getmProto();
        ArrayList<HsbScene> sceneList=protocol.GetSceneList();
        for(HsbScene hsbScene:sceneList){
            for (HsbSceneAction hsbSceneAction:hsbScene.GetActionList()){
                if(hsbSceneAction.GetCondition()!=null){
                    if (hsbSceneAction.GetCondition().GetDevID()==hsbDeviceId){
                        protocol.DelScene(hsbScene.GetName());
                        return;
                    }
                }

                for(HsbDeviceAction hsbDeviceAction:hsbSceneAction.GetActionList()){
                    if (hsbDeviceAction.GetDevID()==hsbDeviceId){
                        protocol.DelScene(hsbScene.GetName());
                        return;
                    }
                }
            }
        }
        if (this.deleteDevicePosition!=-1){
            OrangeHomeApplication.getOrangeHomeApplication().getmProto().DelDevice(devList.get(deleteDevicePosition).GetDevId());
            DeviceAdapter.this.isDelete=false;
            DeviceAdapter.this.notifyDataSetChanged();
            Toast.makeText(mContext,"删除成功！",Toast.LENGTH_LONG).show();
        }
    }

    private void delScene(){
        OrangeHomeApplication.getOrangeHomeApplication().getmProto().DelDevice(devList.get(deleteDevicePosition).GetDevId());
        DeviceAdapter.this.isDelete=false;
        DeviceAdapter.this.notifyDataSetChanged();
        Toast.makeText(mContext,"删除成功！",Toast.LENGTH_LONG).show();
    }
}

class DeviceListViewHolder{
    ImageView   device_list_item_device_delete;
    ImageView   device_list_item_device_edit;
    TextView    device_list_item_device_name;
    TextView    device_list_item_device_location;
    TextView	device_list_item_device_status;
    ToggleButton    device_list_item_switch_power;
    ImageView   device_list_item_timer;
    ImageView   device_list_item_remote_control;
}
