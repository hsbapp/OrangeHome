package com.gmoonxs.www.orangehome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cg.hsb.CC9201;
import com.cg.hsb.CC9201Listener;
import com.cg.hsb.HsbConst;
import com.cg.hsb.HsbDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 威 on 2016/7/15.
 */
public class ChannelAdapter extends BaseAdapter {
    private ArrayList<HsbDevice.HsbChannel> hsbChannels=new ArrayList<>();
    private int devid;
    private LayoutInflater inflater;
    private Context mContext;
    private boolean isDelete=false;
    private CC9201 cc9201;
    private Handler handler;
    private static final int CHANNEL_GET=1;

    public ChannelAdapter(Context context,Context activityContext,int devid){
        mContext=activityContext;
        inflater=LayoutInflater.from(context);
        this.devid=devid;
        cc9201=(CC9201)OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(devid);
        handler=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHANNEL_GET:
                        getChannelList();
                        break;
                }
            }
        };
        init();
    }

    public ArrayList<HsbDevice.HsbChannel> getHsbChannels() {
        return hsbChannels;
    }

    public void setHsbChannels(ArrayList<HsbDevice.HsbChannel> hsbChannels) {
        this.hsbChannels = hsbChannels;
    }

    public void addData(List<HsbDevice.HsbChannel> infos){
        if(null == infos){
            return;
        }
        for(int i = 0 ; i < infos.size(); i ++){
            this.hsbChannels.add(infos.get(i));
        }
    }

    @Override
    public int getCount() {
        if(null == hsbChannels){
            return 0;
        }
        return hsbChannels.size();
    }

    @Override
    public HsbDevice.HsbChannel getItem(int position) {
        return hsbChannels.get(position);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChannelListViewHolder channelListViewHolder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.channel_item_layout, parent, false);
            channelListViewHolder=new ChannelListViewHolder();
            assert view!=null;
            channelListViewHolder.channel_list_item_channel_delete=(ImageView)view.findViewById(R.id.channel_list_item_channel_delete);
            channelListViewHolder.channel_list_item_channel_id=(TextView)view.findViewById(R.id.channel_list_item_channel_id);
            channelListViewHolder.channel_list_item_channel_name=(TextView)view.findViewById(R.id.channel_list_item_channel_name);
            channelListViewHolder.channel_list_item_channel_enter=(ImageView)view.findViewById(R.id.channel_list_item_channel_enter);
            view.setTag(channelListViewHolder);
        } else {
            channelListViewHolder = (ChannelListViewHolder) view.getTag();
        }
        if (!isDelete){
            channelListViewHolder.channel_list_item_channel_delete.setVisibility(View.GONE);
        }
        else{
            channelListViewHolder.channel_list_item_channel_delete.setVisibility(View.VISIBLE);
            channelListViewHolder.channel_list_item_channel_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mContext).setTitle("系统提示")//设置对话框标题

                            .setMessage("确定删除该频道绑定?")//设置显示的内容

                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮


                                @Override

                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                    cc9201.DelChannel(hsbChannels.get(position).GetName());
                                    ChannelAdapter.this.isDelete=false;
                                    ChannelAdapter.this.notifyDataSetChanged();
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

        final HsbDevice.HsbChannel hsbChannel=hsbChannels.get(position);
        channelListViewHolder.channel_list_item_channel_id.setText(hsbChannel.GetId()+"");
        channelListViewHolder.channel_list_item_channel_name.setText(hsbChannel.GetName()+"");
        channelListViewHolder.channel_list_item_channel_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc9201.SetChannelStatus(hsbChannel.GetId());
            }
        });
        return view;
    }

    private void init(){
        cc9201.SetListener(new CC9201Listener() {
            @Override
            public void onGetChannelResult(int errcode) {
                if (errcode == HsbConst.HSB_E_OK) {
                    Message message = new Message();
                    message.what = CHANNEL_GET;
                    handler.sendMessage(message); //告诉主线程执行任务
                } else {
                }
            }
        });
        cc9201.GetChannel();
    }

    private void getChannelList(){
        hsbChannels=cc9201.GetChannelList();
        this.notifyDataSetChanged();
    }
}



class ChannelListViewHolder {
    ImageView channel_list_item_channel_delete,channel_list_item_channel_enter;
    TextView channel_list_item_channel_id,channel_list_item_channel_name;
}