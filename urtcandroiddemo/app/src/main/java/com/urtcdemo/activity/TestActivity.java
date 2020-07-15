package com.urtcdemo.activity;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.urtcdemo.R;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Method;

/**
 * @author ciel
 * @create 2020/4/7
 * @Describe
 */
public class TestActivity extends AppCompatActivity {

    private void enableHDMIInAudio(boolean enable) {
        if (enable) {
            isRecording = true;
            record = new Thread(new recordSound());
            record.start();
        } else {
            isRecording = false;
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setParameters("HDMIin_enable=false");
//            SystemProperties.set("media.audio.device_policy", "");
            setProperty("media.audio.device_policy", "");
        }
    }

    public static  String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "unknown" ));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return value;
        }
    }

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private AudioManager mAudioManager;
    private AudioTrack m_out_trk;
    private Thread record;
    private int m_out_buf_size;
    private boolean isRecording = false;
    private

    class recordSound implements Runnable {
        AudioRecord m_in_rec;

        public void run() {
            synchronized (this) {
                int frequence = 44100;
                int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
                int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
                m_out_buf_size = AudioTrack.getMinBufferSize(frequence,
                        channelConfig, audioEncoding);
                m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, frequence,
                        channelConfig,
                        audioEncoding, m_out_buf_size,
                        AudioTrack.MODE_STREAM);
                File file = null;
                DataOutputStream dos = null;
                byte[] m_in_bytes;
                int m_in_buf_size = AudioRecord.getMinBufferSize(frequence, channelConfig, audioEncoding);
                m_in_rec = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER, frequence, channelConfig,
                        AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);
                m_in_bytes = new byte[m_in_buf_size];
                m_in_rec.startRecording();
                m_out_trk.play();
                while (isRecording) {
                    int bufferReadResult = m_in_rec.read(m_in_bytes, 0, m_in_buf_size);
                    if ((bufferReadResult > 0) && (m_out_trk != null))
                        m_out_trk.write(m_in_bytes, 0, bufferReadResult);
                }

            }
        }
    }

    private Button mButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio);
        mButton = findViewById(R.id.test_audio);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording){
                    enableHDMIInAudio(true);
                    mButton.setText("hdminaudio stop");
                }else{
                    enableHDMIInAudio(false);
                    mButton.setText("hdminaudio start");
                }
            }
        });
    }
}
