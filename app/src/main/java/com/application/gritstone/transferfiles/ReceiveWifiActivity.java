package com.application.gritstone.transferfiles;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.application.gritstone.utils.IpToInt.intToIp;

public class ReceiveWifiActivity extends AppCompatActivity implements DashSpinner.OnDownloadIntimationListener{
    private String file_content=null;
    private String file_name=null;
    private boolean tag=false;
    private String rec=null;
    private String IPAddress;
    private ProgressDialog progressDialog2;

    private float mnProgress = 0.0f;
    private DashSpinner mDashSpinner = null;

    private TextView tv;
    private Button receive_bt;

    private boolean socket_connect_tag=false;

    private boolean state=false;

    private String SAVE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/transferfile_rec";

    private String receive_file=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_receive_wifi);
        setContentView(R.layout.activity_send_bluetooth_third);


        receive_bt= (Button) findViewById(R.id.receive_bt);
        tv= (TextView) findViewById(R.id.textView);
        tv.setText("接收文件");
        progressDialog2 = new ProgressDialog(ReceiveWifiActivity.this);
        progressDialog2.setTitle("提示信息");
        progressDialog2.setMessage("正在接收文件中，请稍后......");
        progressDialog2.setCancelable(true);
        progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        WifiManager wifi_service = (WifiManager) ReceiveWifiActivity.this.getSystemService(WIFI_SERVICE);
//        if(!wifi_service.isWifiEnabled())
            wifi_service.setWifiEnabled(true);

        mDashSpinner = (DashSpinner) findViewById(R.id.progress_spinner);
        mDashSpinner.setOnDownloadIntimationListener(this);
        mDashSpinner.resetValues();
        mnProgress = 0.0f;

        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        IPAddress=intToIp(wifiinfo.getIpAddress());  //本机IP地址
        new MyAsyncTask().execute();

        new Thread(){
            public void run(){
                receive();
            }
        }.start();


    }



    class MyAsyncTask extends AsyncTask<String, Integer, byte[]> {



        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(String... params) {  //发送UDP广播部分
            new Thread(){
                public void run(){
                    String host = "255.255.255.255";
                    int port = 19999;
                    String message = IPAddress;
                    try {
                        while(!socket_connect_tag){
                            InetAddress adds = InetAddress.getByName(host);
                            DatagramSocket ds = new DatagramSocket();
                            ds.setSoTimeout(3000);
                            DatagramPacket dp = new DatagramPacket(message.getBytes(),
                                    message.length(), adds, port);
                            ds.send(dp);
                            ds.close();
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(byte[] result)
        {
            super.onPostExecute(result);
        }


    }

    public void  receive(){
//        new MyAsyncTask2().execute();
        try {
            //接收文件名
            ServerSocket server = new ServerSocket(40004);
            Socket name = server.accept();
            socket_connect_tag=true;
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
                final String savePath=SAVE_PATH+"/"+fileName;
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
        }
    }

    private void setProgress() {
        mnProgress += 0.01;
        mDashSpinner.setProgress(mnProgress);
    }

    public void setreceive_suc(){
        tv.setText(file_name+"接收成功");
        receive_bt.setVisibility(View.VISIBLE );
        receive_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=OpenFile.openFile(receive_file);
                startActivity(intent);
            }
        });
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

    public void receive_success(){
        Toast.makeText(this,"接收成功",Toast.LENGTH_SHORT).show();
    }





























    class MyAsyncTask2 extends AsyncTask<String, Integer, byte[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog2.show();
            progressDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    cancel(true);//执行异步线程取消操作
                }
            });
        }

        @Override
        protected byte[] doInBackground(String... params) {

            new Thread() {
                public void run() {

                    try {
                        //接收文件名
                        ServerSocket server = new ServerSocket(40004);
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
                            String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
                            FileOutputStream file = new FileOutputStream(savePath, false);
                            byte[] buffer = new byte[1024];
                            int size = -1;
                            while ((size = dataStream.read(buffer)) != -1) {
                                file.write(buffer, 0, size);
                                Log.v("aaaaaaaaaaaaaaaaa", "正在接收");
                            }
                            file_content = "传输成功";
                            file.close();
                            dataStream.close();
                            data.close();
                            server.close();
                            state=true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            while(!state){
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(byte[] result) {
            super.onPostExecute(result);
            progressDialog2.dismiss();
            receive_success();
            state=false;
        }
    }

}
