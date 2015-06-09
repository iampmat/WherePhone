package com.example.patrickmatherly1994.wherephone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

/*
Errors: If the input is too short, the error is caught before the service can be started. If the
phrase is not in our dictionary then the error is caught after the service starts. Therefore one
sharedpreference key can be used because both possible errors will not occur at the same time.
 */

public class WherePhoneActivity extends Activity {

    public static Intent rIntent;

    private Switch ioSwitch;

    private EditText etInput;
    private EditText etOutput;
    private static final float text_size = 30;

    private SeekBar seekBar;
    private int seekVal;

    public static String SettingStorage = "SavedData";
    SharedPreferences settingData;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_where_phone);

        initializeVariables();

        setTextSize();

        setSeekBarChangeListener();

        setSwitchChangeListener();

        setEditTextOnFocusChangeListener();

        populateAllSettings();

        setOnSPChangedListener();

    }

    public void setOnSPChangedListener() {
        settingData.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                populateAllSettings();
            }
        });
    }

    public void setEditTextOnFocusChangeListener() {
        etInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        etOutput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void setSeekBarChangeListener() {
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
    }

    public void setSwitchChangeListener() {
        ioSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    stopService(new Intent(getBaseContext(), WherePhoneService.class));

                    saveAllSettings(isChecked);  // auto-calls populateAllSettings(); checkError();

                    disableInput();

                    rIntent = new Intent(getBaseContext(), WherePhoneService.class);

                    startService(rIntent);

                } else if (!isChecked) {
                    stopService(rIntent);
                    saveAllSettings(isChecked);
                    enableInput();
                }
            }
        });
    }

    public void populateAllSettings() {
        checkError();
        settingData = getSharedPreferences(SettingStorage, 0);
        etInput.setText(settingData.getString("inputstring", "Hey where is my phone?"), TextView.BufferType.EDITABLE);
        etOutput.setText(settingData.getString("outputstring", ""), TextView.BufferType.EDITABLE);
        seekBar.setProgress(settingData.getInt("seekval", 0));
        ioSwitch.setChecked(settingData.getBoolean("isOn", false));
        if(ioSwitch.isChecked()) { disableInput(); }
    }

    public void saveAllSettings(boolean isChecked) {
        settingData = getSharedPreferences(SettingStorage, 0);
        SharedPreferences.Editor editor = settingData.edit();
        editor.putString("inputstring", etInput.getText().toString());
        editor.putString("outputstring", etOutput.getText().toString());
        editor.putInt("seekval", seekVal);
        editor.putBoolean("isOn", isChecked);
        editor.commit();
    }

    public void enableInput() {
        etInput.setFocusableInTouchMode(true);
        etOutput.setFocusableInTouchMode(true);
        seekBar.setEnabled(true);
    }

    public void disableInput() {
        etInput.setFocusable(false);
        etOutput.setFocusable(false);
        seekBar.setEnabled(false);
    }

    public void checkError() {
        settingData = getSharedPreferences(SettingStorage, 0);
        String error = settingData.getString("error", "");
        if(error != "") {
            makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            settingData = getBaseContext().getSharedPreferences(SettingStorage, 0);
            SharedPreferences.Editor editor = settingData.edit();
            editor.putString("error", "");
            editor.commit();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setTextSize() {
        etInput.setTextSize(text_size);
        etOutput.setTextSize(text_size);
    }

    public void initializeVariables() {
        etInput = (EditText) findViewById(R.id.et_input);
        etOutput = (EditText) findViewById(R.id.et_output);
        ioSwitch = (Switch) findViewById(R.id.ioSwitch);
        seekBar = (SeekBar) findViewById(R.id.vSeekBar);
    }

}
