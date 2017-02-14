package com.gmoonxs.www.orangehome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbDeviceCondition;
import com.cg.hsb.HsbDeviceState;
import com.cg.hsb.HsbScene;
import com.cg.hsb.HsbSceneAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 威 on 2016/7/24.
 */
public class SceneOperationAdapter  extends BaseAdapter {
    private ArrayList<HsbSceneAction> hsbSceneActions;
    private HsbScene hsbScene;
    private LayoutInflater inflater;
    private Context mContext;
    private Handler handler;
    private Context applicationContext;




    public SceneOperationAdapter(Context context,Context activityContext,HsbScene hsbScene){
        applicationContext=context;
        mContext=activityContext;
        inflater=LayoutInflater.from(context);
        this.hsbScene=hsbScene;
        hsbSceneActions=hsbScene.GetActionList();
    }

    public void setHsbScene(HsbScene hsbScene) {
        this.hsbScene = hsbScene;
        hsbSceneActions=hsbScene.GetActionList();
    }

    public void addData(List<HsbSceneAction> infos){
        if(null == infos){
            return;
        }
        for(int i = 0 ; i < infos.size(); i ++){
            this.hsbSceneActions.add(infos.get(i));
        }
    }

    @Override
    public int getCount() {
        if(null == hsbSceneActions){
            return 0;
        }
        return hsbSceneActions.size();
    }

