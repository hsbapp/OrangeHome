package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.hsb.HsbDevice;

import java.util.List;

public class EditDeviceActivity extends BaseActivity {
    private EditText edit_device_activity_device_name,edit_device_activity_device_location,edit_device_activity_device_note;
    private TextView edit_device_activity_device_type;
    private ImageView edit_device_activity_edit_device_button,edit_device_activity_cancel_button;
    private String deviceName,deviceLocation;
    public static final int RESULT_CODE=2;
    private HsbDevice hsbDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);
        hsbDevice=OrangeHomeApplication.getOrangeHomeApplication().getmProto().FindDevice(getIntent().getExtras().getInt("dev_id"));
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_device, menu);
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
        edit_device_activity_device_name=(EditText)findViewById(R.id.edit_device_activity_device_name);
        edit_device_activity_device_location=(EditText)findViewById(R.id.edit_device_activity_device_location);
        edit_device_activity_device_note=(EditText)findViewById(R.id.edit_device_activity_device_note);
        edit_device_activity_device_type=(TextView)findViewById(R.id.edit_device_activity_device_type);
        edit_device_activity_edit_device_button=(ImageView)findViewById(R.id.edit_device_activity_edit_device_button);
        edit_device_activity_cancel_button=(ImageView)findViewById(R.id.edit_device_activity_cancel_button);
        edit_device_activity_device_type.setText(hsbDevice.GetDevType());
        edit_device_activity_device_name.setText(hsbDevice.GetName());
        edit_device_activity_device_location.setText(hsbDevice.GetLocation());
        edit_device_activity_edit_device_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceName=edit_device_activity_device_name.getText().toString();
                deviceLocation=edit_device_activity_device_location.getText().toString();
                if (deviceName.length() < 0 || deviceName.length() > 20) {
                    Toast.makeText(EditDeviceActivity.this, getBaseContext().getResources().getText(R.string.add_device_activity_device_name_error), Toast.LENGTH_SHORT).show();
                } else if (deviceLocation.length() < 0 || deviceLocation.toString().length() > 20) {
                    Toast.makeText(EditDeviceActivity.this, getBaseContext().getResources().getText(R.string.add_device_activity_device_location_error), Toast.LENGTH_SHORT).show();
                } else {
                    hsbDevice.SetName(deviceName);
                    hsbDevice.SetLocation(deviceLocation);
                    Toast.makeText(EditDeviceActivity.this, getBaseContext().getResources().getText(R.string.edit_device_activity_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditDeviceActivity.this, DeviceListFragment.class);
                    setResult(RESULT_CODE, intent);
                    EditDeviceActivity.this.finish();
                }
            }
        });
        edit_device_activity_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDeviceActivity.this.finish();
            }
        });

    }
}
