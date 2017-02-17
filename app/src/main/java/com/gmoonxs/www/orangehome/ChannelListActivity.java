package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbDeviceListener;
import com.cg.hsb.HsbListener;
import com.cg.hsb.Protocol;
import com.cg.hsb.TVDevice;

import java.util.ArrayList;

public class ChannelListActivity extends BaseActivity {

    private ListView channel_list;
    private ChannelAdapter channelAdapter;
    private ImageView add_channel_button,delete_channel_button;
    int device_id;
    TVDevice cc9201;
    private static final int CHANNEL_GET=1;
    private Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        device_id=getIntent().getExtras().getInt("dev_id");
        cc9201=(TVDevice)OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(device_id);
        channelAdapter=new ChannelAdapter(this.getApplicationContext(),this,device_id);
        initView();
        handler=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHANNEL_GET:
                        getChannelList();
                        break;
                }
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_channel_list, menu);
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
        channel_list=(ListView)findViewById(R.id.channel_list);
        channel_list.setAdapter(channelAdapter);
        add_channel_button=(ImageView)findViewById(R.id.add_channel_button);
        delete_channel_button=(ImageView)findViewById(R.id.delete_channel_button);
        delete_channel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (channelAdapter.isDelete()) {
                    channelAdapter.setIsDelete(false);
                } else {
                    channelAdapter.setIsDelete(true);
                }
                channelAdapter.notifyDataSetChanged();
            }
        });
        add_channel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChannelDialog();
            }
        });
    }



    private void addChannelDialog(){
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.add_channel_dialog_layout, (ViewGroup) findViewById(R.id.dialog));

        new AlertDialog.Builder(this).setTitle("频道绑定")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String add_channel_dialog_id = ((EditText) layout.findViewById(R.id.add_channel_dialog_id)).getText().toString();
                        String add_channel_dialog_name = ((EditText) layout.findViewById(R.id.add_channel_dialog_name)).getText().toString();
                        if (Utils.isNumeric(add_channel_dialog_id)) {
                            Log.e("add_channel_dialog_name","--"+add_channel_dialog_name+"--");
                            cc9201.SetChannel(add_channel_dialog_name, Integer.parseInt(add_channel_dialog_id));
                            refreshChannel();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), getBaseContext().getResources().getText(R.string.add_channel_dialog_id_error), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void refreshChannel(){
        cc9201.SetListener(new HsbDeviceListener() {
            @Override
            public void onDeviceUpdated(HsbDevice device) {
                Message message = new Message();
                message.what = CHANNEL_GET;
                handler.sendMessage(message);
            }
        });
    }

    private void getChannelList(){
        channelAdapter.setHsbChannels(cc9201.GetChannelList());
        channelAdapter.notifyDataSetChanged();
    }

}
