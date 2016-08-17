package com.application.gritstone.transferfiles;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ViewFlipper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ExpandableListView expandableListView;
    private List<String> group_list;
    private List<String> item_lt1;
    private List<String> item_lt2;
    private List<String> item_lt3;
    private List<List<String>> item_list;
    private List<List<Integer>> item_list2;
    private MyExpandableListViewAdapter adapter;
    private ViewFlipper flipper;
    public Animation[] animations=new Animation[4];
    private int[] resId = {R.drawable.ic_header1,R.drawable.ic_header2,R.drawable.ic_header3,R.drawable.ic_header4};
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp=getSharedPreferences("config",Context.MODE_PRIVATE);
        if(sp.getBoolean("isfirst",true)){
            finish();
            startActivity(new Intent(this,LogoActivity.class));
            return;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        group_list = new ArrayList<String>();
        group_list.add("文件管理");
        group_list.add("文件发送");
        group_list.add("文件接收");

        item_lt1 = new ArrayList<String>();
        item_lt1.add("文件管理");

        item_lt2 = new ArrayList<String>();
        item_lt2.add("Wifi发送");
        item_lt2.add("热点发送");
        item_lt2.add("蓝牙发送");
        //item_lt2.add("GPRS网络发送");

        item_lt3 = new ArrayList<String>();
        item_lt3.add("Wifi接收");
        item_lt3.add("热点接收");
        item_lt3.add("蓝牙接收");
        //item_lt3.add("GPRS网络接收");

        item_list = new ArrayList<List<String>>();
        item_list.add(item_lt1);
        item_list.add(item_lt2);
        item_list.add(item_lt3);

        List<Integer> tmp_list1 = new ArrayList<Integer>();
        tmp_list1.add(R.drawable.ic_filemanager);

        List<Integer> tmp_list2 = new ArrayList<Integer>();
        tmp_list2.add(R.drawable.ic_wifi1);
        tmp_list2.add(R.drawable.ic_wifiap1);
        tmp_list2.add(R.drawable.ic_bluetooth1);
        //tmp_list2.add(R.drawable.ic_gprs);

        List<Integer> tmp_list3 = new ArrayList<Integer>();
        tmp_list3.add(R.drawable.ic_wifi2);
        tmp_list3.add(R.drawable.ic_wifiap2);
        tmp_list3.add(R.drawable.ic_bluetooth2);
        //tmp_list3.add(R.drawable.ic_gprs);


        item_list2 = new ArrayList<List<Integer>>();
        item_list2.add(tmp_list1);
        item_list2.add(tmp_list2);
        item_list2.add(tmp_list3);

        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);

        // 监听组点击
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @SuppressLint("NewApi")
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                if (item_list.get(groupPosition).isEmpty())
                {
                    return true;
                }
                return false;
            }
        });

        // 监听每个分组里子控件的点击事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                if(adapter.getChild(groupPosition, childPosition)=="文件管理"){
                    Intent fileManageIntent=new Intent(MainActivity.this, FileManageActivity.class);
                    MainActivity.this.startActivity(fileManageIntent);
                }else if(adapter.getChild(groupPosition, childPosition)=="Wifi发送"){
                    Intent sendWifiIntent=new Intent(MainActivity.this, SendWifiActivity.class);
                    MainActivity.this.startActivity(sendWifiIntent);
                }else if(adapter.getChild(groupPosition, childPosition)=="热点发送"){
                    Intent sendWifiApIntent=new Intent(MainActivity.this, SendWifiApActivity.class);
                    MainActivity.this.startActivity(sendWifiApIntent);
                }else if(adapter.getChild(groupPosition, childPosition)=="蓝牙发送"){
                    Intent sendBluetoothIntent=new Intent(MainActivity.this, SendBluetoothFirstActivity.class);
                    MainActivity.this.startActivity(sendBluetoothIntent);
                }else if(adapter.getChild(groupPosition, childPosition)=="Wifi接收"){
                    Intent receiveWifiIntent=new Intent(MainActivity.this, ReceiveWifiActivity.class);
                    MainActivity.this.startActivity(receiveWifiIntent);
                }else if(adapter.getChild(groupPosition, childPosition)=="热点接收"){
                    Intent receiveWifiApIntent=new Intent(MainActivity.this, ReceiveWifiApActivity.class);
                    MainActivity.this.startActivity(receiveWifiApIntent);
                }else if(adapter.getChild(groupPosition, childPosition)=="蓝牙接收"){
                    Intent receiveBluetoothIntent=new Intent(MainActivity.this, ReceiveBluetoothActivity.class);
                    MainActivity.this.startActivity(receiveBluetoothIntent);
                }
                return true;

            }
        });
        adapter = new MyExpandableListViewAdapter(this);
        expandableListView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast toast = Toast.makeText(MainActivity.this, "软件介绍：本软件是一款主要用于文件管理和文件传输的应用；文件管理功能可以实现对手机的SD卡和内置存储的文件的有效管理，可以进行复制、粘贴、剪切、移动、重命名等操作；文件传输功能可以实现在有网和无网的状态下的手机对手机、手机对PC的文件传输功能，有蓝牙传输、热点传输、Wifi传输、流量传输四种方式以供选择。", Toast.LENGTH_LONG);
                    // 创建Layout，并设置为水平布局
                    LinearLayout mLayout = new LinearLayout(MainActivity.this);
                    mLayout.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView mImage = new ImageView(MainActivity.this);// 用于显示图像的ImageView
                    mImage.setImageResource(R.drawable.ic_commentators);
                    View toastView = toast.getView(); // 获取显示文字的Toast View
                    mLayout.addView(mImage); // 添加到Layout
                    mLayout.addView(toastView);
                    //设置Toast显示的View(上面生成的Layout).
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setView(mLayout);
                    toast.show();
                }
            });
        }

        flipper = (ViewFlipper) findViewById(R.id.flipper);
        //动态导入的方式为ViewFlipper加入子View
        /*for(int i:resId){
            ImageView imageView=new ImageView(this);
            imageView.setImageResource(i);
            flipper.addView(imageView);
        }*/
        for (int i = 0; i < resId.length; i++) {
            flipper.addView(getImageView(resId[i]));
        }
        animations[0]= AnimationUtils.loadAnimation(this, R.anim.layout_left_in);
        animations[1]= AnimationUtils.loadAnimation(this, R.anim.layout_left_out);
        animations[2]= AnimationUtils.loadAnimation(this, R.anim.layout_right_in);
        animations[3]= AnimationUtils.loadAnimation(this, R.anim.layout_right_out);
        //flipper.setInAnimation(this, R.anim.layout_left_in);
        //flipper.setOutAnimation(this, R.anim.layout_left_out);
        flipper.setInAnimation(animations[0]);
        flipper.setOutAnimation(animations[1]);
        flipper.setFlipInterval(5000);
        flipper.startFlipping();
        flipper.setOnTouchListener(new flipperOnTouchListener());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private ImageView getImageView(int resId){
        ImageView image = new ImageView(this);
        image.setBackgroundResource(resId);
        return image;
    }

    //手势控制，实现OnTouchListener事件
    class flipperOnTouchListener implements View.OnTouchListener {
        private int  start;
        public boolean onTouch(View v, MotionEvent event){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    flipper.stopFlipping();
                    start=(int)event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if(start-event.getX()>0){
                        flipper.setInAnimation(animations[2]);
                        flipper.setOutAnimation(animations[3]);
                        flipper.showPrevious();
                    }
                    if(start-event.getX()<=0){
                        flipper.showNext();
                    }
                    flipper.startFlipping();
                    flipper.setInAnimation(animations[0]);
                    flipper.setOutAnimation(animations[1]);
                    break;
            }
            return true;
        }
    }

    // 用过ListView的人一定很熟悉，只不过这里是BaseExpandableListAdapter
    class MyExpandableListViewAdapter extends BaseExpandableListAdapter
    {
        private Context context;
        public MyExpandableListViewAdapter(Context context)
        {
            this.context = context;
        }
        /**
         * 获取组的个数
         */
        @Override
        public int getGroupCount()
        {
            return group_list.size();
        }
        /**
         * 获取指定组中的子元素个数
         */
        @Override
        public int getChildrenCount(int groupPosition)
        {
            return item_list.get(groupPosition).size();
        }

        /**
         * 获取指定组中的数据
         */
        @Override
        public Object getGroup(int groupPosition)
        {
            return group_list.get(groupPosition);
        }
        /**
         * 获取指定组中的指定子元素数据。
         */
        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            return item_list.get(groupPosition).get(childPosition);
        }
        /**
         * 获取指定组的ID，这个组ID必须是唯一的
         */
        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        /**
         * 获取指定组中的指定子元素ID
         */
        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        /**
         * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
         */
        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        /**
         * 获取显示指定组的视图对象
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            GroupHolder groupHolder = null;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.expendlist_group, null);
                groupHolder = new GroupHolder();
                groupHolder.txt = (TextView)convertView.findViewById(R.id.txt);
                groupHolder.img = (ImageView)convertView.findViewById(R.id.img);
                convertView.setTag(groupHolder);
            }
            else
            {
                groupHolder = (GroupHolder)convertView.getTag();
            }

            if(groupPosition==0){
                groupHolder.img.setImageResource(R.drawable.ic_filemanage);
            }
            if(groupPosition==1){
                groupHolder.img.setImageResource(R.drawable.ic_filesend);
            }
            if(groupPosition==2){
                groupHolder.img.setImageResource(R.drawable.ic_filerecive);
            }
            /*}
            else
            {}*/
            groupHolder.txt.setText(group_list.get(groupPosition));
            return convertView;
        }

        /**
         * 获取一个视图对象，显示指定组中的指定子元素数据。
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            ItemHolder itemHolder = null;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.expendlist_item, null);
                itemHolder = new ItemHolder();
                itemHolder.txt = (TextView)convertView.findViewById(R.id.txt);
                itemHolder.img = (ImageView)convertView.findViewById(R.id.img);
                convertView.setTag(itemHolder);
            }
            else
            {
                itemHolder = (ItemHolder)convertView.getTag();
            }
            itemHolder.txt.setText(item_list.get(groupPosition).get(childPosition));
            itemHolder.img.setImageResource(item_list2.get(groupPosition).get(childPosition));
            return convertView;
        }

        /**
         * 是否选中指定位置上的子元素。
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }

    }

    class GroupHolder
    {
        public TextView txt;
        public ImageView img;
    }

    class ItemHolder
    {
        public ImageView img;
        public TextView txt;
    }


    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        /*new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        MainActivity.this.finish();

                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();*/
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("退出软件");
        builder.setMessage("确认退出吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int which)
            {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        SubMenu subMenu = menu.addSubMenu("");
        subMenu.add("摇一摇").setIcon(R.drawable.ic_yaoyiyao).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem item)
            {
                Log.v("aaaaaa","aaaaaaa");
                return false;
            }
        });
        subMenu.add("扫一扫").setIcon(R.drawable.ic_shaoyishao).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem item)
            {
                return false;
            }
        });
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
        MenuItem item = subMenu.getItem();
        item.setIcon(R.drawable.ic_menu_moreoverflow);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_add){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent introduceIntent = new Intent(MainActivity.this, IntroduceActivity.class);
            MainActivity.this.startActivity(introduceIntent);
        } else if (id == R.id.nav_gallery) {
            Intent operationIntent = new Intent(MainActivity.this, OperationActivity.class);
            MainActivity.this.startActivity(operationIntent);
        } else if (id == R.id.nav_slideshow) {
            Intent versionIntent = new Intent(MainActivity.this, VersionActivity.class);
            MainActivity.this.startActivity(versionIntent);
        } else if (id == R.id.nav_manage) {
            Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
            MainActivity.this.startActivity(aboutIntent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            onBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 显示overflower菜单图标
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

}
