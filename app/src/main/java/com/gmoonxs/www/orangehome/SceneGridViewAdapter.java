package com.gmoonxs.www.orangehome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cg.hsb.HsbScene;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 威 on 2016/7/28.
 */
public class SceneGridViewAdapter extends BaseAdapter {
    Context context;
    private ArrayList<HsbScene> hsbScenes;
    LayoutInflater layoutInflater;
    Context activityContext;
    public SceneGridViewAdapter(Context context,Context activityContext,ArrayList<HsbScene> hsbScenes) {
        this.hsbScenes = hsbScenes;
        this.context = context;
        this.activityContext=activityContext;
        layoutInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return hsbScenes.size();
    }

    @Override
    public HsbScene getItem(int position) {
        return hsbScenes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SceneGridViewAdapterHolder sceneGridViewAdapterHolder;
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.scene_gridview_item, null);
            sceneGridViewAdapterHolder=new SceneGridViewAdapterHolder();
            assert view!=null;
            sceneGridViewAdapterHolder.scene_gridview_scene_pic=(ImageView)view.findViewById(R.id.scene_gridview_scene_pic);
            sceneGridViewAdapterHolder.scene_gridview_scene_name=(TextView)view.findViewById(R.id.scene_gridview_scene_name);
            view.setTag(sceneGridViewAdapterHolder);
        } else {
            sceneGridViewAdapterHolder = (SceneGridViewAdapterHolder) view.getTag();
        }
        String sceneName=hsbScenes.get(position).GetName();
        if(sceneName.contains("居家")||sceneName.contains("在家")||sceneName.contains("回家")){
            sceneGridViewAdapterHolder.scene_gridview_scene_pic.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.back_home));
        }else if(sceneName.contains("外出")||sceneName.contains("车")||sceneName.contains("离家")){
            sceneGridViewAdapterHolder.scene_gridview_scene_pic.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.leave_home));
        }else if (sceneName.contains("睡")){
            sceneGridViewAdapterHolder.scene_gridview_scene_pic.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.sleep));
        } else if (sceneName.contains("会客")){
            sceneGridViewAdapterHolder.scene_gridview_scene_pic.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.meeting));
        }else if (sceneName.contains("夜")){
            sceneGridViewAdapterHolder.scene_gridview_scene_pic.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.get_up_night));
        }else {
            sceneGridViewAdapterHolder.scene_gridview_scene_pic.setImageDrawable(activityContext.getResources().getDrawable(R.drawable.more));
        }
        sceneGridViewAdapterHolder.scene_gridview_scene_name.setText(sceneName);
        /*if (hsbScenes.get(position).GetValid()==false){
        }*/
        return view;
    }
}

class SceneGridViewAdapterHolder{
    ImageView scene_gridview_scene_pic;
    TextView scene_gridview_scene_name;
}
