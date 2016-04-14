package com.wirelessproject;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Vishnu on 10/04/16.
 */
public class ReceiverActivity extends Activity {

    private boolean stopped = true;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver_activity);
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        EditText receive_data = (EditText) findViewById(R.id.datareceived);
        Button receive_btn = (Button) findViewById(R.id.receive);
        Button clear_button = (Button) findViewById(R.id.clear);

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {

                    public void run() {
                        Log.i("Audio", "Running Audio Thread");
                        AudioRecord recorder = null;
                        AudioTrack track = null;
                        short[][] buffers = new short[256][160];
                        int ix = 0;

                        /*
                         * Initialize buffer to hold continuously recorded audio data, start recording, and start
                         * playback.
                         */
                        try {
                            int N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10);
//                            track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
//                                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10, AudioTrack.MODE_STREAM);
                            recorder.startRecording();
//                            track.play();
            /*
             * Loops until something outside of this thread stops it.
             * Reads the data from the recorder and writes it to the audio track for playback.
             */
                            while (!stopped) {
                                Log.i("Map", "Writing new data to buffer");
                                short[] buffer = buffers[ix++ % buffers.length];


                                N = recorder.read(buffer, 0, buffer.length);

                                for(int i = 0;i<buffer.length;i++){
                                    System.out.print(buffer[i] + " ");
                                }

                                System.out.println("Hell Yeah");
//                                track.write(buffer, 0, buffer.length);
                            }
                        } catch (Throwable x) {
                            Log.w("Audio", "Error reading voice audio", x);
                        }
        /*
         * Frees the thread's resources after the loop completes so that it can be run again
         */ finally {
                            recorder.stop();
                            recorder.release();
                            track.stop();
                            track.release();
                        }
                    }
                });
            }
        });
        receive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopped = false;
                thread.start();
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopped = true;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
