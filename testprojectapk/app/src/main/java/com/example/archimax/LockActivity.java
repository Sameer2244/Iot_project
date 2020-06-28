package com.example.archimax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


public class LockActivity extends AppCompatActivity {
    String serialidtopic;
    static String USERNAME = "sameer";
    static String PASSWORD = "ggwpglhf";
    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
    Button unlock;
    public static MqttConnectOptions options;
    public static MqttAndroidClient client;
    boolean isStarted= false;
    private CountDownTimer time;
    long timeout = 15000;
    private static final String TAG = LockActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        unlock =(Button)findViewById(R.id.unlock);
        serialidtopic =serialfragment.serialid;
        Toast.makeText(this, "Serialid "+serialidtopic, Toast.LENGTH_SHORT).show();
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStarted) {
                    isStarted = true;
                    Toast.makeText(LockActivity.this, "Unlock", Toast.LENGTH_SHORT).show();
                    String message = "topic";
                    try {

                        client.publish(serialidtopic, message.getBytes(),0,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    time = new CountDownTimer(timeout, 1000) {
                        @Override
                        public void onTick(long l) {

                            unlock.setText("LOCK");
                            if (timeout == 5000) {

                            }
                        }

                        @Override
                        public void onFinish() {
                            unlock.setText("UNLOCK");
                            isStarted = false;
                            Toast.makeText(LockActivity.this, "Locked", Toast.LENGTH_SHORT).show();
                        }
                    }.start();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        IMqttToken token = null;
        try {
            token = client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                //Toast.makeText(LockActivity.this, "your device is online", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
               // Toast.makeText(LockActivity.this, "your device is offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
