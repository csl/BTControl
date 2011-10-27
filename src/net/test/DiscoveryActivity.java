package net.test;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;


public class DiscoveryActivity extends Activity implements OnClickListener, OnItemClickListener
{
	private volatile boolean discoveryFinished;
	private Handler _handler = new Handler();
	private ListView list;
	List<String> xxx = new ArrayList<String>();
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
	.getDefaultAdapter();
	
	private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

	private Runnable discoveryWorkder = new Runnable()
	{
		public void run()
		{
			// 開始搜索
			bluetoothAdapter.startDiscovery();
			while(true)
			{
				if (discoveryFinished)
				{
					break;
				}
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					finish(); 
				}
			}
		}
	};
	
	private BroadcastReceiver foundReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent) 
		{
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			bluetoothDevices.add(device);
		}
	};
	
	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent) 
		{
			unregisterReceiver(foundReceiver);
			unregisterReceiver(this);
			discoveryFinished = true;
		}
	};
	
	protected void showDevices()
	{
		for (int i = 0, size = bluetoothDevices.size() ; i < size; ++i)
		{
			StringBuilder b = new StringBuilder();
			BluetoothDevice d = bluetoothDevices.get(i);
			b.append(d.getAddress());
			b.append('\n');
			b.append(d.getName());
			String s = b.toString();
			xxx.add(s);
		}
		
		final ArrayAdapter<String> Data = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, xxx);
		
		_handler.post(new Runnable()
		{
			public void run()
			{
				list.setAdapter(Data);
			}
		});
	}
	
	public void onClick(View view) 
	{
		switch(view.getId())
		{			
			case R.id.SHOWLIST:
				showDevices();
				break;
		}
	}
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);      
		setContentView(R.layout.discovery);

		Button SHOWLIST = (Button) findViewById(R.id.SHOWLIST);
		SHOWLIST.setOnClickListener(this);	
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
		
		IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(discoveryReceiver, discoveryFilter);
		registerReceiver(foundReceiver, foundFilter);
		
		SamplesUtils.indeterminate(DiscoveryActivity.this, _handler,
				"正在掃描...", discoveryWorkder, new OnDismissListener()
				{
					public void onDismiss(DialogInterface dialog)
					{

						for (; bluetoothAdapter.isDiscovering();)
						{
							bluetoothAdapter.cancelDiscovery();
						}
						discoveryFinished = true;
					}
				}, true);
    }
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		Intent result = new Intent();
		result.putExtra(BluetoothDevice.EXTRA_DEVICE, bluetoothDevices
				.get(position));
		setResult(RESULT_OK, result);
		finish();
	}
}