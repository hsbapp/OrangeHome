package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;

public class CurtainRemoteCActivity extends Activity {

    private SeekBar mSeekBar;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain_remote_c);
        init();
    }

    private void init(){
        mSeekBar=(SeekBar)findViewById(R.id.curtain_remote_c_seek_bar);
        mEditText=(EditText)findViewById(R.id.curtain_remote_edit_text);
        mSeekBar.setProgress(0);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int p;
                mEditText.setText("" + (progress));// 80为进度条滑到最小值时代表的数值
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int p = 0;
                try {
                    p = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    p = 0;
                }

                if(p<0 || p>100) {
                    mEditText.setText("窗帘拉开范围范围：0~100");
                    mSeekBar.setProgress(0);
                } else {
                    mEditText.setText("");
                    mSeekBar.setProgress(p);
                }
            }
        });


    }
}
