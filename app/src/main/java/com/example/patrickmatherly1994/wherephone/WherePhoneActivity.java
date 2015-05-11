package com.example.patrickmatherly1994.wherephone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;


public class WherePhoneActivity extends Activity {


    private Intent rIntent;

    private Switch ioSwitch;

    private EditText etInput;
    private EditText etOutput;

    private SeekBar seekBar;
    private int seekVal;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_where_phone);
        initializeVariables();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Set up Switch, beginRec when on, shutdown rec when off
        ioSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // Disable edittext and seekBar
                    etInput.setFocusable(false);
                    etOutput.setFocusable(false);
                    seekBar.setEnabled(false);

                    // Set up intent and attach keywords
                    rIntent = new Intent(getBaseContext(), WherePhoneService.class);

                    rIntent.putExtra("sInput", etInput.getText().toString().toLowerCase().replaceAll("[^\\w\\s]", ""));
                    rIntent.putExtra("sOutput", etOutput.getText().toString().toLowerCase());
                    rIntent.putExtra("seekVal", seekVal);

                    startService(rIntent);
                }
                else if(!isChecked) {
                    stopService(rIntent);

                    // Enable edittext and seekBar
                    etInput.setFocusableInTouchMode(true);
                    etOutput.setFocusableInTouchMode(true);
                    seekBar.setEnabled(true);
                }
            }
        });
    }

    public void initializeVariables() {
        etInput = (EditText) findViewById(R.id.et_input);
        etOutput = (EditText) findViewById(R.id.et_output);
        ioSwitch = (Switch) findViewById(R.id.ioSwitch);
        seekBar = (SeekBar) findViewById(R.id.vSeekBar);
    }


}
