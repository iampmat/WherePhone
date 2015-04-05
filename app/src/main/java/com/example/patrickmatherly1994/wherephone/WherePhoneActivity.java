package com.example.patrickmatherly1994.wherephone;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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


public class WherePhoneActivity extends Activity implements RecognitionListener {

    private SpeechRecognizer recognizer;
    private static final String KEYPHRASE = "where is my phone";

    private EditText etInput;
    private EditText etOutput;

    private String sInput;
    private String sOutput;

    private TextToSpeech reply;

    private  AsyncTask t;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_where_phone);

        // init edittexts
        etInput = (EditText) findViewById(R.id.et_input);
        etOutput = (EditText) findViewById(R.id.et_output);

        // Set up the texttospeech on reply
        reply=new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            reply.setLanguage(Locale.US);
                        }
                    }
                });
    }

    public void beginRecognizer(View view) {
        // Recognizer initialization, Include resource files in form of assets
        // Call switchsearch on the keyphrase
        sInput = etInput.getText().toString();
        sOutput = etOutput.getText().toString();

        // Restart the recognizer if it was running
        //recognizer.shutdown();

        t = new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(WherePhoneActivity.this);
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
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(sInput);
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                        // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)

                        // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)

                        // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(sInput, sInput);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(sInput))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);
        /*
        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
        */
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
        if (text.equals(sInput)) {
            // Text to speech
            ((TextView) findViewById(R.id.result_text)).setText(sInput);
            reply.speak(sOutput, TextToSpeech.QUEUE_ADD, null);

            // Restart the recognizer
            if(!reply.isSpeaking()){ recognizer.stop(); }
            switchSearch(sInput);
        }
        else{ ((TextView) findViewById(R.id.result_text)).setText("Try again"); }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(sInput);
    }


    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
        t.cancel(true);
    }
}
