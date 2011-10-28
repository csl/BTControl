package net.test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class main extends Activity implements OnClickListener{

	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.btnOpenBluetooth:
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, 0x1);
				break;
			case R.id.btnCloseBluetooth:
				bluetoothAdapter.disable();
				break;
			case R.id.btnSearchBluetooth:
				Intent searchIntent = new Intent(this, DiscoveryActivity.class);
				startActivity(searchIntent);
				break;
			case R.id.btnClientSocket:
				Intent clientSocketIntent = new Intent(this, ClientSocketActivity.class);
				startActivity(clientSocketIntent);
				break;
			case R.id.exit:
				bluetoothAdapter.disable();
				finish();
				break;
		}
		
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);

		bluetoothAdapter.disable();
        
		Button btnOpenBluetooth = (Button) findViewById(R.id.btnOpenBluetooth);
		Button btnCloseBluetooth = (Button) findViewById(R.id.btnCloseBluetooth);
		Button btnSearchBluetooth = (Button) findViewById(R.id.btnSearchBluetooth);
		Button btnClientSocket = (Button) findViewById(R.id.btnClientSocket);
		Button exit = (Button) findViewById(R.id.exit);
		btnOpenBluetooth.setOnClickListener(this);
		btnCloseBluetooth.setOnClickListener(this);
		btnSearchBluetooth.setOnClickListener(this);
		btnClientSocket.setOnClickListener(this);
		exit.setOnClickListener(this);

    }
}