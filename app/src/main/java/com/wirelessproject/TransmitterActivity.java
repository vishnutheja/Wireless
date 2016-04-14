package com.wirelessproject;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class TransmitterActivity extends Activity{
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private final int duration = 1; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private double[] sample = new double[numSamples];
    private final double freqOfTone = 440; // hz

    private byte generatedSnd[];

    Handler handler = new Handler();


    public EditText dataToSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transmitter_activity);

        dataToSend = (EditText) findViewById(R.id.datatosend);
        Button send_data = (Button) findViewById(R.id.send);
        Button zero_button = (Button) findViewById(R.id.zerobutton);
        Button one_button = (Button) findViewById(R.id.onebutton);
        Button clear_button = (Button) findViewById(R.id.clearbutton);
        Button delete_button = (Button) findViewById(R.id.deletebutton);

        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Thread thread = new Thread(new Runnable() {
                    public void run() {
                        genTone();
                        handler.post(new Runnable() {

                            public void run() {
                                playSound();
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        zero_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = String.valueOf(dataToSend.getText());
                data = data + "0";
                dataToSend.setText(data);
            }
        });

        one_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = String.valueOf(dataToSend.getText());
                data = data + "1";
                dataToSend.setText(data);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = String.valueOf(dataToSend.getText());
                if(data.length()>0) {
                    data = data.substring(0, data.length() - 1);
                    dataToSend.setText(data);
                }
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataToSend.setText("");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    void genTone(){
        // fill out the array
        String data = String.valueOf(dataToSend.getText());
        sample = new double[data.length()*numSamples];
        for(int i = 0;i<data.length();i++){
            if(data.charAt(i) == '0'){
                for (int j = 0; j < numSamples; ++j) {
                    sample[i*numSamples + j] = 0;
                }
            }
            if(data.charAt(i) == '1'){
                for (int j = 0; j < numSamples; ++j) {
                    sample[i*numSamples + j] = Math.sin(2 * Math.PI * j / (sampleRate/freqOfTone));
                }
            }
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        generatedSnd = new byte[2*numSamples*data.length()];
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
}
