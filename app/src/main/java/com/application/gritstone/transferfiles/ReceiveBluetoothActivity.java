package com.application.gritstone.transferfiles;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReceiveBluetoothActivity extends AppCompatActivity implements DashSpinner.OnDownloadIntimationListener {
    int dec;
    public static int dec1;
    int dec2;
    long temp=100;
    float mnProgress = 0.0f;
    DashSpinner mDashSpinner = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dec=2;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        open(bluetoothAdapter);
        while(bluetoothAdapter.isEnabled()==false){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_bluetooth);
        mDashSpinner = (DashSpinner) findViewById(R.id.progress_spinner);
        mDashSpinner.setOnDownloadIntimationListener(this);
        mDashSpinner.resetValues();
        mnProgress = 0.0f;
        AcceptThread acceptThread=new AcceptThread();
        acceptThread.setPriority(10);
        acceptThread.start();
        dec=acceptThread.value();
        if(dec==1){
            mDashSpinner.showSuccess();
        }


    }

    private void open(BluetoothAdapter adapter){
        if (adapter == null) {
            Toast.makeText(ReceiveBluetoothActivity.this, "设备不支持蓝牙设备！", Toast.LENGTH_SHORT).show();
        }else{
            adapter.enable();
        }
    }

    protected void onDestroy() {
        super.onDestroy();

    }
    private void setProgress(long temp) {
        mnProgress += 0.005;
        mDashSpinner.setProgress(mnProgress);
    }

    private void setProgress() {
        mnProgress += 0.01;
        mDashSpinner.setProgress(mnProgress);
    }

    public void onDownloadIntimationDone(DashSpinner.DASH_MODE dashMode) {
        switch (dashMode) {
            case SUCCESS:
                Toast.makeText(this, "Download Successful!", Toast.LENGTH_SHORT).show();
                break;
            case FAILURE:
                Toast.makeText(this, "Download Failed!", Toast.LENGTH_SHORT).show();
                break;
            case UNKNOWN:
                Toast.makeText(this, "Unknown Download Error!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
//等待客户端请求的线程类
class AcceptThread extends Thread{
    ReceiveBluetoothActivity receive=new ReceiveBluetoothActivity();
    int dec;
    long temp=100;
    float mnProgress = 0.0f;
    DashSpinner mDashSpinner = null;
    public static final String MY_UUID ="00001101-0000-1000-8000-00805F9B34FB";
    public static final String PROTOCOL_SCHEME_RFCOMM ="bluetooth22222";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private InputStream is;
    Handler testHandler = new Handler();
    public AcceptThread(){
        try {
            serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,UUID.fromString(MY_UUID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            Message message = new Message();
            message.what = 1;
            String sb=null;
            socket=serverSocket.accept();
            is=socket.getInputStream();
            byte[] buffer_1=new byte[200];
            char[] buffer_2=new char[200];
            byte [] buffer=new byte[8192];
            is.read(buffer_1,0,buffer_1.length);
            for(int i=0;i<buffer_1.length;i++){
                buffer_2[i]=(char)buffer_1[i];
            }
            int count=0;
                String strtemp=new String(buffer_2);
                Log.v("jjjjjjjjjjjjjjj",strtemp);

                String strtemp3="........";
                int a=strtemp.indexOf(strtemp3);
                String strtemp1=strtemp.substring(0,a);
                sb=strtemp1;
            FileOutputStream fos=new FileOutputStream("/storage/emulated/0/"+sb);
            while((count=is.read(buffer,0,buffer.length))>=0){
                    fos.write(buffer,0,count);
            }
            this.testHandler.sendMessage(message);
            receive.dec=1;
            this.dec=receive.dec;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int value(){
        return dec;
    }


}




