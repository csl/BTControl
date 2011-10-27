package net.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

public class BluetoothService 
{
	private final Handler mHandler;
	private ConnectedClientThread mConnectedThread;
	private OutputStream mmOutStream;
	
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	
	public BluetoothService (Context context, Handler handler, BluetoothSocket socket)
	{
		mHandler = handler;
		mConnectedThread = new ConnectedClientThread(socket);
		mConnectedThread.start();
	}
	
	public void write(byte[] bytes) {
		ConnectedClientThread r;
		synchronized (this) {
			r = mConnectedThread;
		}
		r.write(bytes);
	}
	
	public class ConnectedClientThread extends Thread{
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
	
		public ConnectedClientThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = mmSocket.getInputStream();
				tmpOut = mmSocket.getOutputStream();
			} catch (IOException e) {}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}
		public void run() {
			byte[] buffer = new byte[2048];
			int bytes;
			while (true) {
				try {
					bytes = mmInStream.read(buffer);
					mHandler.obtainMessage 
					(ClientSocketActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				} catch (IOException e) { 
					break;
				}
			}
		}
		
		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {}	
		}
	}
	

}