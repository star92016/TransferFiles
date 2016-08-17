package com.application.gritstone.transferfiles;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SendBluetoothSecondActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{

    private ListView list2=null;
    private static boolean exit=false;
    public List<HashMap<String,Object>> mData=null;
    public  String mDir= Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView title_path;
    private Context context=null;
    private String current_file;
    private String pass_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("aaaaaaaaaaaaaaaaa","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        setContentView(R.layout.activity_send_bluetooth_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        context=SendBluetoothSecondActivity.this;
        Intent intent=this.getIntent();
        pass_string=intent.getStringExtra("bluetoothAddress");
        title_path= (TextView) findViewById(R.id.title_path2);
        list2= (ListView) findViewById(R.id.listview2);
        mData=getData();
        list2.setAdapter(new MyAdapter(context));
        list2.setOnItemClickListener(new MyOnItemClickListener());
        list2.setOnItemLongClickListener(this);
    }

    ListView.OnCreateContextMenuListener menuList= new ListView.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//            contextMenu.setHeaderTitle("操作"+fileType);
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
        String filrpath= (String) mData.get(position).get("info");

        //点击发送文件
        switch(item.getItemId()){
            case R.id.send:
                Intent intent=new Intent();
                intent.setClass(SendBluetoothSecondActivity.this,SendBluetoothThirdActivity.class);
                intent.putExtra("bluetoothAddress",pass_string);
                intent.putExtra("sendfile",filrpath);
                File f=new File(filrpath);
                intent.putExtra("isDirectory",""+f.isDirectory());
                startActivity(intent);
                break;
        }

        return super.onContextItemSelected(item);
    }



    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if((Integer)mData.get(position).get("image")==R.drawable.empty_folder||(Integer)mData.get(position).get("image")==R.drawable.folder){
                mDir=(String) mData.get(position).get("info");
                mData=getData();
                list2.setAdapter(new MyAdapter(SendBluetoothSecondActivity.this));
            }
        }
    }




    public List<HashMap<String,Object>> getData(){
        List<HashMap<String,Object>> list= new ArrayList<HashMap<String, Object>>();
        HashMap<String,Object> map=null;
        File f=new File(mDir);
        if(mDir.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            title_path.setText("/手机存储");
        }
        else {
            String pre=f.getAbsolutePath();
            String[] res=pre.split(Environment.getExternalStorageDirectory().getAbsolutePath());
            title_path.setText("/手机存储"+res[1]);
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
                list2.setAdapter(new MyAdapter(SendBluetoothSecondActivity.this));
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
            navigateUpTo(new Intent(this, SendBluetoothFirstActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class FileComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        //1.比较文件夹和文件夹，且以A-Z顺序排列
        if(file1.isDirectory()&&file2.isDirectory()){
            return file1.getName().compareToIgnoreCase(file2.getName());
        }else{
            //2.比较文件夹和文件
            if(file1.isDirectory()&&!file2.isDirectory()){
                return -1;
            }else{
                //3.比较文件和文件夹
                if(!file1.isDirectory()&&file2.isDirectory()){
                    return 1;
                }else{
                    //4.比较文件和文件
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            }
        }
    }

}

class MyFileFilter implements FileFilter {

    //	@Override
    public boolean accept(File pathname) {
        if (!pathname.getName().startsWith(".")) {
            return true;
        } else {
            return false;
        }
    }

}
