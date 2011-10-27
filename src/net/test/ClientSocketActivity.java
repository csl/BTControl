package net.test;

import java.io.IOException;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientSocketActivity extends Activity implements OnClickListener
{
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	public TextView textview;
	private EditText edittext;
	private BluetoothSocket socket;
	
	private BluetoothService mService = null;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public String Stringx;

	
	private final Handler mHandler = new Handler() {
		public void handleMessage (Message msg) {
			switch (msg.what) {
//			case MESSAGE_WRITE:
//				byte[] writeBuf = (byte[]) msg.obj;
//				String writeMessage = new String(writeBuf);
//				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;				
				String readMessage = new String (readBuf, 0, msg.arg1);
				Stringx = readMessage;
				String text = (String) textview.getText();
				text = text + readMessage;
				textview.setText(text);
				break;
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_OK)
		{	
			return;
		}
		final BluetoothDevice device = data
		.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		
		new Thread()
		{
			public void run()
			{
				// 連接選中的設備
				connect(device);
			};
		}.start();
	}
	
	protected void connect(BluetoothDevice device)
	{
		bluetoothAdapter.cancelDiscovery();
		socket = null;
		try
		{
			socket = device.createRfcommSocketToServiceRecord
			(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		}
		catch (IOException e){
			finish();
			return;
		}
		try
		{
			socket.connect();
		}
		catch (IOException connectException){
			finish();
			return;
		}	
		mService = new BluetoothService(this, mHandler, socket);
	}
	
	@Override
	public void onClick(View view) 
	{
		String message;
		byte[] buf;

		switch (view.getId())
		{
			case R.id.up:
				//message = "BT1011"; 
				//buf = convert(message);
				buf = new  byte[8];
				buf[0] = (byte) 0x42;
				buf[1] = (byte) 0x54;
				buf[2] = (byte) 0x31;
				buf[3] = (byte) 0x30;
				buf[4] = (byte) 0x31;
				buf[5] = (byte) 0x31;
				buf[6] = (byte) 0x0D;
				buf[7] = (byte) 0x0A;

				mService.write(buf);
				//textview.setText(buf);
				break;
			case R.id.down:
				message = "BT1021"; 
				buf = convert(message);
				mService.write(buf);
				textview.setText(message);
				break;
			case R.id.stop:
				message = "BT1020"; 
				buf = convert(message);
				mService.write(buf);
				textview.setText(message);
				break;
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		try	{
			socket.close();
		}
		catch (IOException e){}
	}
	
	public byte[] convert(String msg)
	{
		byte[] send = new byte[8];
       byte[] theByteArray = msg.getBytes();
        
		for (int i=0; i<6; i++)
		{
			send[i] = theByteArray[i];
		}	
		
		send[6] = (byte) 0x0D;
		send[7] = (byte) 0x0A;

		return send;
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.client_socket);
		
		Button up = (Button) findViewById(R.id.up);
		Button down = (Button) findViewById(R.id.down);
		Button stop = (Button) findViewById(R.id.stop);
		
		up.setOnClickListener(this);
		down.setOnClickListener(this);
		stop.setOnClickListener(this);

		textview = (TextView) findViewById(R.id.textview1);	
/*		if (!bluetooth.isEnabled())
		{
			finish();
			return;
		}
*/		
		// 伺服器的藍牙設備ん
		Toast clientsocket = Toast.makeText(this, "選取已搜尋到的藍牙裝置", Toast.LENGTH_SHORT);
		clientsocket.show();
		// 跳轉到搜索的藍牙設備列表區進行選擇
		Intent intent = new Intent(this, DiscoveryActivity.class);
		startActivityForResult(intent, 0x1);
	}
}