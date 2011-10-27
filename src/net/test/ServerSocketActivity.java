package net.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ServerSocketActivity extends Activity implements OnClickListener
{
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
	.getDefaultAdapter();
	private OutputStream mmOutStream1;
	private OutputStream mmOutStream2;
	private InputStream mmInStream1;
	private InputStream mmInStream2;
	List<String> xxx = new ArrayList<String>();
	private BluetoothDevice BT1;
	private BluetoothDevice BT2;
	private TextView text1;
	private TextView text2;
	private EditText edit1;
	private EditText edit2;
	private BluetoothSocket socket1;
	private BluetoothSocket socket2;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_socket);
//		list = (ListView) findViewById(R.id.pairedlist);
//		list.setOnItemClickListener(this);
		Button socketsocket = (Button) findViewById(R.id.socket11);
		Button socketsocket2 = (Button) findViewById(R.id.socket22);
		socketsocket.setOnClickListener(this);
		socketsocket2.setOnClickListener(this);
		text1 = (TextView) findViewById (R.id.textview1);
		text2 = (TextView) findViewById (R.id.textview2);
		edit1 = (EditText) findViewById(R.id.edittext1);	
		edit2 = (EditText) findViewById(R.id.edittext2);	
		BT1 = bluetoothAdapter.getRemoteDevice("00:1A:FF:09:02:63");
		BT2 = bluetoothAdapter.getRemoteDevice("00:1A:FF:09:02:64");
		
		bluetoothAdapter.cancelDiscovery();
		try
		{
			socket1 = BT1.createRfcommSocketToServiceRecord
			(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		}
		catch (IOException e){
			finish();
			return;
		}
		try
		{
			socket2 = BT2.createRfcommSocketToServiceRecord
			(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		}
		catch (IOException e){
			finish();
			return;
		}
		try
		{
			socket1.connect();
		}
		catch (IOException connectException){
			finish();
			return;
		}	
		try
		{
			socket2.connect();
		}
		catch (IOException connectException){
			finish();
			return;
		}	
		try
		{
			mmOutStream1 = socket1.getOutputStream();
			mmInStream1 = socket1.getInputStream();
		}
		catch (IOException e){
			return;
		}
		try
		{
			mmOutStream2 = socket2.getOutputStream();
			mmInStream2 = socket2.getInputStream();
		}
		catch (IOException e){
			return;
		}

		
	}
	
	public void write(OutputStream output, InputStream input, TextView text,byte[] bytes) {
		
		try {
			output.write(bytes);
			
		} catch (IOException e) {}
		try {
			byte[] buffer = new byte[1024];
			int bytes1;
			bytes1 = input.read(buffer);
			String readMessage = new String(buffer , 0, bytes1);
			text.setText(readMessage);
			Toast clientsocket = Toast.makeText(this, String.valueOf(bytes1), Toast.LENGTH_SHORT);
			clientsocket.show();
		} catch (IOException e) {}		
	}
	@Override
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.socket11:
				String message1 = edit1.getText().toString(); 
				byte[] send1 = message1.getBytes();
				write(mmOutStream1,mmInStream1,text1,send1);
				break;
			case R.id.socket22:
				String message2 = edit2.getText().toString(); 
				byte[] send2 = message2.getBytes();
				write(mmOutStream2,mmInStream2,text2,send2);
				break;
		}
	}


}