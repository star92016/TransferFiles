package com.application.gritstone.transferfiles;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static com.application.gritstone.utils.IpToInt.intToIp;

/**
 * Created by guorenjie on 2016/6/19.
 */
public class SendWifi_ThirdActivity extends AppCompatActivity implements DashSpinner.OnDownloadIntimationListener{
    boolean dec;
    long temp=100;
    float mnProgress = 0.0f;
    DashSpinner mDashSpinner = null;
    private TextView textView;

//    public static final String MY_UUID ="00001101-0000-1000-8000-00805F9B34FB";
    OutputStream os=null;
//    private Socket socket=null;
//    BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//    BluetoothDevice bluetoothDevice=null;
//    ArrayList<HashMap<String, Object>> listItem=null;
//    ListView lv=null;
    String bluetoothAddress=null;
    String sendFile=null;
    String strPath=null;
    String IPAddress=null;

//    private boolean isConnected=false;//判断Socket连接是否成功

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bluetooth_third);
        Intent intent=this.getIntent();
        sendFile=intent.getStringExtra("file_path");
        strPath=intent.getStringExtra("file_name");
        IPAddress=intent.getStringExtra("IPAddress");
        mDashSpinner = (DashSpinner) findViewById(R.id.progress_spinner);
        mDashSpinner.setOnDownloadIntimationListener(this);
        mDashSpinner.resetValues();
        mnProgress = 0.0f;
//        WifiManager wifi_service = (WifiManager) SendWifi_ThirdActivity.this.getSystemService(WIFI_SERVICE );
//        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
//        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
//
//        IPAddress= intToIp(dhcpInfo.serverAddress);

        textView= (TextView) findViewById(R.id.textView);
        textView.setText(strPath+" "+"文件发送！");
//        Toast.makeText(this,sendFile,Toast.LENGTH_LONG).show();
        new Thread(){
            public void run(){
//                sendPath(strPath);
//                if(isConnected)
                    sendFile(strPath);
            }
        }.start();
    }

    private void sendFile(String str){
        Socket name = null;
        try {
            name = new Socket(IPAddress, 40004);
//            isConnected=true;
            OutputStream outputName = name.getOutputStream();
            if(outputName!=null) {
                OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
                BufferedWriter bwName = new BufferedWriter(outputWriter);
                bwName.write(str);
                bwName.close();
                outputWriter.close();
                outputName.close();
                name.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
//            connect_err();
//            isConnected=false;
//            mDashSpinner.showUnknown();
        }
        Socket socket=null;
        try {
            try {
                    socket= new Socket(IPAddress, 40004);
                    os=socket.getOutputStream();
            }catch(IOException e){

            }
            if(os!=null){
                File file=new File(sendFile);
                byte [] buffer=new byte[100];
                String strtemp=""+file.length()+"...";
                for(int i=0;i<strtemp.getBytes().length;i++){
                    buffer[i]=strtemp.getBytes()[i];
                }
                os.write(buffer,0,buffer.length);
                int sum=(file.length()%8192==0?(int)(file.length()/8192):(int)(file.length()/8192+1));
                FileInputStream fis=new FileInputStream(sendFile);
                buffer=new byte[8192];
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
                os.close();
                fis.close();
                socket.close();
                Looper.prepare();
                mDashSpinner.showSuccess();
                Looper.loop();

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

    private void setProgress() {
        mnProgress += 0.01;
        mDashSpinner.setProgress(mnProgress);
    }

    public void send_suc(){
        textView.setText(strPath+"发送成功");
    }
    public void connect_err(){
        textView.setText(strPath+"发送失败");
    }

    public void onDownloadIntimationDone(DashSpinner.DASH_MODE dashMode) {
        switch (dashMode) {
            case SUCCESS:
//                Toast.makeText(this, "Download Successful!", Toast.LENGTH_SHORT).show();
                send_suc();
                break;
            case FAILURE:
                Toast.makeText(this, "请确认发送端是否已开启", Toast.LENGTH_SHORT).show();
                connect_err();
                break;
            case UNKNOWN:
                Toast.makeText(this, "Unknown Download Error!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

