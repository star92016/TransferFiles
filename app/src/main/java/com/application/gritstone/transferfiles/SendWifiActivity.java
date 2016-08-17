package com.application.gritstone.transferfiles;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class SendWifiActivity extends AppCompatActivity {

//    private TextView text,text2;
    private String IPAddress;
//    private EditText et;

    //	private String mSDCard=Environment.getExternalStorageDirectory().toString();
    private ProgressDialog progressDialog;

    private boolean tag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_wifi);
//        text=(TextView) findViewById(R.id.IPaddress);
//        text2=(TextView) findViewById(R.id.IPaddress2);
//        et=(EditText) findViewById(R.id.content);

        progressDialog = new ProgressDialog(SendWifiActivity.this);
        progressDialog.setTitle("提示信息");
        progressDialog.setMessage("正在连接设备中，请稍后......");
        //    设置setCancelable(true); 表示我们能取消这个弹出框，
        progressDialog.setCancelable(true);
        //    设置ProgressDialog样式为圆圈的形式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        WifiManager wifi_service = (WifiManager) SendWifiActivity.this.getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        IPAddress=intToIp(wifiinfo.getIpAddress());  //本机IP地址

        if(!wifi_service.isWifiEnabled()){
            wifi_service.setWifiEnabled(true);
        }

        new MyAsyncTask().execute();
    }
    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

//	public void send(View v){
//		new MyAsyncTask().execute();
//	}

    public void show_wro(){
        Toast.makeText(this,"设备连接失败，请确认两台手机处于同一局域网中",Toast.LENGTH_LONG).show();
    }


    class MyAsyncTask extends AsyncTask<String, Integer, byte[]> {

        private StringBuffer sbuf=null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //    在onPreExecute()中我们让ProgressDialog显示出来
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    cancel(true);//执行异步线程取消操作
                    show_wro();
                }
            });
        }

        @Override
        protected byte[] doInBackground(String... params) {
            new Thread(){
                public void run(){
                    int port = 19999;//开启监听的端口
                    DatagramSocket ds = null;
                    DatagramPacket dp = null;
                    byte[] buf = new byte[1024];//存储发来的消息
                    sbuf = new StringBuffer();
                    try {
                        //绑定端口的
                        ds = new DatagramSocket(port);
                        dp = new DatagramPacket(buf, buf.length);
                        System.out.println("监听广播端口打开：");
                        ds.receive(dp);
                        ds.close();
                        int i;
                        for(i=0;i<1024;i++){
                            if(buf[i] == 0){
                                break;
                            }
                            sbuf.append((char) buf[i]);
                        }
                        System.out.println("收到广播消息：" + sbuf.toString());
                        IPAddress=sbuf.toString();
                        tag=true;

//	            rec="收到广播消息：" + sbuf.toString();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            int i=0;

            while(true){
                if(tag)
                    break;
            }

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
            if(tag) {
//                text.setText("收到广播消息：" + sbuf.toString());
                Intent intent=new Intent();
                intent.setClass(SendWifiActivity.this,SendWifi_SecondActivity.class);
                intent.putExtra("IPAddress",IPAddress);
                startActivity(intent);
                finish();
            }
            else
//                text.setText("接收失败");
            progressDialog.dismiss();
            tag=false;
        }


    }






}