    @Override
    public HsbSceneAction getItem(int position) {
        return hsbSceneActions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SceneOperationListViewHolder sceneOperationListViewHolder;
        View view = convertView;
        if (view == null) {
            view=inflater.inflate(R.layout.scene_operation_item, parent, false);
            sceneOperationListViewHolder=new SceneOperationListViewHolder();
            assert view!=null;
            sceneOperationListViewHolder.scence_action_item=(LinearLayout)view.findViewById(R.id.scence_action_item);
            sceneOperationListViewHolder.scene_action_item_delay=(TextView)view.findViewById(R.id.scene_action_item_delay);
            sceneOperationListViewHolder.scene_action_item_device_type=(ImageView)view.findViewById(R.id.scene_action_item_device_type);
            sceneOperationListViewHolder.scene_action_item_condition=(LinearLayout)view.findViewById(R.id.scene_action_item_condition);
            sceneOperationListViewHolder.scene_action_item_condition_name=(TextView)view.findViewById(R.id.scene_action_item_condition_name);
            sceneOperationListViewHolder.scene_action_item_condition_condition=(TextView)view.findViewById(R.id.scene_action_item_condition_condition);
            sceneOperationListViewHolder.scene_action_item_action_list=(ListView)view.findViewById(R.id.scene_action_item_action_list);
            sceneOperationListViewHolder.scene_action_item_setting=(ImageView)view.findViewById(R.id.scene_action_item_setting);
            sceneOperationListViewHolder.scene_action_item_delete=(ImageView)view.findViewById(R.id.scene_action_item_delete);
            sceneOperationListViewHolder.scene_action_item_up=(ImageView)view.findViewById(R.id.scene_action_item_up);
            sceneOperationListViewHolder.scene_action_item_down=(ImageView)view.findViewById(R.id.scene_action_item_down);
            view.setTag(sceneOperationListViewHolder);
        } else {
            sceneOperationListViewHolder = (SceneOperationListViewHolder) view.getTag();
        }
        final HsbSceneAction hsbSceneAction=hsbSceneActions.get(position);
        sceneOperationListViewHolder.scene_action_item_delay.setText(hsbSceneAction.GetDelay()+"秒");

        //类型图片
        if (hsbSceneAction!=null) {
            HsbDevice actionDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(hsbSceneAction.GetAction(0).GetDevID());
            switch (actionDevice.GetDevType()){
                case HsbConstant.HSB_DEV_TYPE_PLUG:{
                    sceneOperationListViewHolder.scene_action_item_device_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.scene_action_item_plug));
                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_STB_CC9201: {
                    sceneOperationListViewHolder.scene_action_item_device_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.scene_action_item_tv));
                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_GRAY_AC: {
                    sceneOperationListViewHolder.scene_action_item_device_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.scene_action_item_airconditioner));
                    break;
                }
                default:
                    break;
            }
            //操作执行条件
            HsbDeviceCondition hsbDeviceCondition=hsbSceneAction.GetCondition();
            if (hsbDeviceCondition==null){
                sceneOperationListViewHolder.scene_action_item_condition.setVisibility(View.GONE);
            }
            else {
                sceneOperationListViewHolder.scene_action_item_condition.setVisibility(View.VISIBLE);
                HsbDevice hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(hsbDeviceCondition.GetDevID());
                sceneOperationListViewHolder.scene_action_item_condition_name.setText(hsbDevice.GetName());
                String condition="";
                if (hsbDevice.GetState(hsbDeviceCondition.GetID()).GetType()== HsbDeviceState.TYPE_LIST){
                    condition=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetName();;
                    condition+=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetValDesc(hsbDeviceCondition.GetVal());

                }else if (hsbDevice.GetState(hsbDeviceCondition.GetID()).GetType()== HsbDeviceState.TYPE_INT){
                    condition=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetName();
                    condition+=Constant.DEVICE_CONDITION_EXP[hsbDeviceCondition.GetExp()];
                    condition+=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetValDesc(hsbDeviceCondition.GetVal());

                }
                sceneOperationListViewHolder.scene_action_item_condition_condition.setText(condition);

            }

            //操作动作列表
            SceneActionAdapter actionAdapter=new SceneActionAdapter(applicationContext,mContext);
            actionAdapter.setmActionList(hsbSceneAction.GetActionList());
            sceneOperationListViewHolder.scene_action_item_action_list.setAdapter(actionAdapter);

            sceneOperationListViewHolder.scene_action_item_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SceneActivity) mContext).scene_activity_edit_scene_linearlayout.setVisibility(View.GONE);
                    Intent intent = new Intent(((SceneActivity) mContext), SceneOperationActivity.class);
                    intent.putExtra("scene_action_id", position);
                    ((SceneActivity) mContext).startActivityForResult(intent, 0);
                }
            });

            sceneOperationListViewHolder.scene_action_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mContext).setTitle("系统提示")//设置对话框标题

                            .setMessage("确定删除操作?")//设置显示的内容

                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮


                                @Override

                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                    // devList.remove(position);
                                    OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
                                    hsbSceneActions.remove(position);
                                    SceneOperationAdapter.this.notifyDataSetChanged();
                                }

                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮


                        @Override

                        public void onClick(DialogInterface dialog, int which) {//响应事件

                            // TODO Auto-generated method stub


                        }

                    }).show();//在按键响应事件中显示此对话框
                }
            });
            sceneOperationListViewHolder.scene_action_item_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position!=0){
                        HsbSceneAction hsbSceneActionTemp=hsbSceneActions.get(position-1);
                        hsbSceneActions.set(position - 1, hsbSceneActions.get(position));
                        hsbSceneActions.set(position,hsbSceneActionTemp);
                        SceneOperationAdapter.this.notifyDataSetChanged();
                    }
                }
            });

            sceneOperationListViewHolder.scene_action_item_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position != (hsbSceneActions.size() - 1)) {
                        HsbSceneAction hsbSceneActionTemp = hsbSceneActions.get(position + 1);
                        hsbSceneActions.set(position + 1, hsbSceneActions.get(position));
                        hsbSceneActions.set(position, hsbSceneActionTemp);
                        SceneOperationAdapter.this.notifyDataSetChanged();
                    }
                }
            });


        }
        return view;
    }
}

class SceneOperationListViewHolder{
    LinearLayout scence_action_item;
    ImageView scene_action_item_device_type;
    TextView scene_action_item_delay;
    LinearLayout scene_action_item_condition;
    TextView scene_action_item_condition_name;
    TextView scene_action_item_condition_condition;
    ListView scene_action_item_action_list;
    ImageView scene_action_item_setting;
    ImageView scene_action_item_delete;
    ImageView scene_action_item_up;
    ImageView scene_action_item_down;
}
