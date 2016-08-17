package com.application.gritstone.transferfiles;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.application.gritstone.utils.RandomTextView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SendBluetoothFirstActivity extends AppCompatActivity {
    public static final String MY_UUID ="00001101-0000-1000-8000-00805F9B34FB";
    OutputStream os=null;
    private BluetoothSocket socket=null;
    BluetoothAdapter bluetoothAdapter=null;
    BluetoothDevice bluetoothDevice=null;
    ArrayList<HashMap<String, Object>> listItem=null;
    ListView lv=null;

    protected void onCreate(Bundle savedInstanceState) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        open(bluetoothAdapter);
        while(bluetoothAdapter.isEnabled()==false){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bluetooth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final RandomTextView randomTextView = (RandomTextView) findViewById(
                R.id.random_textview);
        lv=(ListView)findViewById(R.id.listView);
        listItem = new ArrayList<HashMap<String, Object>>();
        searchPairedDevice(bluetoothAdapter,listItem);
        searchDevice(bluetoothAdapter,listItem);
        updateList(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateList(lv);
                if(listItem.get(position).get("ItemText").equals("未配对")){
                    Toast.makeText(SendBluetoothFirstActivity.this, "设备还未配对，请先到系统蓝牙处配对再使用此功能！", Toast.LENGTH_SHORT).show();
                }else if (listItem.get(position).get("ItemText").equals("已配对")){
                    //bluetoothDevice = bluetoothAdapter.getRemoteDevice((String) listItem.get(position).get("Address"));
                    if(bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();
                    }
                    Intent sendBluetoothIntent=new Intent(SendBluetoothFirstActivity.this, SendBluetoothSecondActivity.class);
                    sendBluetoothIntent.putExtra("bluetoothAddress",(String) listItem.get(position).get("Address"));
                    Log.d("bluetoothAddress",(String) listItem.get(position).get("Address"));
                    SendBluetoothFirstActivity.this.startActivity(sendBluetoothIntent);
                    return;

                }
            }
        });
        Log.v("aaaaaaaaaaaaaaa","yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                for(int i=0;i<listItem.size();i++){
                    randomTextView.addKeyWord((String)listItem.get(i).get("ItemTitle"));
                }
                randomTextView.show();
                //randomTextView.addKeyWord("a");
                //randomTextView.addKeyWord("b");
                //randomTextView.addKeyWord("c");
                //randomTextView.show();
            }
        }, 2 * 1000);
    }

    private void open(BluetoothAdapter adapter){
        if (adapter == null) {
            Toast.makeText(SendBluetoothFirstActivity.this, "设备不支持蓝牙设备！", Toast.LENGTH_SHORT).show();
        }else{
            adapter.enable();
        }
    }

    private void close(BluetoothAdapter adapter){
        adapter.disable();
    }

    private void searchPairedDevice(BluetoothAdapter adapter,ArrayList<HashMap<String, Object>> listItem){
        Log.v("sda",""+adapter.isEnabled());
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.ic_bluetooth);
                map.put("ItemTitle", bluetoothDevice.getName());
                map.put("Address", bluetoothDevice.getAddress());
                map.put("ItemText", "已配对");
                listItem.add(map);
            }
        }
        updateList(lv);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            //Log.v("aaaaaaaaaaaaaaa","bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("ItemImage", R.drawable.ic_bluetooth);// 加入图片
                    map.put("ItemTitle", device.getName());
                    map.put("Address", device.getAddress());
                    map.put("ItemText", "未配对");
                    listItem.add(map);

                }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    setProgressBarIndeterminateVisibility(false);
                    updateList(lv);
                    Log.v("aaaaaaaaaaaaaaa","完毕");
                }
            }
        }
    };

    private void searchDevice(BluetoothAdapter adapter,ArrayList<HashMap<String, Object>> listItem){
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, mFilter);
        // 注册搜索完时的receiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, mFilter);
        // 如果正在搜索，就先取消搜索
        if (adapter.isDiscovering()){
            adapter.cancelDiscovery();
        }
        // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回
        adapter.startDiscovery();
        updateList(lv);
        Log.v("aaaaaaaaaaaaaaa","ddddddddddddddddddddddddddddddd");
    }

    protected void onDestroy() {
        Log.v("aaaaaaaaaaaaaaa","天天天天天天天天天天天天天天天天天天退出");
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    public void updateList(ListView lv) {
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem,
                R.layout.send_bluetooth_listview, new String[] { "ItemImage", "ItemTitle",
                "ItemText" }, new int[] { R.id.imageView1,
                R.id.textView1, R.id.textView2 });
        lv.setAdapter(mSimpleAdapter);// 为ListView绑定适配器
    }


    @Override
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
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}





