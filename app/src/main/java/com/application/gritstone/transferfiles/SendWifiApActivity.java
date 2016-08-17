package com.application.gritstone.transferfiles;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendWifiApActivity extends AppCompatActivity {
    private static final String TAG ="aaa" ;
    private ListView deviceListView;
    private WifiReceiver wifiReceiver;
    private boolean isConnected=false;
    private List<ScanResult> wifiList;
    private List<String> passableHotsPot;

    private ProgressDialog progressDialog;

    private boolean find=false;


    private HashMap<String,Object> map;
    private ArrayList<HashMap<String,Object>> listitem=new ArrayList<HashMap<String,Object>>();

    private WifiManager wifiManager;
    private boolean flag = false;
    private Context context;

    private boolean isConnected_device=false;

    private String capabilities=null;

    private MyAsyncTask myAsyncTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_wifiap);


        progressDialog = new ProgressDialog(SendWifiApActivity.this);
        progressDialog.setTitle("提示信息");
        progressDialog.setMessage("正在连接设备中，请开启接收端......");
        //    设置setCancelable(false); 表示我们不能取消这个弹出框，等下载完成之后再让弹出框消失
//        progressDialog.setCancelable(false);
        //    设置ProgressDialog样式为圆圈的形式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setCancelable(true);//响应取消操作，这里如果设置false,按返回键ProgressDialog也不消失。

        context=this;
        //获取wifi管理服务
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

