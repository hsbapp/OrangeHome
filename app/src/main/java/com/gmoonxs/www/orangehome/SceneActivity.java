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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbListener;
import com.cg.hsb.HsbScene;
import com.cg.hsb.HsbSceneListener;

public class SceneActivity extends BaseActivity {


    TextView scence_activity_title;
    ListView scene_activity_operation_list;
    ImageView scene_activity_confirm_button,scene_activity_cancel_button,scene_activity_enter_scene_button,scene_activity_delete_scene_button;
    LinearLayout scene_activity_edit_scene_linearlayout;
    HsbScene hsbScene;
    int intentType;
    private Handler handler;
    private static final int SET_SCENE=1;
    private static final int ENTER_SCENE=2;
    private static final int DEL_SCENE=3;
    public static final int RESULT_CODE=1;
    SceneOperationAdapter sceneOperationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        init();

        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scence, menu);
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

    private void init(){
        hsbScene=OrangeHomeApplication.getOrangeHomeApplication().getMiddleHsbScene();
        intentType=getIntent().getExtras().getInt("type");
        handler=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SET_SCENE:
                        setSuccess();
                        break;
                    case ENTER_SCENE:
                        enterScene();
                        break;
                    case DEL_SCENE:
                        delScene();
                        break;
                }
            }
        };
    }


    private void initView(){

        scence_activity_title=(TextView)findViewById(R.id.scence_activity_title);
        scene_activity_operation_list=(ListView)findViewById(R.id.scene_activity_operation_list);
        scene_activity_confirm_button=(ImageView)findViewById(R.id.scene_activity_confirm_button);
        scene_activity_cancel_button=(ImageView)findViewById(R.id.scene_activity_cancel_button);
        scene_activity_enter_scene_button=(ImageView)findViewById(R.id.scene_activity_enter_scene_button);
        scene_activity_delete_scene_button=(ImageView)findViewById(R.id.scene_activity_delete_scene_button);
        scene_activity_edit_scene_linearlayout=(LinearLayout)findViewById(R.id.scene_activity_edit_scene_linearlayout);
        if (intentType==SceneFragment.ADD){
            scene_activity_edit_scene_linearlayout.setVisibility(View.GONE);
        }else if (intentType==SceneFragment.EDIT){
            scene_activity_edit_scene_linearlayout.setVisibility(View.VISIBLE);
            scene_activity_enter_scene_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OrangeHomeApplication.getOrangeHomeApplication().getmProto().EnterScene(hsbScene.GetName());
                    OrangeHomeApplication.getOrangeHomeApplication().getmProto().SetSceneListener(new HsbSceneListener() {
                        @Override
                        public void onEnterSceneResult(int errcode) {
                            if (errcode == HsbConstant.HSB_E_OK) {
                                Message message = new Message();
                                message.what = ENTER_SCENE;
                                handler.sendMessage(message); //告诉主线程执行任务
                            } else {
                            }
                        }

                    });
                }
            });
            scene_activity_delete_scene_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SceneActivity.this);
                    builder.setMessage("确认删除吗?");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OrangeHomeApplication.getOrangeHomeApplication().getmProto().DelScene(hsbScene.GetName());
                            OrangeHomeApplication.getOrangeHomeApplication().getmProto().SetSceneListener(new HsbSceneListener() {
                                @Override
                                public void onDelSceneResult(int errcode) {
                                    if (errcode == HsbConstant.HSB_E_OK) {
                                        Message message = new Message();
                                        message.what = DEL_SCENE;
                                        handler.sendMessage(message); //告诉主线程执行任务
                                    } else {
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();


                }
            });
        }


        scence_activity_title.setText(hsbScene.GetName());
        sceneOperationAdapter=new SceneOperationAdapter(this.getApplicationContext(),SceneActivity.this,hsbScene);
        scene_activity_operation_list.setAdapter(sceneOperationAdapter);
        LayoutInflater inflater = getLayoutInflater();
        View footView=inflater.inflate(R.layout.scene_operation_item_footer, null);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scene_activity_edit_scene_linearlayout.setVisibility(View.GONE);
                Intent intent=new Intent(SceneActivity.this,SceneOperationActivity.class);
                // OrangeHomeApplication.getOrangeHomeApplication().setMiddleHsbScene(hsbScene);
                startActivityForResult(intent, 0);
            }
        });
        scene_activity_operation_list.addFooterView(footView, null, false);

        scene_activity_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OrangeHomeApplication.getOrangeHomeApplication().isSceneEdit()){
                    OrangeHomeApplication.getOrangeHomeApplication().getmProto().SetScene(hsbScene);
                    OrangeHomeApplication.getOrangeHomeApplication().getmProto().SetSceneListener(new HsbSceneListener() {
                        @Override
                        public void onSetSceneResult(int errcode) {
                            if (errcode == HsbConstant.HSB_E_OK) {
                                Message message = new Message();
                                message.what = SET_SCENE;
                                handler.sendMessage(message); //告诉主线程执行任务
                            } else {
                            }
                        }

                    });
                }
                else {
                    Intent intent = new Intent(SceneActivity.this, SceneFragment.class);
                    setResult(RESULT_CODE, intent);
                    SceneActivity.this.finish();
                }

            }
        });
        scene_activity_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SceneActivity.this.finish();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == SceneOperationActivity.RESULT_CODE) {
            hsbScene=OrangeHomeApplication.getOrangeHomeApplication().getMiddleHsbScene();

            sceneOperationAdapter.setHsbScene(hsbScene);

            sceneOperationAdapter.notifyDataSetChanged();
        }
    }

    private void setSuccess(){
        if(intentType==SceneFragment.ADD){
            Toast.makeText(SceneActivity.this,getBaseContext().getResources().getText(R.string.scence_activity_add_scene_success),Toast.LENGTH_LONG).show();
        }
        if(intentType==SceneFragment.EDIT){
            Toast.makeText(SceneActivity.this,getBaseContext().getResources().getText(R.string.scence_activity_edit_scene_success),Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(SceneActivity.this, SceneFragment.class);
        setResult(RESULT_CODE, intent);
        SceneActivity.this.finish();
    }

    private void enterScene(){
        OrangeHomeApplication.getOrangeHomeApplication().addToLogsDB(getBaseContext().getResources().getText(R.string.log_info_enter_scene)+hsbScene.GetName());
        Toast.makeText(SceneActivity.this,getBaseContext().getResources().getText(R.string.scence_activity_enter_scene_success)+hsbScene.GetName(),Toast.LENGTH_LONG).show();
    }

    private void delScene(){
        Toast.makeText(SceneActivity.this,getBaseContext().getResources().getText(R.string.scence_activity_del_scene_success)+hsbScene.GetName(),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SceneActivity.this, SceneFragment.class);
        setResult(RESULT_CODE, intent);
        SceneActivity.this.finish();
    }



}
