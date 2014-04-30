package dk.escteam.keyboardextender.protocol.bluetooth;

import java.io.IOException;
import java.util.UUID;


import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Looper;

public class KeyboardExtenderConnectionBluetooth extends KeyboardExtenderConnection
{
	private BluetoothSocket socket;
	
	public KeyboardExtenderConnectionBluetooth(BluetoothSocket socket) throws IOException
	{
		super(socket.getInputStream(), socket.getOutputStream());
		
		this.socket = socket;
	}
	
	public static KeyboardExtenderConnectionBluetooth create(KeyboardExtender application, String address) throws IOException
	{
		Looper.prepare();
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		if (adapter != null)
		{
			if (adapter.isEnabled())
			{
				try
				{
					BluetoothDevice device = adapter.getRemoteDevice(address);
					
					if (device != null)
					{
						BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(KeyboardExtenderConnection.BLUETOOTH_UUID));
						socket.connect();
						
						KeyboardExtenderConnectionBluetooth connection = new KeyboardExtenderConnectionBluetooth(socket);
						
						return connection;
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new IOException();
				}
			}
			else
			{
				if (application.requestEnableBluetooth())
				{
					Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					application.startActivity(intent);
				}
			}
		}
		
		throw new IOException();
	}
	
	public void close() throws IOException
	{
		this.socket.close();
		super.close();
	}
}