//        wifiManager.setWifiEnabled(false);
//        wifiManager.setWifiEnabled(true);
        //打开Wifi
        if(!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        init();
        myAsyncTask=new MyAsyncTask();
        myAsyncTask.execute();
//        Intent intent = new Intent();
//        intent.setClass(SendWifiApActivity.this, SendWifiAps_SecondActivity.class);
//        startActivity(intent);

    }

    /* 初始化参数 */
    public void init() {
        Log.v("aaa", "启动一个Wifi 热点！");
//        deviceListView= (ListView) findViewById(R.id.listView2);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        //搜索热点
        wifiManager.startScan();
        wifiList=wifiManager.getScanResults();
        wifiList=noSameName(wifiList);
//         mWifiConfigurations=wifiManager.getConfiguredNetworks();
    }

    public boolean addNetwork(WifiConfiguration wcg) { // 添加一个网络配置并连接
        int wcgID = wifiManager.addNetwork(wcg);
        boolean b = wifiManager.enableNetwork(wcgID, true);
        Log.d(TAG,"addNetwork--" + wcgID);
        Log.d(TAG,"enableNetwork--" + b);
        System.out.println("addNetwork--" + wcgID);
        System.out.println("enableNetwork--" + b);
        return b;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        Log.i(TAG, "SSID:" + SSID + ",password:" + Password);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

//        WifiConfiguration tempConfig = this.IsExsits(SSID);
        WifiConfiguration tempConfig =null;

        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        } else {
            Log.i(TAG, "IsExsits is null.");
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            Log.i(TAG, "Type =1.");
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            Log.i(TAG, "Type =2.");
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {

            Log.i(TAG, "Type =3.");
            config.preSharedKey = "\"" + Password + "\"";

            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

//    private WifiConfiguration IsExsits(String SSID) { // 查看以前是否已经配置过该SSID
//        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
//        for (WifiConfiguration existingConfig : existingConfigs) {
//            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
//                return existingConfig;
//            }
//        }
//        return null;
//    }


    //ScanResult去重复和空值
    public List<ScanResult>noSameName(List<ScanResult> list)
    {

        List<ScanResult> newlist = new ArrayList<ScanResult>();
        if(list!=null) {
            for (ScanResult result : list) {
                if (!TextUtils.isEmpty(result.SSID) && !containName(newlist, result.SSID))
                    newlist.add(result);
            }
        }
        return newlist;
    }

    public boolean containName(List<ScanResult> sr, String name)
    {
        for (ScanResult result : sr)
        {
            if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
                return true;
        }
        return false;
    }


//    // wifi热点开关
//    public boolean setWifiApEnabled(boolean enabled) {
//        if (enabled) { // disable WiFi in any case
//            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
//            wifiManager.setWifiEnabled(false);
//        }
//        try {
//            //热点的配置类
//            WifiConfiguration apConfig = new WifiConfiguration();
//            //配置热点的名称(可以在名字后面加点随机数什么的)
//            apConfig.SSID = "WIFI_AP_CCONNECTION";
//            //配置热点的密码
//            apConfig.preSharedKey = "12122112";
//            //通过反射调用设置热点
//            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
//            //返回热点打开状态
//            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
//        } catch (Exception e) {
//            return false;
//        }
//    }




    /* 监听热点变化 */
    private final class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.v("bbb", "启动一个Wifi 热点！");
            wifiList = wifiManager.getScanResults();
            if (wifiList == null || wifiList.size() == 0 || isConnected)
                return;
            onReceiveNewNetworks(wifiList);
        }
    }

    /*当搜索到新的wifi热点时判断该热点是否符合规格*/
    public void onReceiveNewNetworks(List<ScanResult> wifiList){
        Log.v("ccc", "启动一个Wifi 热点！");
        passableHotsPot=new ArrayList<String>();
        for(ScanResult result:wifiList){
            System.out.println(result.SSID);
            if((result.SSID).contains("YRCCONNECTION"))
                passableHotsPot.add(result.SSID);
        }
        synchronized (this) {
            connectToHotpot();
        }
    }

    /*连接到热点*/
    public void connectToHotpot(){
        Log.v("ddd", "启动一个Wifi 热点！");
        if(passableHotsPot==null || passableHotsPot.size()==0)
            return;
        WifiConfiguration wifiConfig=this.setWifiParams(passableHotsPot.get(0));
        int wcgID = wifiManager.addNetwork(wifiConfig);
        boolean flag=wifiManager.enableNetwork(wcgID, true);
        isConnected=flag;
        System.out.println("connect success? "+flag);
    }

    /*设置要连接的热点的参数*/
    public WifiConfiguration setWifiParams(String ssid){
        Log.v("eee", "启动一个Wifi 热点！");
        WifiConfiguration apConfig=new WifiConfiguration();
        apConfig.SSID="\""+ssid+"\"";
        apConfig.preSharedKey="\"12122112\"";
        apConfig.hiddenSSID = true;
        apConfig.status = WifiConfiguration.Status.ENABLED;
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return apConfig;
    }


    class MyAsyncTask extends AsyncTask<String, Integer, byte[]> {

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
                }
            });
        }

        @Override
        protected byte[] doInBackground(String... params) {

            new Thread(){
                public void run(){
                    for(int a=0;a<20;a++){
                        init();
                        for (int i = 0; i < wifiList.size(); i++) {
                            if (wifiList.get(i).SSID.equals("WIFI_AP_CCONNECTION")) {
                                find = true;
                                capabilities = wifiList.get(i).capabilities;
                                isConnected_device=addNetwork(CreateWifiInfo("WIFI_AP_CCONNECTION", null, 1));
                                break;
                            }
                        }
                        if(find)
                            break;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            try {
                for(int i=0;i<20;i++) {
                    if(find)
                        break;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!find) {
                cancel(true);
                finish();
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
            progressDialog.dismiss();
            if(find) {
                find=false;
//                boolean res=addNetwork(CreateWifiInfo("WIFI_AP_CCONNECTION", null, 1));
                if(isConnected_device) {
                    Intent intent = new Intent();
                    intent.setClass(SendWifiApActivity.this, SendWifiAps_SecondActivity.class);
                    startActivity(intent);
                }
                finish();

            }
            else{
                System.out.print("ERROR");
            }
        }

        // 调用中断的处理
//        @Override
//        protected void onCancelled() {
////            mAuthTask = null;
//            progressDialog.cancel();
//            finish();
////            closeProgressDialog();//关闭遮罩进度条
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
