package com.application.gritstone.transferfiles;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
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

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.application.gritstone.utils.ButtonStateChangeListener;
import com.application.gritstone.utils.FileSizeUtil;
import com.application.gritstone.utils.FileUtil;
import com.application.gritstone.utils.OpenFile;

import java.io.DataInput;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FileManageActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{

    public  List<HashMap<String,Object>> mData=null;
    public  String mDir= Environment.getExternalStorageDirectory().getAbsolutePath();
    private ListView lv;
    private File parentfile;

    private int location;
    private  TextView title_path;
    private Context context;
    private ImageButton createfolder,morefunction;

    private String fileType=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_file_manage);
        context=FileManageActivity.this;
        title_path= (TextView) findViewById(R.id.title_path);
        mData=getData();
        lv=(ListView) findViewById(R.id.listview);
        lv.setAdapter(new MyAdapter(this));
        lv.setOnItemClickListener(new MyOnItemClickListener());
        lv.setOnItemLongClickListener(this);
        createfolder= (ImageButton) findViewById(R.id.createfolder);
        morefunction= (ImageButton) findViewById(R.id.morefunction);
        createfolder.setOnTouchListener(ButtonStateChangeListener.touchListener);
        morefunction.setOnTouchListener(ButtonStateChangeListener.touchListener);
    }

    public void createfolder(View v){
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
                        lv.setAdapter(new MyAdapter(context));
                        Toast.makeText(context,"创建成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"已有同名文件存在,请重命名!",Toast.LENGTH_SHORT).show();
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



    public void morefunction(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.morefunction, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }


    ListView.OnCreateContextMenuListener menuList= new ListView.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//            contextMenu.setHeaderTitle("操作"+fileType);
            MenuInflater mif=getMenuInflater();
            mif.inflate(R.menu.menu_view,contextMenu);
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(new File((String)mData.get(i).get("info")).isDirectory()){
            fileType="文件夹";
        }else{
            fileType="文件";
        }
        lv.setOnCreateContextMenuListener(menuList);
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menu;
        menu= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int position=menu.position;
//        String filrpath= (String) mData.get(position).get("info");


        switch(item.getItemId()){
            case R.id.rename:
                rename(position);
                break;
            case R.id.copy:
//                Toast.makeText(this,"点击了复制",Toast.LENGTH_SHORT).show();
                copy(position);
                break;
            case R.id.delete:
                delete(position);
//                Toast.makeText(this,"点击了粘贴",Toast.LENGTH_SHORT).show();
                break;
            case R.id.detail:
                showDetail(position);
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void showDetail(int position){
        String file_choose= (String) mData.get(position).get("info");
        final AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        File Dfile=new File(file_choose);
        String Fname=Dfile.getName();
        dialog.setTitle(Fname);
        View view=LayoutInflater.from(context).inflate(R.layout.activity_file_manager_filedetail,null);
        dialog.setView(view);
        TextView text1=(TextView) view.findViewById(R.id.fmd_text1);
        TextView text2=(TextView) view.findViewById(R.id.fmd_text2);
        TextView text3=(TextView) view.findViewById(R.id.fmd_text3);
        String fileSize=null;
        fileSize=FileSizeUtil.getAutoFileOrFilesSize(file_choose);
        Date data=new Date(Dfile.lastModified());
        if(Dfile.isDirectory()){
            //文件夹处理
            text1.setText("大小："+fileSize);
            text2.setText("内容："+FileSizeUtil.FilesContent(file_choose));
            text3.setText("修改时间："+data.toLocaleString());
        }else{
            //文件处理
            text1.setText("大小："+ fileSize);
            text2.setText("格式："+Fname.substring(Fname.lastIndexOf(".")+1,Fname.length()));
            text3.setText("修改时间："+data.toLocaleString());
        }
        Button bt= (Button) view.findViewById(R.id.fmd_bt);
        final AlertDialog ad=dialog.show();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });
    }

    public void delete(final int position){
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
//        final TextView text=new TextView(context);
//        dialog.setView(text);
//        dialog.setTitle("标题");
        String title;
        String del_path= (String) mData.get(position).get("info");
        final File del_file=new File(del_path);
        if(del_file.isDirectory()) {
//            text.setText("确定要删除该文件夹及其中的所有文件?");
            title="确定要删除该文件夹及其中的所有文件?";
        }else{
//            text.setText("确定要删除该文件?");
            title="确定要删除该文件?";
        }
        dialog.setTitle(title);
        dialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                del_method(del_file);
                Toast.makeText(context,"删除成功!",Toast.LENGTH_SHORT).show();
                mData.remove(position);
                lv.setAdapter(new MyAdapter(context));
            }
        });
        dialog.setNegativeButton("取消",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    public void del_method(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childfiles=file.listFiles();
            if(childfiles==null||childfiles.length==0){
                file.delete();
                return;
            }
            for(int i=0;i<childfiles.length;i++){
                del_method(childfiles[i]);
            }
            file.delete();
        }
    }

    public void rename(final int position){
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        final EditText edit=new EditText(context);
        dialog.setView(edit);
        final String oldpath= (String) mData.get(position).get("info");
        edit.setHint(oldpath.substring(oldpath.lastIndexOf("/")+1,oldpath.length()-1));
        dialog.setTitle("重命名");
//        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edit,0);


        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName=edit.getText().toString();
                if(newName!=null&&!"".equals(newName)){
                    int index=oldpath.lastIndexOf("/");
                    String path1=oldpath.substring(0,index);
                    File file=new File(oldpath);
                    boolean b=file.renameTo(new File(path1+"/"+newName));
                    Log.d("myfile test","oldpath:"+oldpath);
                    Log.d("myfile test","path1:"+path1+" ,newName:"+newName);
                    if(b){
                        Toast.makeText(context,"该文件已重命名为"+newName,Toast.LENGTH_SHORT).show();
                        //更新mData
                        mData.get(position).put("info",path1+"/"+newName);
                        mData.get(position).put("title",newName);
                        Log.d("myfile test", (String) mData.get(position).get("info"));
                        lv.setAdapter(new MyAdapter(context));
                    }else{
                        Toast.makeText(context,"重命名失败！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,"文件名不能为空!",Toast.LENGTH_SHORT).show();
                }
            }
        });dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();
    }

    public void copy(int position){
        Intent intent=new Intent(FileManageActivity.this,FileManager_PasteActivity.class);
        intent.putExtra("current_file",(String)mData.get(position).get("info"));
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                Bundle bundle=data.getExtras();
                if(bundle!=null){
                    mDir=bundle.getString("destiny_path");
                    mData=getData();
//                    Toast.makeText(context,"当前地址为"+mDir,Toast.LENGTH_SHORT).show();
                    lv.setAdapter(new MyAdapter(context));
                }
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if((Integer)mData.get(position).get("image")==R.drawable.empty_folder||(Integer)mData.get(position).get("image")==R.drawable.folder){
                mDir=(String) mData.get(position).get("info");
                mData=getData();
                lv.setAdapter(new MyAdapter(context));
            }else{
               //打开文件
                Intent intent=OpenFile.openFile((String) mData.get(position).get("info"));
                startActivity(intent);
            }
        }
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
//        if(!mDir.equals("/")){
//            map=new HashMap<String, Object>();
//            map.put("title", "Back to ../");
//            map.put("info", f.getParent());
//            map.put("image", R.drawable.ex_folder);
//            list.add(map);
//        }
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
                lv.setAdapter(new MyAdapter(context));
            }else{
//                if(!exit){
//                    Toast.makeText(this,"再次点击返回键退出！",Toast.LENGTH_SHORT).show();
//                    exit=true;
//                }else
                    finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


