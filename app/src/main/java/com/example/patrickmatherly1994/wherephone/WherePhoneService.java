package com.example.patrickmatherly1994.wherephone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class WherePhoneService extends Service implements RecognitionListener {

    private static String SettingStorage = "SavedData";
    SharedPreferences settingData;

    private SpeechRecognizer recognizer;

    private String sInput;
    private String sOutput;

    private int seekVal;
    private TextToSpeech reply;

    private AsyncTask t;


    public WherePhoneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        makeText(getApplicationContext(), "onHandle start", Toast.LENGTH_SHORT).show();

        getValues();

        startTTS();

        t = new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(WherePhoneService.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    //((TextView) findViewById(R.id.caption_text)).setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(sInput);
                }
            }
        }.execute();
        return Service.START_STICKY;
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        try {
            recognizer = defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                    .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                            // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                            //.setRawLogDir(assetsDir)

                            // Threshold to tune for keyphrase to balance between false alarms and misses
                    .setKeywordThreshold(1e-45f)

                            // Use context-independent phonetic search, context-dependent is too slow for mobile
                    .setBoolean("-allphone_ci", true)

                    .getRecognizer();
            recognizer.addListener(this);

            // Create keyword-activation search.
            recognizer.addKeyphraseSearch(sInput, sInput);
        } catch(Exception e) {
            notInDictError();
            stopSelf();
        }
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(sInput))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(sInput))
            switchSearch(sInput);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        makeText(getApplicationContext(), "Partial", Toast.LENGTH_SHORT).show();

        if (text.equals(sInput)) {

            setVolume();

            // Text to speech
            reply.speak(sOutput, TextToSpeech.QUEUE_ADD, null);

            switchSearch(sInput);
        }
        else {
            makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
            switchSearch(sInput);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            // restart listener and affirm that partial has past
            makeText(getApplicationContext(), "end", Toast.LENGTH_SHORT).show();
            //recognizer.startListening(sInput);
            switchSearch(sInput);
        }
    }


    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {
        switchSearch(sInput);
    }

    public void startTTS() {
        reply = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    reply.setLanguage(Locale.UK);
                }
            }
        });
    }

    public void getValues() {
        settingData = getBaseContext().getSharedPreferences(SettingStorage, 0);
        sInput = settingData.getString("inputstring", "Where is my phone").toString().toLowerCase().replaceAll("[^\\w\\s]", "");
        sOutput = settingData.getString("outputstring", "").toString().toLowerCase();
        seekVal = settingData.getInt("seekval", 0);
    }

    public void setVolume() {
        int seekValConvert = 0;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int getMaxPhoneVol = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);
        seekValConvert = ((seekVal * getMaxPhoneVol)/100);
        audioManager.setStreamVolume(audioManager.STREAM_MUSIC, seekValConvert, 0);
    }

    public void notInDictError() {
        settingData = getBaseContext().getSharedPreferences(SettingStorage, 0);
        SharedPreferences.Editor editor = settingData.edit();
        editor.putBoolean("isOn", false);
        editor.putString("error", "Your phrase can't be found in the dictionary");
        editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        makeText(getApplicationContext(), "destroy", Toast.LENGTH_SHORT).show();
        recognizer.cancel();
        recognizer.shutdown();
        t.cancel(true);
    }
}
