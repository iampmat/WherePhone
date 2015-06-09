package com.example.patrickmatherly1994.wherephone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by patrickmatherly1994 on 5/27/15.
 */
public class WherePhoneStartActivity extends Activity {

    private Button startButton;
    private TextView Input;
    private TextView Output;

    private static final float text_size = 50;

    public static String SettingStorage = "SavedData";
    SharedPreferences settingData;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_where_phone_start);

        initializeVariables();
        populateAllSettings();
        setTextSize();
    }

    public void populateAllSettings() {
        settingData = getSharedPreferences(SettingStorage, 0);
        Input.setText(settingData.getString("inputstring", "Hey where is my phone?"), TextView.BufferType.EDITABLE);
        Output.setText(settingData.getString("outputstring", ""), TextView.BufferType.EDITABLE);
    }

    public void initializeVariables() {
        startButton = (Button) findViewById(R.id.start_button);
        Input = (TextView) findViewById(R.id.input);
        Output = (TextView) findViewById(R.id.output);
    }

    public void setTextSize() {
        Input.setTextSize(text_size);
        Output.setTextSize(text_size);
    }

    public void startMain(View view) {
        Intent intent = new Intent(WherePhoneStartActivity.this,  WherePhoneActivity.class);
        startActivity(intent);
    }
}
