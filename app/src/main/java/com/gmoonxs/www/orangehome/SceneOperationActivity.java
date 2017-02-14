package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbDeviceAction;
import com.cg.hsb.HsbDeviceCondition;
import com.cg.hsb.HsbDeviceState;
import com.cg.hsb.HsbScene;
import com.cg.hsb.HsbSceneAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SceneOperationActivity extends BaseActivity {

    private static final int ADD_NEW_ACTION=-1;
    private static final int ADD_NEW_CONDITION=-1;
    private static final int EDIT_CONDITION=1;
    public static final int RESULT_CODE=1;

    HsbSceneAction hsbSceneAction=new HsbSceneAction();
    HsbSceneAction initHsbSceneAction;
    private ArrayList<HsbDeviceAction> hsbDeviceActionArrayList;

    boolean isEdit=false;

    boolean isChanged=false;

    private ArrayList<HsbDevice> hsbDevices;

    private List<SpinnerData> actionDeviceSpinner,conditionDeviceSpinner;

    SceneActionAdapter actionAdapter;

    HsbScene hsbScene;

    HsbDeviceCondition hsbDeviceCondition;


    LinearLayout scence_operation_activity_add_oreration_condition_linearlayout,scence_operation_activity_add_oreration_action_linearlayout;
    LinearLayout scence_operation_activity_add_oreration_condition_device_linearlayout,scence_operation_activity_add_oreration_condition_state_linearlayout,
            scence_operation_activity_condition_val_int_linearlayout,scence_operation_activity_condition_val_list_linearlayout,scence_operation_activity_add_oreration_condition_button_linearlayout,
            scence_operation_activity_add_oreration_action_device_linearlayout,scence_operation_activity_add_oreration_action_state_linearlayout,
            scence_operation_activity_action_val_int_linearlayout,scence_operation_activity_action_val_list_linearlayout,scence_operation_activity_add_oreration_action_button_linearlayout;

    EditText scence_operation_activity_oreration_delay,scence_operation_activity_condition_edit_val,scence_operation_activity_action_edit_val;

    TextView scence_activity_title,scence_operation_activity_oreration_condition,scence_operation_activity_action_unit,add_edit_oreration_action,add_edit_oreration_condition,scence_operation_activity_condition_unit;

    ListView scence_operation_activity_oreration_action_list;

    Spinner scence_operation_activity_condition_select_device,scence_operation_activity_condition_select_state,scence_operation_activity_condition_select_exp,scence_operation_activity_condition_select_val;
    Spinner scence_operation_activity_action_select_device,scence_operation_activity_action_select_state,scence_operation_activity_action_select_val;

    ImageView scence_operation_activity_condition_add_condition,scence_operation_activity_condition_cancel,scence_operation_activity_action_add_action;
    ImageView scence_operation_activity_action_cancel,scence_operation_activity_add_operation,scence_operation_activity_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_operation);
        init();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scene_operation, menu);
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
        if (getIntent().hasExtra("scene_action_id")){
            isEdit=true;
            hsbSceneAction=hsbScene.GetAction(getIntent().getExtras().getInt("scene_action_id"));
            initHsbSceneAction=new HsbSceneAction();
            ArrayList<HsbDeviceAction> actionArrayList=new ArrayList<>();
            for (HsbDeviceAction hsbDeviceAction:hsbSceneAction.GetActionList()){
                HsbDeviceAction hsbDeviceAction1=new HsbDeviceAction(hsbDeviceAction.GetDevID(),hsbDeviceAction.GetID(),hsbDeviceAction.GetParam1(),hsbDeviceAction.GetParam2(),hsbDeviceAction.GetFlag());
                actionArrayList.add(hsbDeviceAction1);
            }
            initHsbSceneAction.setmActionList(actionArrayList);
            HsbDeviceCondition hsbDeviceCondition1;
            if (hsbSceneAction.GetCondition()!=null){
                hsbDeviceCondition1=new HsbDeviceCondition(hsbSceneAction.GetCondition().GetDevID(),hsbSceneAction.GetCondition().GetID(),hsbSceneAction.GetCondition().GetVal(),hsbSceneAction.GetCondition().GetExp());

            }else {
                hsbDeviceCondition1=null;
            }
            initHsbSceneAction.SetCondition(hsbDeviceCondition1);
            initHsbSceneAction.SetDelay(hsbSceneAction.GetDelay());
        }
        else {
            isEdit=false;
        }
        hsbDeviceActionArrayList=hsbSceneAction.GetActionList();

        hsbDevices=OrangeHomeApplication.getOrangeHomeApplication().getDevList();
        hsbDeviceCondition=hsbSceneAction.GetCondition();
        actionDeviceSpinner=new ArrayList<SpinnerData>();
        actionDeviceSpinner.add(new SpinnerData(-1,getResources().getString(R.string.scence_operation_activity_select_device_defult)));
        conditionDeviceSpinner=new ArrayList<SpinnerData>();
        conditionDeviceSpinner.add(new SpinnerData(-1,getResources().getString(R.string.scence_operation_activity_select_device_defult)));


        for (HsbDevice hsbDevice:hsbDevices){
            if (hsbDevice.SupportCondition()){
                conditionDeviceSpinner.add(new SpinnerData(hsbDevice.GetDevId(),hsbDevice.GetName()));
            }
            if (hsbDevice.SupportAction()){
                actionDeviceSpinner.add(new SpinnerData(hsbDevice.GetDevId(),hsbDevice.GetName()));
            }
        }

    }

    private void initView(){
        scence_operation_activity_add_oreration_condition_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_condition_linearlayout);
        scence_operation_activity_add_oreration_action_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_action_linearlayout);

        scence_operation_activity_add_oreration_condition_device_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_condition_device_linearlayout);
        scence_operation_activity_add_oreration_condition_state_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_condition_state_linearlayout);
        scence_operation_activity_condition_val_int_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_condition_val_int_linearlayout);
        scence_operation_activity_condition_val_list_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_condition_val_list_linearlayout);
        scence_operation_activity_add_oreration_condition_button_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_condition_button_linearlayout);

        scence_operation_activity_add_oreration_action_device_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_action_device_linearlayout);
        scence_operation_activity_add_oreration_action_state_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_action_state_linearlayout);
        scence_operation_activity_action_val_int_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_action_val_int_linearlayout);
        scence_operation_activity_action_val_list_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_action_val_list_linearlayout);
        scence_operation_activity_add_oreration_action_button_linearlayout=(LinearLayout)findViewById(R.id.scence_operation_activity_add_oreration_action_button_linearlayout);


        scence_operation_activity_oreration_delay=(EditText)findViewById(R.id.scence_operation_activity_oreration_delay);
        scence_operation_activity_condition_edit_val=(EditText)findViewById(R.id.scence_operation_activity_condition_edit_val);
        scence_operation_activity_action_edit_val=(EditText)findViewById(R.id.scence_operation_activity_action_edit_val);

        scence_activity_title=(TextView)findViewById(R.id.scence_activity_title);
        scence_operation_activity_oreration_condition=(TextView)findViewById(R.id.scence_operation_activity_oreration_condition);
        scence_operation_activity_action_unit=(TextView)findViewById(R.id.scence_operation_activity_action_unit);
        add_edit_oreration_action=(TextView)findViewById(R.id.add_edit_oreration_action);
        add_edit_oreration_condition=(TextView)findViewById(R.id.add_edit_oreration_condition);
        scence_operation_activity_condition_unit=(TextView)findViewById(R.id.scence_operation_activity_condition_unit);

        scence_operation_activity_oreration_action_list = (ListView) findViewById(R.id.scence_operation_activity_oreration_action_list);

        scence_operation_activity_condition_select_device=(Spinner)findViewById(R.id.scence_operation_activity_condition_select_device);
        scence_operation_activity_condition_select_state=(Spinner)findViewById(R.id.scence_operation_activity_condition_select_state);
        scence_operation_activity_condition_select_exp=(Spinner)findViewById(R.id.scence_operation_activity_condition_select_exp);
        scence_operation_activity_condition_select_val=(Spinner)findViewById(R.id.scence_operation_activity_condition_select_val);
        scence_operation_activity_action_select_device=(Spinner)findViewById(R.id.scence_operation_activity_action_select_device);
        scence_operation_activity_action_select_state=(Spinner)findViewById(R.id.scence_operation_activity_action_select_state);
        scence_operation_activity_action_select_val=(Spinner)findViewById(R.id.scence_operation_activity_action_select_val);

        scence_operation_activity_condition_add_condition=(ImageView)findViewById(R.id.scence_operation_activity_condition_add_condition);
        scence_operation_activity_condition_cancel=(ImageView)findViewById(R.id.scence_operation_activity_condition_cancel);
        scence_operation_activity_action_add_action=(ImageView)findViewById(R.id.scence_operation_activity_action_add_action);
        scence_operation_activity_action_cancel=(ImageView)findViewById(R.id.scence_operation_activity_action_cancel);
        scence_operation_activity_add_operation=(ImageView)findViewById(R.id.scence_operation_activity_add_operation);
        scence_operation_activity_cancel=(ImageView)findViewById(R.id.scence_operation_activity_cancel);

        scence_operation_activity_add_oreration_condition_linearlayout.setVisibility(View.GONE);
        scence_operation_activity_add_oreration_action_linearlayout.setVisibility(View.GONE);


        scence_operation_activity_oreration_delay.setText(hsbSceneAction.GetDelay()+"");

        if (hsbDeviceCondition!=null){
            HsbDevice hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(hsbDeviceCondition.GetDevID());
            if(hsbDevice.GetState(hsbDeviceCondition.GetID()).GetType()==HsbDeviceState.TYPE_INT){
                setConditionText();
            }
            else if(hsbDevice.GetState(hsbDeviceCondition.GetID()).GetType()==HsbDeviceState.TYPE_LIST){
                setConditionText1();
            }
        }

        initViewOnClick();

    }

    private void initViewOnClick(){
        actionAdapter=new SceneActionAdapter(getApplicationContext(),SceneOperationActivity.this);
        actionAdapter.setmActionList(hsbDeviceActionArrayList);

        scence_operation_activity_oreration_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scence_operation_activity_add_oreration_condition_linearlayout.setVisibility(View.VISIBLE);
                scence_operation_activity_add_oreration_action_linearlayout.setVisibility(View.GONE);
                if (hsbDeviceCondition==null){
                    setConditionDevice(ADD_NEW_CONDITION);
                }else {
                    setConditionDevice(EDIT_CONDITION);
                }
            }
        });


        LayoutInflater inflater = getLayoutInflater();
        View footView=inflater.inflate(R.layout.scene_action_item_footer, null);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scence_operation_activity_add_oreration_condition_linearlayout.setVisibility(View.GONE);
                scence_operation_activity_add_oreration_action_linearlayout.setVisibility(View.VISIBLE);
                setActionDevice(ADD_NEW_ACTION);
            }
        });
        scence_operation_activity_oreration_action_list.setAdapter(actionAdapter);
        scence_operation_activity_oreration_action_list.addFooterView(footView, null, false);

        scence_operation_activity_oreration_action_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                scence_operation_activity_add_oreration_condition_linearlayout.setVisibility(View.GONE);
                scence_operation_activity_add_oreration_action_linearlayout.setVisibility(View.VISIBLE);
                setActionDevice(arg2);
            }
        });

        scence_operation_activity_add_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scence_operation_activity_oreration_delay.getText().toString()!=null&&!scence_operation_activity_oreration_delay.getText().toString().equals("")){
                    int delay = Integer.parseInt(scence_operation_activity_oreration_delay.getText().toString());
                    if (hsbDeviceActionArrayList.size() != 0 && delay <= 999 && delay >= 0) {
                        HsbSceneAction newHsbSceneAction = getHsbAction(delay);
                        if (isEdit) {
                            //isEdit=true;

                            judgeIsChanged(newHsbSceneAction);

                            hsbSceneAction = newHsbSceneAction;
                            hsbScene.GetActionList().get(getIntent().getExtras().getInt("scene_action_id")).SetDelay(delay);
                            Intent intent = new Intent(SceneOperationActivity.this, SceneActivity.class);
                            setResult(RESULT_CODE, intent);
                            SceneOperationActivity.this.finish();
                        } else {
                            OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
                            hsbScene.AddAction(newHsbSceneAction);
                            Intent intent = new Intent(SceneOperationActivity.this, SceneActivity.class);
                            setResult(RESULT_CODE, intent);
                            SceneOperationActivity.this.finish();
                        }
                    } else {
                        Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_add_operation_error), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_add_operation_delay_error), Toast.LENGTH_LONG).show();
                }

            }
        });

        scence_operation_activity_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SceneOperationActivity.this.finish();
            }
        });

    }

    private void setActionDevice(final int args){
        final int defaltPosition;
        int position=0;
        ArrayAdapter<SpinnerData> deviceNameAdapter=new ArrayAdapter<SpinnerData>(this,R.layout.my_spinner_item, actionDeviceSpinner);
        deviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scence_operation_activity_action_select_device.setAdapter(deviceNameAdapter);
        HsbDeviceAction hsbSceneAction;
        if (args==ADD_NEW_ACTION) {
            add_edit_oreration_action.setText(getBaseContext().getResources().getText(R.string.scence_operation_activity_add_oreration_action));
            scence_operation_activity_add_oreration_action_state_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_action_val_int_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_action_val_list_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_add_oreration_action_button_linearlayout.setVisibility(View.GONE);
        }else {
            add_edit_oreration_action.setText(getBaseContext().getResources().getText(R.string.scence_operation_activity_edit_oreration_action));
            scence_operation_activity_add_oreration_action_state_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_add_oreration_action_button_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_action_add_action.setImageDrawable(getResources().getDrawable(R.drawable.confirm_button));
            hsbSceneAction=hsbDeviceActionArrayList.get(args);
            int actionDeviceId=hsbSceneAction.GetDevID();
            for (int i=0;i<actionDeviceSpinner.size();i++){
                if (actionDeviceSpinner.get(i).getId()==actionDeviceId){
                    scence_operation_activity_action_select_device.setSelection(i,true);
                    position=i;
                }
            }
            setActionState(hsbSceneAction.GetID(), hsbSceneAction.GetParam1(), args);
        }
        defaltPosition=position;
        scence_operation_activity_action_select_device.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 != 0 && arg2 != defaltPosition) {

                    setActionState(ADD_NEW_ACTION, ADD_NEW_ACTION, args);
                }
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setActionState(int stateId,int val, final int args){
        final int defaltPosition;
        int position=0;
        scence_operation_activity_add_oreration_action_state_linearlayout.setVisibility(View.VISIBLE);
        ArrayList<SpinnerData> actionStateSpinner=new ArrayList<SpinnerData>();
        actionStateSpinner.add(new SpinnerData(-1,getResources().getString(R.string.scence_operation_activity_select_state_defult)));
        final HsbDevice hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(((SpinnerData)scence_operation_activity_action_select_device.getSelectedItem()).getId());
        ArrayList<HsbDeviceState> deviceStates=hsbDevice.GetState();
        for (HsbDeviceState hsbDeviceState:deviceStates){
            actionStateSpinner.add(new SpinnerData(hsbDeviceState.GetID(),hsbDeviceState.GetName()));
        }
        ArrayAdapter<SpinnerData> deviceStateAdapter=new ArrayAdapter<SpinnerData>(this,R.layout.my_spinner_item, actionStateSpinner);
        deviceStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scence_operation_activity_action_select_state.setAdapter(deviceStateAdapter);


        if (stateId!=ADD_NEW_ACTION) {
            for (int i=0;i<actionStateSpinner.size();i++){
                if (actionStateSpinner.get(i).getId()==stateId){

                    scence_operation_activity_action_select_state.setSelection(i,true);
                    position=i;
                }
            }

            setActionVal(hsbDevice.GetState(((SpinnerData) scence_operation_activity_action_select_state.getSelectedItem()).getId()), val, args);

        }
        else {
            scence_operation_activity_action_val_int_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_action_val_list_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_add_oreration_action_button_linearlayout.setVisibility(View.GONE);
        }

        defaltPosition=position;
        scence_operation_activity_action_select_state.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 != 0&&arg2!=defaltPosition) {

                    setActionVal(hsbDevice.GetState(((SpinnerData) scence_operation_activity_action_select_state.getSelectedItem()).getId()), ADD_NEW_ACTION, args);
                }
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setActionVal(final HsbDeviceState hsbDeviceState,int val, final int args){
        if (hsbDeviceState.GetType()==HsbDeviceState.TYPE_INT){
            scence_operation_activity_action_val_int_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_action_val_list_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_add_oreration_action_button_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_action_unit.setText(hsbDeviceState.GetUnit());
            if (val!=ADD_NEW_ACTION){
                scence_operation_activity_action_edit_val.setText(val+"");
            }
            scence_operation_activity_action_add_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String editVal = scence_operation_activity_action_edit_val.getText().toString();
                    int editValInt;
                    if (editVal != null && editVal != "") {
                        try {
                            editValInt = Integer.parseInt(editVal);
                            if (editValInt <= hsbDeviceState.getmMax() && editValInt >= hsbDeviceState.getmMin()&& ((SpinnerData)scence_operation_activity_action_select_device.getSelectedItem()).getId()!=-1
                                    && ((SpinnerData)scence_operation_activity_action_select_state.getSelectedItem()).getId()!=-1) {
                                if (args!=ADD_NEW_ACTION){
                                    hsbDeviceActionArrayList.get(args).Set(hsbDeviceState.MakeAction(editValInt));
                                    actionAdapter.notifyDataSetChanged();
                                }else {
                                    hsbDeviceActionArrayList.add(hsbDeviceState.MakeAction(editValInt));
                                    actionAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_select_val_error), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_select_val_error), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_select_val_error), Toast.LENGTH_LONG).show();
                    }

                }
            });
            scence_operation_activity_action_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scence_operation_activity_add_oreration_action_linearlayout.setVisibility(View.GONE);
                }
            });
        }
        else if (hsbDeviceState.GetType()==HsbDeviceState.TYPE_LIST){
            scence_operation_activity_action_val_list_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_action_val_int_linearlayout.setVisibility(View.GONE);
            ArrayList<SpinnerData> actionValSpinner=new ArrayList<SpinnerData>();
            ArrayList<HsbDeviceState.StateVal>stateVals=hsbDeviceState.GetValList();
            for(HsbDeviceState.StateVal stateVal:stateVals){
                actionValSpinner.add(new SpinnerData(stateVal.mVal,stateVal.mDesc));
            }

            ArrayAdapter<SpinnerData> deviceValAdapter=new ArrayAdapter<SpinnerData>(this,R.layout.my_spinner_item, actionValSpinner);
            deviceValAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            scence_operation_activity_action_select_val.setAdapter(deviceValAdapter);

            if (val!=ADD_NEW_ACTION){
                for (int i=0;i<actionValSpinner.size();i++){
                    if (actionValSpinner.get(i).getId()==val){
                        scence_operation_activity_action_select_val.setSelection(i,true);

                    }
                }
            }


            scence_operation_activity_action_select_val.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    scence_operation_activity_add_oreration_action_button_linearlayout.setVisibility(View.VISIBLE);
                    scence_operation_activity_action_add_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (((SpinnerData) scence_operation_activity_action_select_device.getSelectedItem()).getId() != -1 && ((SpinnerData) scence_operation_activity_action_select_state.getSelectedItem()).getId() != -1) {
                                if (args!=ADD_NEW_ACTION){
                                    hsbDeviceActionArrayList.get(args).Set(hsbDeviceState.MakeAction(((SpinnerData) scence_operation_activity_action_select_val.getSelectedItem()).getId()));
                                    actionAdapter.notifyDataSetChanged();
                                }else {
                                    hsbDeviceActionArrayList.add(hsbDeviceState.MakeAction(((SpinnerData) scence_operation_activity_action_select_val.getSelectedItem()).getId()));
                                    actionAdapter.notifyDataSetChanged();
                                }
                            }

                        }
                    });
                    arg0.setVisibility(View.VISIBLE);
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                    arg0.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private HsbSceneAction getHsbAction(int delay){
        HsbSceneAction newHsbSceneAction=new HsbSceneAction();
        newHsbSceneAction.setmActionList(hsbDeviceActionArrayList);
        newHsbSceneAction.SetCondition(hsbDeviceCondition);
        newHsbSceneAction.SetDelay(delay);
        return newHsbSceneAction;
    }

    private void setConditionDevice(final int args){
        final int defaltPosition;
        int position=0;
        ArrayAdapter<SpinnerData> deviceNameAdapter=new ArrayAdapter<SpinnerData>(this,R.layout.my_spinner_item, conditionDeviceSpinner);
        deviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scence_operation_activity_condition_select_device.setAdapter(deviceNameAdapter);
        if (args==ADD_NEW_CONDITION) {
            add_edit_oreration_condition.setText(getBaseContext().getResources().getText(R.string.scence_operation_activity_add_oreration_condition));
            scence_operation_activity_add_oreration_condition_state_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_condition_val_int_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_condition_val_list_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_add_oreration_condition_button_linearlayout.setVisibility(View.GONE);
        }

        if(args==EDIT_CONDITION){
            add_edit_oreration_condition.setText(getBaseContext().getResources().getText(R.string.scence_operation_activity_edit_oreration_condition));
            scence_operation_activity_add_oreration_condition_state_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_add_oreration_condition_button_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_condition_add_condition.setImageDrawable(getResources().getDrawable(R.drawable.confirm_button));
            int conditionDeviceId=hsbDeviceCondition.GetDevID();
            for (int i=0;i<conditionDeviceSpinner.size();i++){
                if (conditionDeviceSpinner.get(i).getId()==conditionDeviceId){
                    scence_operation_activity_condition_select_device.setSelection(i,true);
                    position=i;
                }
            }
            setConditionState(args);
        }
        defaltPosition=position;
        scence_operation_activity_condition_select_device.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 != 0 && arg2 != defaltPosition) {
                    setConditionState(args);
                }
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setConditionState(final int args){
        final int defaltPosition;
        int position=0;
        scence_operation_activity_add_oreration_condition_state_linearlayout.setVisibility(View.VISIBLE);
        ArrayList<SpinnerData> conditionStateSpinner=new ArrayList<SpinnerData>();
        conditionStateSpinner.add(new SpinnerData(-1, getResources().getString(R.string.scence_operation_activity_select_state_defult)));
        final HsbDevice hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(((SpinnerData)scence_operation_activity_condition_select_device.getSelectedItem()).getId());
        ArrayList<HsbDeviceState> deviceStates=hsbDevice.GetState();
        for (HsbDeviceState hsbDeviceState:deviceStates){
            conditionStateSpinner.add(new SpinnerData(hsbDeviceState.GetID(),hsbDeviceState.GetName()));
        }
        ArrayAdapter<SpinnerData> deviceStateAdapter=new ArrayAdapter<SpinnerData>(this,R.layout.my_spinner_item, conditionStateSpinner);
        deviceStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scence_operation_activity_condition_select_state.setAdapter(deviceStateAdapter);
        if (args!=ADD_NEW_ACTION) {
            for (int i=0;i<conditionStateSpinner.size();i++){
                if (conditionStateSpinner.get(i).getId()==hsbDeviceCondition.GetID()){
                    scence_operation_activity_condition_select_state.setSelection(i,true);
                    position=i;
                }
            }

            setConditionVal(hsbDevice.GetState(((SpinnerData) scence_operation_activity_condition_select_state.getSelectedItem()).getId()), args);

        }
        else {
            scence_operation_activity_condition_val_int_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_condition_val_list_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_add_oreration_condition_button_linearlayout.setVisibility(View.GONE);
        }

        defaltPosition=position;
        scence_operation_activity_condition_select_state.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 != 0&&arg2!=defaltPosition) {
                    setConditionVal(hsbDevice.GetState(((SpinnerData) scence_operation_activity_condition_select_state.getSelectedItem()).getId()), args);
                }
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setConditionVal(final HsbDeviceState hsbDeviceState,final int args){
        if (hsbDeviceState.GetType()==HsbDeviceState.TYPE_INT){
            scence_operation_activity_condition_val_int_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_condition_val_list_linearlayout.setVisibility(View.GONE);
            scence_operation_activity_add_oreration_condition_button_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_condition_unit.setText(hsbDeviceState.GetUnit());
            List<String> conditionExp=java.util.Arrays.asList(Constant.DEVICE_CONDITION_EXP);
            ArrayAdapter<String> conditionExpAdapter=new ArrayAdapter<String>(this,R.layout.my_spinner_item, conditionExp);
            conditionExpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            scence_operation_activity_condition_select_exp.setAdapter(conditionExpAdapter);

            if (args!=ADD_NEW_CONDITION){
                scence_operation_activity_condition_edit_val.setText(hsbDeviceCondition.GetVal() + "");
                scence_operation_activity_condition_select_exp.setSelection(hsbDeviceCondition.GetExp());

            }
            scence_operation_activity_condition_add_condition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String editVal = scence_operation_activity_condition_edit_val.getText().toString();
                    int editValInt;
                    if (editVal != null && editVal != "") {
                        try {
                            editValInt = Integer.parseInt(editVal);
                            if (editValInt <= hsbDeviceState.getmMax() && editValInt >= hsbDeviceState.getmMin()&& ((SpinnerData)scence_operation_activity_condition_select_device.getSelectedItem()).getId()!=-1
                                    && ((SpinnerData)scence_operation_activity_condition_select_state.getSelectedItem()).getId()!=-1) {
                                int selectExp=scence_operation_activity_condition_select_exp.getSelectedItemPosition();
                                hsbDeviceCondition=hsbDeviceState.MakeCondition(editValInt,selectExp);
                                Log.d("hsbDeviceCondition",hsbDeviceCondition.GetDevID()+"  "+hsbDeviceCondition.GetID());
                                hsbSceneAction.SetCondition(hsbDeviceCondition);
                                setConditionText();
                            } else {

                                Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_select_val_error), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {

                            Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_select_val_error), Toast.LENGTH_LONG).show();
                        }
                    } else {

                        Toast.makeText(SceneOperationActivity.this, getBaseContext().getResources().getText(R.string.scence_operation_activity_select_val_error), Toast.LENGTH_LONG).show();
                    }

                }
            });
            scence_operation_activity_condition_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scence_operation_activity_add_oreration_condition_linearlayout.setVisibility(View.GONE);
                }
            });
        }
        else if (hsbDeviceState.GetType()==HsbDeviceState.TYPE_LIST){
            scence_operation_activity_condition_val_list_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_condition_val_int_linearlayout.setVisibility(View.GONE);
            ArrayList<SpinnerData> conditionValSpinner=new ArrayList<SpinnerData>();
            ArrayList<HsbDeviceState.StateVal>stateVals=hsbDeviceState.GetValList();
            for(HsbDeviceState.StateVal stateVal:stateVals){
                conditionValSpinner.add(new SpinnerData(stateVal.mVal,stateVal.mDesc));
            }

            ArrayAdapter<SpinnerData> deviceValAdapter=new ArrayAdapter<SpinnerData>(this,R.layout.my_spinner_item, conditionValSpinner);
            deviceValAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            scence_operation_activity_condition_select_val.setAdapter(deviceValAdapter);

            if (args!=ADD_NEW_CONDITION){
                for (int i=0;i<conditionValSpinner.size();i++){
                    if (conditionValSpinner.get(i).getId()==hsbDeviceCondition.GetVal()){
                        scence_operation_activity_condition_select_val.setSelection(i,true);
                    }
                }
            }

            scence_operation_activity_condition_select_val.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    arg0.setVisibility(View.VISIBLE);
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                    arg0.setVisibility(View.VISIBLE);
                }
            });

            scence_operation_activity_add_oreration_condition_button_linearlayout.setVisibility(View.VISIBLE);
            scence_operation_activity_condition_add_condition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((SpinnerData) scence_operation_activity_condition_select_device.getSelectedItem()).getId() != -1 && ((SpinnerData) scence_operation_activity_condition_select_state.getSelectedItem()).getId() != -1) {


                        hsbDeviceCondition=hsbDeviceState.MakeCondition(((SpinnerData) scence_operation_activity_condition_select_val.getSelectedItem()).getId(), 0);
                        Log.d("hsbDeviceCondition","val"+((SpinnerData) scence_operation_activity_condition_select_val.getSelectedItem()).getId());
                        hsbSceneAction.SetCondition(hsbDeviceCondition);
                        setConditionText1();
                    }
                }
            });
        }
    }

    private void setConditionText() {
        HsbDevice hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(hsbDeviceCondition.GetDevID());
        String conditionText= hsbDevice.GetName();
        conditionText+=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetName();
        conditionText+=Constant.DEVICE_CONDITION_EXP[hsbDeviceCondition.GetExp()];
        conditionText+=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetValDesc(hsbDeviceCondition.GetVal());
        conditionText+=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetUnit();
        scence_operation_activity_oreration_condition.setText(conditionText);
        scence_operation_activity_oreration_condition.setBackgroundColor(getResources().getColor(R.color.my_orange));
    }
    private void setConditionText1() {
        HsbDevice hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(hsbDeviceCondition.GetDevID());
        String conditionText= hsbDevice.GetName();
        conditionText+=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetName();
        conditionText+=hsbDevice.GetState(hsbDeviceCondition.GetID()).GetValDesc(hsbDeviceCondition.GetVal());
        scence_operation_activity_oreration_condition.setText(conditionText);
        scence_operation_activity_oreration_condition.setBackgroundColor(getResources().getColor(R.color.my_orange));
    }

    private void judgeIsChanged(HsbSceneAction newHsbSceneAction){

        if (initHsbSceneAction.GetDelay()!=newHsbSceneAction.GetDelay()){
            OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
            return;
        }

        //执行动作数不相等
        if (initHsbSceneAction.GetActionList().size()!=newHsbSceneAction.GetActionList().size()){
            OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
            return;
        }
        //新加了控制条件
        if (initHsbSceneAction.GetCondition()==null&&newHsbSceneAction.GetCondition()!=null){
            OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
            return;
        }
        //控制条件不相等
        if (initHsbSceneAction.GetCondition()!=null&&newHsbSceneAction.GetCondition()!=null){
            HsbDeviceCondition hsbDeviceCondition=initHsbSceneAction.GetCondition();
            HsbDeviceCondition newHsbDeviceCondition=newHsbSceneAction.GetCondition();
            if (!(hsbDeviceCondition.GetDevID()==newHsbDeviceCondition.GetDevID()&&hsbDeviceCondition.GetID()==newHsbDeviceCondition.GetID()&&hsbDeviceCondition.GetVal()==newHsbDeviceCondition.GetVal()&&hsbDeviceCondition.GetExp()==newHsbDeviceCondition.GetExp())){
                OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
                return;
            }
        }
        //执行动作不相等
        for (int i=0;i<initHsbSceneAction.GetActionList().size();i++){
            HsbDeviceAction hsbDeviceAction=initHsbSceneAction.GetActionList().get(i);
            HsbDeviceAction newHsbDeviceAction=newHsbSceneAction.GetActionList().get(i);
            if(hsbDeviceAction!=null&&newHsbDeviceAction!=null){
                if (!(hsbDeviceAction.GetID()==newHsbDeviceAction.GetID()&&hsbDeviceAction.GetDevID()==newHsbDeviceAction.GetDevID()&&hsbDeviceAction.GetParam1()==newHsbDeviceAction.GetParam1())){
                    OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
                    return;
                }
            }
        }




    }

}
