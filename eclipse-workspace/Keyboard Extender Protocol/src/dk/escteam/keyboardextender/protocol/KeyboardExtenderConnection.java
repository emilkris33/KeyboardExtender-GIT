package dk.escteam.keyboardextender.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import dk.escteam.keyboardextender.protocol.action.KeyboardExtenderAction;

public abstract class KeyboardExtenderConnection
{
	public static final String BLUETOOTH_UUID = "300ad0a7-059d-4d97-b9a3-eabe5f6af813";
	public static final String DEFAULT_PASSWORD = "azerty";
	
	private DataInputStream dataInputStream;
	private OutputStream outputStream;
	
	public KeyboardExtenderConnection(InputStream inputStream, OutputStream outputStream)
	{
		this.dataInputStream = new DataInputStream(inputStream);
		this.outputStream = outputStream;
	}
	
	public KeyboardExtenderAction receiveAction() throws IOException
	{
		synchronized (this.dataInputStream)
		{
			KeyboardExtenderAction action = KeyboardExtenderAction.parse(this.dataInputStream);
			return action;
		}
	}
	
	public void sendAction(KeyboardExtenderAction action) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		action.toDataOutputStream(new DataOutputStream(baos));
		
		synchronized (this.outputStream)
		{
			this.outputStream.write(baos.toByteArray());
			this.outputStream.flush();
		}
	}
	
	public void close() throws IOException
	{
		this.dataInputStream.close();
		this.outputStream.close();
	}
}
