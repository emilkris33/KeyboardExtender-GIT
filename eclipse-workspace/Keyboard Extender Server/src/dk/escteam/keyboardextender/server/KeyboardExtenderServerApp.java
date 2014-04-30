package dk.escteam.keyboardextender.server;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.prefs.Preferences;

import dk.escteam.keyboardextender.server.connection.KeyboardExtenderServerBluetooth;
import dk.escteam.keyboardextender.server.connection.KeyboardExtenderServerTcp;
import dk.escteam.keyboardextender.server.gui.KeyboardExtenderServerTrayIcon;

public class KeyboardExtenderServerApp
{
	public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");
	
	private Preferences preferences;
	private KeyboardExtenderServerTrayIcon trayIcon;
	private Robot robot;
	
	private KeyboardExtenderServerTcp serverTcp;
	public static KeyboardExtenderServerBluetooth serverBluetooth;
	
	public KeyboardExtenderServerApp() throws AWTException, IOException
	{
		this.preferences = Preferences.userNodeForPackage(this.getClass());
		
		this.robot = new Robot();
		
		this.trayIcon = new KeyboardExtenderServerTrayIcon(this);
		
		try
		{
			this.serverTcp = new KeyboardExtenderServerTcp(this);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			KeyboardExtenderServerApp.serverBluetooth = new KeyboardExtenderServerBluetooth(this);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Preferences getPreferences()
	{
		return preferences;
	}
	
	public KeyboardExtenderServerTrayIcon getTrayIcon()
	{
		return trayIcon;
	}
	
	public Robot getRobot()
	{
		return robot;
	}
	
	public KeyboardExtenderServerTcp getServerTcp()
	{
		return serverTcp;
	}
	
	public KeyboardExtenderServerBluetooth getServerBluetooth()
	{
		return serverBluetooth;
	}
	
	public void exit()
	{
		this.trayIcon.close();
		
		if (this.serverTcp != null)
		{
			this.serverTcp.close();
		}
		
		if (KeyboardExtenderServerApp.serverBluetooth != null)
		{
			KeyboardExtenderServerApp.serverBluetooth.close();
		}
		
		System.exit(0);
	}
	
	public static void main(String[] args)
	{
		try
		{
			KeyboardExtenderServerApp application = new KeyboardExtenderServerApp();
		}
		catch (AWTException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}
