package com.application.gritstone.transferfiles;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SendBluetoothThirdActivity extends AppCompatActivity implements DashSpinner.OnDownloadIntimationListener {

    boolean dec;
    long temp=100;
    float mnProgress = 0.0f;
    DashSpinner mDashSpinner = null;

    public static final String MY_UUID ="00001101-0000-1000-8000-00805F9B34FB";
    OutputStream os=null;
    private BluetoothSocket socket=null;
    BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice bluetoothDevice=null;
    ArrayList<HashMap<String, Object>> listItem=null;
    ListView lv=null;
    String bluetoothAddress=null;
    String sendFile=null;
    String strPath=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bluetooth_third);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent=this.getIntent();
        bluetoothAddress=intent.getStringExtra("bluetoothAddress");
        sendFile=intent.getStringExtra("sendfile");
        strPath=splitPath(sendFile);
        mDashSpinner = (DashSpinner) findViewById(R.id.progress_spinner);
        mDashSpinner.setOnDownloadIntimationListener(this);
        mDashSpinner.resetValues();
        mnProgress = 0.0f;
        TextView textView= (TextView) findViewById(R.id.textView);
        textView.setText(strPath+" "+"文件发送！");
        Toast.makeText(this,sendFile,Toast.LENGTH_LONG).show();
        new Thread(){
            public void run(){
                sendPath(strPath);
                sendFile();
            }
        }.start();
    }

    private void sendFile(){

        try {
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            try {
                if(bluetoothDevice==null){
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress);
                }
                if(socket==null){
                    socket=bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    socket.connect();
                    os=socket.getOutputStream();
                }
            }catch(IOException e){

            }
            if(os!=null){
                File file=new File(sendFile);
                int sum=(file.length()%8192==0?(int)(file.length()/8192):(int)(file.length()/8192+1));
                FileInputStream fis=new FileInputStream(sendFile);
                byte [] buffer=new byte[8192];
                int bool1=1;
                int bool2=0;
                int count=0;
                int totalCount=0;
                if(sum>100){
                    bool2=1;
                    sum=sum/100+1;
                }else{
                    bool2=0;
                }
                while((count=fis.read(buffer))>0){
                    os.write(buffer,0,count);
                    totalCount+=count;
                    Log.d("totalCount",String.valueOf(totalCount));
                    if(bool2==0){
                        if(sum<=10){
                            for(int i=0;i<10;i++){
                                setProgress();
                            }
                        }else if(bool2>10&&bool2<=20){
                            for(int i=0;i<5;i++){
                                setProgress();
                            }
                        }else if(bool2>20&&bool2<=50){
                            for(int i=0;i<2;i++){
                                setProgress();
                            }
                        }else{
                            setProgress();
                        }
                    }
                    if(bool2==1){
                        if(bool1==sum){
                            setProgress();
                        }
                        bool1++;
                        if(bool1>sum){
                            bool1=1;
                        }
                    }
                }
                if(mnProgress <= 1.0){
                    while(mnProgress <= 1.0){
                        setProgress();
                    }
                }
                dec=true;
                Looper.prepare();
                mDashSpinner.showSuccess();
                Looper.loop();
                fis.close();
            }else{

                dec=false;
                Looper.prepare();
                mDashSpinner.showFailure();
                Looper.loop();

            }

        }catch(Exception e){

            Looper.prepare();
            mDashSpinner.showUnknown();
            Looper.loop();
            dec=false;
        }
    }

    private void sendPath(String str){
        str=str+"........";

        try {
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            try {
                if(bluetoothDevice==null){
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress);
                }
                if(socket==null){
                    socket=bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    socket.connect();
                    os=socket.getOutputStream();
                }
            }catch(IOException e){
                e.printStackTrace();
                System.err.print("err:"+e);

            }


            if(os!=null){
                byte [] buffer=new byte[200];

                for(int i=0;i<str.getBytes().length;i++){
                    buffer[i]=str.getBytes()[i];
                }
                os.write(buffer,0,buffer.length);
                Log.d("totalCount",str);
                Log.d("totalCount",new String(buffer));

            }else{

                dec=false;
            }

        }catch(Exception e){
            dec=false;
        }
        if(dec=false){
            mDashSpinner.showUnknown();
        }
    }

    private String splitPath(String str){
        String str1=null;
        String [] str2=null;
        str2=str.split("/");
        str1=str2[str2.length-1];
        return str1;
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
                Toast.makeText(this, "文件传输成功!", Toast.LENGTH_SHORT).show();
                break;
            case FAILURE:
                Toast.makeText(this, "文件传输失败!", Toast.LENGTH_SHORT).show();
                break;
            case UNKNOWN:
                Toast.makeText(this, "未知错误!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, SendBluetoothSecondActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
