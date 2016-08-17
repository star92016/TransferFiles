package com.application.gritstone.transferfiles;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.gritstone.utils.OpenFile;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveWifiApActivity extends AppCompatActivity implements DashSpinner.OnDownloadIntimationListener{
    private WifiManager wifiManager;
    private boolean flag = false;
    public boolean err=false;
    private String file_name=null,file_content=null;
    private String mSDCard= Environment.getExternalStorageDirectory().toString();
    private TextView tv;
    private Button receive_bt;
    float mnProgress = 0.0f;
    DashSpinner mDashSpinner = null;

    private boolean state=false;

    private ProgressDialog progressDialog;

    private String SAVE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/transferfile_rec";
    private String receive_file=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_receive_bluetooth);
        setContentView(R.layout.activity_send_bluetooth_third);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        flag = !flag;
        setWifiApEnabled(flag,wifiManager);

        receive_bt= (Button) findViewById(R.id.receive_bt);
        tv= (TextView) findViewById(R.id.textView);
//        tv.setVisibility(View.GONE);
        tv.setText("接收文件");
        mDashSpinner = (DashSpinner) findViewById(R.id.progress_spinner);
        mDashSpinner.setOnDownloadIntimationListener(this);
        mDashSpinner.resetValues();
        mnProgress = 0.0f;
//        progressDialog = new ProgressDialog(ReceiveWifiApActivity.this);
//        progressDialog.setTitle("提示信息");
//        progressDialog.setMessage("正在接收文件中......");
//        progressDialog.setCancelable(false);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        new Thread(){
            public void run(){
                receive();
            }
        }.start();


//        new MyAsyncTask().execute();
    }

    public void setreceive_suc(){
        tv.setText(file_name+"接收成功");
        receive_bt.setVisibility(View.VISIBLE );
        receive_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= OpenFile.openFile(receive_file);
                startActivity(intent);
            }
        });
    }



    public void receive(){
        try {
            //接收文件名
            ServerSocket server = new ServerSocket(35000);
            Socket name = server.accept();
            if(name!=null) {
                InputStream nameStream = null;
                nameStream = name.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(nameStream);
                BufferedReader br = new BufferedReader(streamReader);
                String fileName = br.readLine();
                file_name = fileName;
                br.close();
                streamReader.close();
                nameStream.close();
                name.close();
                Socket data = server.accept();
                InputStream dataStream = data.getInputStream();
//                String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
                File f=new File(SAVE_PATH);
                if(!f.exists())
                    f.mkdirs();
                String savePath=SAVE_PATH+"/"+fileName;
                receive_file=savePath;

                FileOutputStream file = new FileOutputStream(savePath, false);
//                                FileOutputStream file = new FileOutputStream(savePath);
                byte[] flen=new byte[200];
                byte[] buffer = new byte[8192];
                dataStream.read(flen,0,flen.length);
//                StringBuffer sb=new StringBuffer();
                String temp=new String(flen);
                String flength=temp.substring(0,temp.lastIndexOf("..."));
                int sum=Integer.parseInt(flength);

                sum=(sum%8192==0?(int)(sum/8192):(int)(sum/8192+1));
//                int sum=5000;
                int size = -1;
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
                while ((size = dataStream.read(buffer)) >=0) {
                    file.write(buffer, 0, size);
//                    Log.v("aaaaaaaaaaaaaaaaa", "正在接收");
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
                dataStream.close();
                file.close();
                data.close();
                server.close();
                Looper.prepare();
                mDashSpinner.showSuccess();
                Looper.loop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            err=true;
                }
    }
    private void setProgress() {
        mnProgress += 0.01;
        mDashSpinner.setProgress(mnProgress);
    }

    public void onDownloadIntimationDone(DashSpinner.DASH_MODE dashMode) {
        switch (dashMode) {
            case SUCCESS:
//                Toast.makeText(this, "Download Successful!", Toast.LENGTH_SHORT).show();
                setreceive_suc();
                break;
            case FAILURE:
                Toast.makeText(this, "Download Failed!", Toast.LENGTH_SHORT).show();
                break;
            case UNKNOWN:
                Toast.makeText(this, "Unknown Download Error!", Toast.LENGTH_SHORT).show();
                break;
        }
    }












































//    public void receive_success(){
//        Toast.makeText(this,"接收成功",Toast.LENGTH_SHORT).show();
//    }
//
//    public void receive_err(){
//        Toast.makeText(this,"连接失败，请重新连接",Toast.LENGTH_SHORT).show();
//    }



















//    class MyAsyncTask extends AsyncTask<String, Integer, byte[]> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog.show();
//            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    // TODO Auto-generated method stub
//                    cancel(true);//执行异步线程取消操作
//                }
//            });
//        }
//
//        @Override
//        protected byte[] doInBackground(String... params) {
//            new Thread() {
//                public void run() {
//                        try {
//                            //接收文件名
//                            ServerSocket server = new ServerSocket(35000);
//                            Socket name = server.accept();
//                            if(name!=null) {
//                                InputStream nameStream = null;
//                                nameStream = name.getInputStream();
//                                InputStreamReader streamReader = new InputStreamReader(nameStream);
//                                BufferedReader br = new BufferedReader(streamReader);
//                                String fileName = br.readLine();
//                                file_name = fileName;
//                                br.close();
//                                streamReader.close();
//                                nameStream.close();
//                                name.close();
//                                Socket data = server.accept();
//                                InputStream dataStream = data.getInputStream();
//                                String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
//                                FileOutputStream file = new FileOutputStream(savePath, false);
////                                FileOutputStream file = new FileOutputStream(savePath);
//                                byte[] buffer = new byte[8192];
//                                int size = -1;
//                                while ((size = dataStream.read(buffer)) >=0) {
//                                    file.write(buffer, 0, size);
//                                    Log.v("aaaaaaaaaaaaaaaaa", "正在接收");
//                                }
//                                file_content = "传输成功";
//                                file.close();
//                                dataStream.close();
//                                data.close();
//                                server.close();
//                                state=true;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            err=true;
//
//                        }
//                    }
//            }.start();
//            while(!state){
//                if(err)
//                    break;
//            }
//
//
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onPostExecute(byte[] result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//            receive_success();
//            state=false;
//            finish();
//        }
//    }


    // wifi热点开关
    public static boolean setWifiApEnabled(boolean enabled,WifiManager wifiManager) {
        if (enabled) { // disable WiFi in any case
            wifiManager.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "WIFI_AP_CCONNECTION";
            //配置热点的密码
            apConfig.preSharedKey = "12122112";
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }
}
