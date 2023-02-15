package ca.app.assasins.taskappsassassinsandroid.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class AudioService extends AppCompatActivity {

    AudioManager audioManager;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    final private static String RECORDED_FILE = "/audio.3gp";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // set the volume of played media to maximum.
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        // check the permission
        /*if (!checkPermissionDevice())
            requestPermission();*/
    }

    private void recordAudio() {
        if (checkPermissionDevice()) {
//                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()
//                            + "/recording.3gp";
//                    pathSave = getExternalFilesDir(null).getAbsolutePath()
//                            + RECORDED_FILE;
            pathSave = getExternalCacheDir().getAbsolutePath()
                    + RECORDED_FILE;

            Log.d("path", "onClick: " + pathSave);

            setUpMediaRecorder();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException ise) {
                // make something ...
                ise.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();*/
        } else
            requestPermission();
    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        /*mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);*/
        mediaRecorder.setOutputFile(pathSave);
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    }

}
