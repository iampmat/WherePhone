package com.example.patrickmatherly1994.wherephone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;


public class WherePhoneActivity extends Activity {


    private Intent rIntent;

    private Switch ioSwitch;

    private EditText etInput;
    private EditText etOutput;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_where_phone);

        // init edittexts
        etInput = (EditText) findViewById(R.id.et_input);
        etOutput = (EditText) findViewById(R.id.et_output);

        // Set up Switch, beginRec when on, shutdown rec when off
        ioSwitch = (Switch) findViewById(R.id.ioSwitch);
        ioSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // Set up intent and attach keywords

                    rIntent = new Intent(getBaseContext(), WherePhoneService.class);
                    rIntent.putExtra("sInput", etInput.getText().toString().toLowerCase().replaceAll("[^\\w\\s]",""));
                    rIntent.putExtra("sOutput", etOutput.getText().toString().toLowerCase());
                    startService(rIntent);

                }
                else if(!isChecked) {
                    stopService(rIntent);
                }
            }
        });
    }


}
