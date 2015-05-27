package com.example.patrickmatherly1994.wherephone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class BootCompleteIntentReceiver extends BroadcastReceiver {
    private static String SettingStorage = "SavedData";
    SharedPreferences settingData;

    public BootCompleteIntentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("IT STARTED!");
        // Start service if getChecked is true
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            settingData = context.getSharedPreferences(SettingStorage, 0);
            boolean isOn = settingData.getBoolean("isOn", false);
            System.out.println("THE SWITCH IS SET TO: " + isOn);
            if(isOn){
                Intent pushIntent = new Intent(context, WherePhoneService.class);

                String input = settingData.getString("inputstring", "Hey where is my phone?");
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
