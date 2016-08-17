package com.application.gritstone.transferfiles;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.gritstone.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by guorenjie on 2016/5/26.
 */


public class FileManager_PasteActivity extends AppCompatActivity {
    private ListView list2;
    private static boolean exit=false;
    public  List<HashMap<String,Object>> mData=null;
    public  String mDir= Environment.getExternalStorageDirectory().getAbsolutePath();
    private  TextView title_path;
    private Context context;
    private String current_file;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.pasteview);
        context=FileManager_PasteActivity.this;
        title_path= (TextView) findViewById(R.id.title_path1_2);
        list2= (ListView) findViewById(R.id.listview1_2);
        Intent intent=getIntent();
        current_file=intent.getStringExtra("current_file");
//        sourcefilename=current_file.substring(current_file.lastIndexOf("/"),current_file.length()-1);
        File source_file=new File(current_file);
        mDir=source_file.getParent();
        mData=getData();
        list2.setAdapter(new MyAdapter(context));
        list2.setOnItemClickListener(new MyOnItemClickListener());
    }

    public void cancel(View v){
        Intent intent=new Intent();
        intent.putExtra("destiny_path",mDir);
        setResult(1,intent);
        finish();
    }

    public void paste_to(View v){
//        File file=new File(mDir+"/"+sourcefilename);
        copy(current_file,mDir);
        Intent intent=new Intent();
        intent.putExtra("destiny_path",mDir);
        setResult(1,intent);
        finish();
    }

    public int copy(String fromFile, String toFile)
    {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if(!root.exists())
        {
//            Toast.makeText(context,"当前目录中已存在该文件或者文件夹！",Toast.LENGTH_SHORT).show();
            return -1;
        }
        if(root.isDirectory()) {
            //如果存在则获取当前目录下的全部文件 填充数组
            currentFiles = root.listFiles();
            //目标目录
            File targetDir = new File(toFile+"/"+root.getName());
            //创建目录
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < currentFiles.length; i++) {
                if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
                {
                    copy(currentFiles[i].getAbsolutePath() , toFile +"/"+ root.getName());
                } else//如果当前项为文件则进行文件拷贝
                {
                    CopySdcardFile(currentFiles[i].getPath(), toFile +"/"+new File(currentFiles[i].getParent()).getName()+"/"+ currentFiles[i].getName());
                }
            }
        }else{
            if((new File(toFile+"/"+root.getName())).exists()){
                Toast.makeText(context,"当前目录中已存在该文件或者文件夹！",Toast.LENGTH_SHORT).show();
                return -1;
            }
            CopySdcardFile(root.getAbsolutePath(),toFile+"/"+root.getName());
        }
        return 0;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopySdcardFile(String fromFile, String toFile)
    {

        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex)
        {
            return -1;
        }
    }




    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if((Integer)mData.get(position).get("image")==R.drawable.empty_folder||(Integer)mData.get(position).get("image")==R.drawable.folder){
                mDir=(String) mData.get(position).get("info");
                mData=getData();
                list2.setAdapter(new MyAdapter(context));
            }
        }
    }

    public void createfolder2(View v){
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        final EditText edit=new EditText(context);
        dialog.setView(edit);
        dialog.setTitle("新建文件夹");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String filename=edit.getText().toString();
                if(filename!=null&&!"".equals(filename)){
                    File file=new File(mDir+"/"+filename);
                    if(!file.exists()){
                        file.mkdirs();
                        HashMap<String,Object> map=new HashMap<String, Object>();
                        map.put("title",filename);
                        map.put("info",mDir+"/"+filename);
                        map.put("image",R.drawable.empty_folder);
                        mData.add(map);
                        list2.setAdapter(new MyAdapter(context));
                        Toast.makeText(context,"创建成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"已有同名文件存在,请重命名!",Toast.LENGTH_SHORT);
                    }
                }else{
                    Toast.makeText(context,"创建成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    public List<HashMap<String,Object>> getData(){
        List<HashMap<String,Object>> list= new ArrayList<HashMap<String, Object>>();
        HashMap<String,Object> map=null;
        File f=new File(mDir);
        if(mDir.equals(Environment.getExternalStorageDirectory().getAbsolutePath()))
            title_path.setText("/SDcard");
        else {
            String pre=f.getAbsolutePath();
            String[] res=pre.split(Environment.getExternalStorageDirectory().getAbsolutePath());
            title_path.setText("/SDcard"+res[1]);
        }
        File[] files=f.listFiles(new MyFileFilter());
        FileUtil.sort(files);

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
                    if(filename.endsWith(".txt")){
                        map.put("image",R.drawable.doc);
                    }else if(filename.endsWith(".flv")|filename.endsWith(".mvc")|filename.endsWith(".mp4")){
                        map.put("image",R.drawable.mp4);
                    }else if(filename.endsWith(".mp3")){
                        map.put("image",R.drawable.mp3);
                    }else if(filename.endsWith(".config")){
                        map.put("image",R.drawable.config);
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
                convertView=lif.inflate(R.layout.file_item, null);
                vholder.title=(TextView) convertView.findViewById(R.id.title);
//                vholder.info=(TextView) convertView.findViewById(R.id.info);
                vholder.image=(ImageView) convertView.findViewById(R.id.img);
//                final CheckBox cb= (CheckBox) convertView.findViewById(R.id.check);
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
//                exit=false;
                mDir=(String)(new File(mDir).getParent());
                mData = getData();
                list2.setAdapter(new MyAdapter(context));
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
