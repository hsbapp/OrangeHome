package com.gmoonxs.www.orangehome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbDeviceAction;
import com.cg.hsb.HsbSceneAction;

import java.util.ArrayList;

/**
 * Created by 威 on 2016/7/25.
 */
public class SceneActionAdapter extends BaseAdapter {
    private ArrayList<HsbDeviceAction> mActionList;
    private LayoutInflater inflater;
    private Context mContext;

    public SceneActionAdapter(Context context,Context activityContext){
        mContext=activityContext;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(null == mActionList){
            return 0;
        }
        return mActionList.size();
    }

    public ArrayList<HsbDeviceAction> getmActionList() {
        return mActionList;
    }

    public void setmActionList(ArrayList<HsbDeviceAction> mActionList) {
        this.mActionList = mActionList;
    }

    @Override
    public HsbDeviceAction getItem(int position) {
        return mActionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SceneActionListViewHolder sceneActionListViewHolder;
        View view = convertView;
        if (view == null) {
            view=inflater.inflate(R.layout.scene_action_item, parent, false);
            sceneActionListViewHolder=new SceneActionListViewHolder();
            assert view!=null;
            sceneActionListViewHolder.scene_action_item_action_name=(TextView)view.findViewById(R.id.scene_action_item_action_name);
            sceneActionListViewHolder.scene_action_item_action_action=(TextView)view.findViewById(R.id.scene_action_item_action_action);
            view.setTag(sceneActionListViewHolder);
        }else {
            sceneActionListViewHolder = (SceneActionListViewHolder) view.getTag();
        }
        HsbDeviceAction hsbDeviceAction=mActionList.get(position);
        HsbDevice hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(hsbDeviceAction.GetDevID());
        sceneActionListViewHolder.scene_action_item_action_name.setText(hsbDevice.GetName());

        String action=hsbDevice.GetState(hsbDeviceAction.GetID()).GetName();
        action+=hsbDevice.GetState(hsbDeviceAction.GetID()).GetValDesc(hsbDeviceAction.GetParam1());
        sceneActionListViewHolder.scene_action_item_action_action.setText(action);
        return view;
    }

}

class SceneActionListViewHolder{
    TextView scene_action_item_action_name;
    TextView scene_action_item_action_action;
}
