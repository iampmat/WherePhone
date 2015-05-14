package com.example.patrickmatherly1994.wherephone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class BootCompleteIntentReceiver extends BroadcastReceiver {
    private static String SettingStorage = "SavedData";
    SharedPreferences settingData;
    Context Pref;

    public BootCompleteIntentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        settingData = Pref.getSharedPreferences(SettingStorage, 0);
        boolean isOn = settingData.getBoolean("isOn", false);

        // Start service if getChecked is true
        if(isOn) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                SharedPreferences.Editor editor = settingData.edit();
                editor.putBoolean("isOn", true);
                editor.commit();

                Intent pushIntent = new Intent(context, WherePhoneService.class);

                String input = settingData.getString("inputstring", "Where is my phone?");
                String output = settingData.getString("outputstring", "");
                int seekVal = settingData.getInt("seekval", 0);

                pushIntent.putExtra("sInput", input.toString().toLowerCase().replaceAll("[^\\w\\s]", ""));
                pushIntent.putExtra("sOutput", output.toString().toLowerCase());
                pushIntent.putExtra("seekVal", seekVal);

                context.startService(pushIntent);
            }
        }
    }
}
