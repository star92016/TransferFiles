package com.application.gritstone.transferfiles;

/**
 * Created by guorenjie on 2016/6/14.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.HashMap;
import java.util.List;

import static com.application.gritstone.utils.IpToInt.intToIp;

public class SendWifiAps_SecondActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{

    private ListView list2=null;
    private static boolean exit=false;
    public List<HashMap<String,Object>> mData=null;
    public  String mDir= Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView title_path;
    private Context context=null;
    private String current_file;
    private String filepath;
    private String IPAddress;

    private boolean isOK=false;

    private boolean err=false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bluetooth_second);

        Toast.makeText(this,"设备匹配成功",Toast.LENGTH_SHORT).show();

        context=SendWifiAps_SecondActivity.this;
        title_path= (TextView) findViewById(R.id.title_path2);
        list2= (ListView) findViewById(R.id.listview2);
        mData=getData();
        list2.setAdapter(new MyAdapter(context));
        list2.setOnItemClickListener(new MyOnItemClickListener());
        list2.setOnItemLongClickListener(this);

        WifiManager wifi_service = (WifiManager) SendWifiAps_SecondActivity.this.getSystemService(WIFI_SERVICE );
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();

        IPAddress=intToIp(dhcpInfo.serverAddress);

        progressDialog = new ProgressDialog(SendWifiAps_SecondActivity.this);
        progressDialog.setTitle("提示信息");
        progressDialog.setMessage("正在发送文件......");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setCancelable(true);//响应取消操作，这里如果设置false,按返回键ProgressDialog也不消失。
    }

//    private String intToIp(int paramInt) {  //转换为IP地址格式
//        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
//                + (0xFF & paramInt >> 24);
//    }

    ListView.OnCreateContextMenuListener menuList= new ListView.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater mif=getMenuInflater();
            mif.inflate(R.menu.menu,contextMenu);
        }
    };


    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        list2.setOnCreateContextMenuListener(menuList);
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menu;
        menu= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position=menu.position;
        filepath= (String) mData.get(position).get("info");

        boolean isDirectory=false;

        if(new File(filepath).isDirectory()){
            isDirectory=true;
        }
        //点击发送文件
        switch(item.getItemId()){
            case R.id.send:
                if(isDirectory){
                    Toast.makeText(this,"暂不支持发送文件夹，请选择文件进行发送",Toast.LENGTH_SHORT).show();
                }else {
//                    new MyAsyncTask().execute();
                    Intent intent=new Intent();
                    intent.setClass(SendWifiAps_SecondActivity.this,SendWifiAps_ThirdActivity.class);
                    intent.putExtra("file_name",new File(filepath).getName());
                    intent.putExtra("file_path",filepath);
                    startActivity(intent);
                }
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void show_err(){
        Toast.makeText(this,"传送失败，请确定发送端已打开",Toast.LENGTH_SHORT).show();
    }

    public void show_suc(){
        Toast.makeText(this,"传送成功",Toast.LENGTH_SHORT).show();
    }
    public void show_wro(){
        Toast.makeText(this,"传送失败，请重新传送",Toast.LENGTH_SHORT).show();
    }


    class MyAsyncTask extends AsyncTask<String, Integer, byte[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            new Thread() {
                public void run() {
                    try {
                        Socket name = new Socket(IPAddress, 35000);
                        OutputStream outputName = name.getOutputStream();
                        OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
                        BufferedWriter bwName = new BufferedWriter(outputWriter);
                        bwName.write(new File(filepath).getName());
                        bwName.close();
                        outputWriter.close();
                        outputName.close();
                        name.close();
                        Socket data = new Socket(IPAddress, 35000);
                        OutputStream outputData = data.getOutputStream();
                        FileInputStream fileInput = new FileInputStream(filepath);
                        int size = -1;
                        byte[] buffer = new byte[1024];
                        while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
                            outputData.write(buffer, 0, size);
                        }
                        outputData.close();
                        fileInput.close();
                        data.close();
                        isOK = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        err=true;
                    }
                }
            }.start();

            while(!isOK){
                if(err)
                    break;
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
            progressDialog.dismiss();
            if(err)
                show_err();
            else if(isOK)
                show_suc();
            isOK=false;
            err=false;
        }
    }


    class SendWifiAp_Thread extends Thread{

        public void run(){

            try {
                Socket name = new Socket(IPAddress, 40000);
                if(name.isConnected()) {
                    isOK=true;
                    OutputStream outputName = name.getOutputStream();
                    OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
                    BufferedWriter bwName = new BufferedWriter(outputWriter);
                    bwName.write(new File(filepath).getName());
                    bwName.close();
                    outputWriter.close();
                    outputName.close();
                    name.close();
                }

                Socket data = new Socket(IPAddress, 40000);
                if(data.isConnected()) {
                    OutputStream outputData = data.getOutputStream();
                    FileInputStream fileInput = new FileInputStream(filepath);
                    int size = -1;
                    byte[] buffer = new byte[1024];
                    while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
                        outputData.write(buffer, 0, size);
                    }
                    outputData.close();
                    fileInput.close();
                    data.close();
                    show_suc();
                }

                if(!isOK)
                    show_err();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if((Integer)mData.get(position).get("image")==R.drawable.empty_folder||(Integer)mData.get(position).get("image")==R.drawable.folder){
                mDir=(String) mData.get(position).get("info");
                mData=getData();
                list2.setAdapter(new MyAdapter(SendWifiAps_SecondActivity.this));
            }
        }
    }




    public List<HashMap<String,Object>> getData(){
        List<HashMap<String,Object>> list= new ArrayList<HashMap<String, Object>>();
        HashMap<String,Object> map=null;
        File f=new File(mDir);
        if(mDir.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            title_path.setText("/SDcard");
        }
        else {
            String pre=f.getAbsolutePath();
            String[] res=pre.split(Environment.getExternalStorageDirectory().getAbsolutePath());
            title_path.setText("/SDcard"+res[1]);
        }
        File[] files=f.listFiles(new MyFileFilter());
        sort(files);

        if(files!=null){
            for(int i=0;i<files.length;i++){
                map=new HashMap<String, Object>();
                map.put("title",files[i].getName());
                map.put("info", files[i].getAbsolutePath());
                if(files[i].isDirectory()){
                    if(files[i].listFiles()==null||files[i].listFiles().length==0)  //空文件夹
                        map.put("image",R.drawable.empty_folder);
                    else
                        map.put("image", R.drawable.folder);
                }else{
                    String filename=files[i].getName().toLowerCase();
                    if(filename.endsWith(".txt")|filename.endsWith(".doc")){
                        map.put("image",R.drawable.doc);
                    }else if(filename.endsWith(".flv")|filename.endsWith(".mvc")|filename.endsWith(".mp4")){
                        map.put("image",R.drawable.mp4);
                    }else if(filename.endsWith(".mp3")){
                        map.put("image",R.drawable.mp3);
                    }else if(filename.endsWith(".html")){
                        map.put("image",R.drawable.html);
                    }else if(filename.endsWith(".jpg")|filename.endsWith("png")|filename.endsWith("jpeg")){
                        map.put("image",R.drawable.picture);
                    }else{
                        map.put("image",R.drawable.unknow);
                    }
                }
                list.add(map);
            }

        }
        return list;
    }
    class MyAdapter extends BaseAdapter {

        private LayoutInflater lif;

        public MyAdapter(Context con){
            this.lif=LayoutInflater.from(con);

        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vholder=null;
            if(convertView==null){
                vholder=new ViewHolder();
                convertView=lif.inflate(R.layout.activity_send_bluetooth_second_listview_item, null);
                vholder.title=(TextView) convertView.findViewById(R.id.title);
                vholder.image=(ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(vholder);

            }else
                vholder=(ViewHolder) convertView.getTag();
            vholder.title.setText((String)mData.get(position).get("title"));
            vholder.image.setBackgroundResource((Integer)mData.get(position).get("image"));

            return convertView;
        }
    }
    class ViewHolder{
        TextView title;
        TextView info;
        ImageView image;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!mDir.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                mDir=(String)(new File(mDir).getParent());
                mData = getData();
                list2.setAdapter(new MyAdapter(SendWifiAps_SecondActivity.this));
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static File[] sort(File[] listfiles){
        if(listfiles!=null) {
            List<File> list = Arrays.asList(listfiles);
            Collections.sort(list, new FileComparator());
            File[] array = list.toArray(new File[list.size()]);
            return array;
        }else
            return null;
    }
}


