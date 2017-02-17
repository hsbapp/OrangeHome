package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cg.hsb.Protocol;

import java.util.List;
import java.util.regex.Pattern;

public class AddDeviceActivity extends BaseActivity {
    private List<String> device_type= java.util.Arrays.asList(Constant.DEVICE_TYPE);
    private EditText add_device_activity_device_name,add_device_activity_device_location,add_device_activity_device_ip,add_device_activity_device_note;
    private Spinner add_device_activity_device_type;
    private ImageView add_device_activity_add_device_button,add_device_activity_cancel_button;
    private ArrayAdapter<String> adapter;
    String deviceName,deviceLocation,ipAddress,deviceNote,deviceType;
    public static final int RESULT_CODE=1;
    private String selectTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_device, menu);
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
        add_device_activity_device_name=(EditText)findViewById(R.id.add_device_activity_device_name);
        add_device_activity_device_location=(EditText)findViewById(R.id.add_device_activity_device_location);
        add_device_activity_device_note=(EditText)findViewById(R.id.add_device_activity_device_note);
        add_device_activity_device_type=(Spinner)findViewById(R.id.add_device_activity_device_type);
        add_device_activity_add_device_button=(ImageView)findViewById(R.id.add_device_activity_add_device_button);
        add_device_activity_cancel_button=(ImageView)findViewById(R.id.add_device_activity_cancel_button);
        adapter = new ArrayAdapter<String>(this,R.layout.my_spinner_item, device_type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        add_device_activity_device_type.setAdapter(adapter);
        //赋初值
        deviceType=(String) add_device_activity_device_type.getSelectedItem();
        add_device_activity_device_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                deviceType=(String) add_device_activity_device_type.getSelectedItem();
                selectTypeId=Constant.IR_TYPE_ALL[arg2];
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });

        add_device_activity_add_device_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceName = add_device_activity_device_name.getText().toString();
                deviceLocation = add_device_activity_device_location.getText().toString();
                deviceNote = add_device_activity_device_note.getText().toString();
                if (deviceName.length() < 0 || deviceName.length() > 20) {
                    Toast.makeText(AddDeviceActivity.this, getBaseContext().getResources().getText(R.string.add_device_activity_device_name_error), Toast.LENGTH_SHORT).show();
                } else if (deviceLocation.length() < 0 || deviceLocation.toString().length() > 20) {
                    Toast.makeText(AddDeviceActivity.this, getBaseContext().getResources().getText(R.string.add_device_activity_device_location_error), Toast.LENGTH_SHORT).show();
                } else if (deviceNote.length() < 0 || deviceNote.toString().length() > 200) {
                    Toast.makeText(AddDeviceActivity.this, getBaseContext().getResources().getText(R.string.add_device_activity_device_note_error), Toast.LENGTH_SHORT).show();
                } else {
                    Protocol protocol = OrangeHomeApplication.getOrangeHomeApplication().getmProto();
                    //protocol.AddDevice(3, selectTypeId, hsbDeviceConfig);
                    protocol.AddDevice(selectTypeId, deviceName, deviceLocation);
                    Toast.makeText(AddDeviceActivity.this, getBaseContext().getResources().getText(R.string.add_device_activity_success), Toast.LENGTH_SHORT).show();
                    OrangeHomeApplication.getOrangeHomeApplication().addToLogsDB(getBaseContext().getResources().getText(R.string.log_info_add_device)+deviceName);
                    Intent intent = new Intent(AddDeviceActivity.this, DeviceListFragment.class);
                    setResult(RESULT_CODE, intent);
                    AddDeviceActivity.this.finish();
                }

            }
        });

        add_device_activity_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDeviceActivity.this.finish();
            }
        });
    }
}
