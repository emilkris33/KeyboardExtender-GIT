package dk.escteam.keyboardextender.server.gui;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import dk.escteam.keyboardextender.protocol.KeyboardExtenderConnection;
import dk.escteam.keyboardextender.protocol.bluetooth.KeyboardExtenderConnectionBluetooth;
import dk.escteam.keyboardextender.protocol.tcp.KeyboardExtenderConnectionTcp;
import dk.escteam.keyboardextender.server.KeyboardExtenderServerApp;
import dk.escteam.keyboardextender.server.connection.KeyboardExtenderServerBluetooth;

public class KeyboardExtenderServerTrayIcon
{
	private Preferences preferences;
	private KeyboardExtenderServerApp application;
	private TrayIcon trayIcon;
	
	public KeyboardExtenderServerTrayIcon(KeyboardExtenderServerApp application) throws AWTException, IOException
	{
		this.application = application;
		
		this.preferences = this.application.getPreferences();
		
		this.initTrayIcon();
	}
	
	public void notifyConnection(KeyboardExtenderConnection connection)
	{
		String message = "";
		
		if (connection instanceof KeyboardExtenderConnectionTcp)
		{
			KeyboardExtenderConnectionTcp connectionTcp = (KeyboardExtenderConnectionTcp) connection;
			message = connectionTcp.getInetAddress().getHostAddress() + ":" + connectionTcp.getPort();
		}
		else if (connection instanceof KeyboardExtenderConnectionBluetooth)
		{
			message = "Bluetooth";
		}
		
		this.trayIcon.displayMessage("Keyboard Extender", "New connection : " + message, MessageType.INFO);
	}
	
	public void notifyProtocolProblem()
	{
		this.trayIcon.displayMessage("Keyboard Extender", "Protocol problem. Please Download the server again", MessageType.INFO);
	}
	
	public void close()
	{
		SystemTray.getSystemTray().remove(this.trayIcon);
	}
	
	private void initTrayIcon() throws AWTException, IOException
	{
		PopupMenu menu = new PopupMenu();
		
		MenuItem menuItemPassword = new MenuItem("Password");
		menuItemPassword.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String password = KeyboardExtenderServerTrayIcon.this.preferences.get("password", KeyboardExtenderConnection.DEFAULT_PASSWORD);
				password = JOptionPane.showInputDialog("Password", password);
				if (password != null)
				{
					KeyboardExtenderServerTrayIcon.this.preferences.put("password", password);
				}
			}
		});
		menu.add(menuItemPassword);
		
		if (KeyboardExtenderServerApp.IS_WINDOWS)
		{
			final CheckboxMenuItem menuItemUnicodeWindows = new CheckboxMenuItem("Force disable Unicode Windows alt trick", this.preferences.getBoolean("force_disable_unicode_windows_alt_trick", false));
			menuItemUnicodeWindows.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					KeyboardExtenderServerTrayIcon.this.preferences.putBoolean("force_disable_unicode_windows_alt_trick", menuItemUnicodeWindows.getState());
					JOptionPane.showMessageDialog(null, "Restart the connection to apply this preference.");
				}
			});
			menu.add(menuItemUnicodeWindows);
		}
		
		menu.addSeparator();
		
		MenuItem menuItemWifiServer = new MenuItem("Wifi Server");
		menuItemWifiServer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StringBuilder message = new StringBuilder();
				
				if (KeyboardExtenderServerTrayIcon.this.application.getServerTcp() != null)
				{
					message.append("Wifi server is listening on :\n");
					message.append(KeyboardExtenderServerTrayIcon.this.getTcpListenAddresses());
				}
				else
				{
					message.append("Wifi server is not running");
				}
				
				JOptionPane.showMessageDialog(null, message.toString());
			}
		});
		menu.add(menuItemWifiServer);
		
		MenuItem menuItemPort = new MenuItem("Port");
		menuItemPort.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					int port = KeyboardExtenderServerTrayIcon.this.preferences.getInt("port", KeyboardExtenderConnectionTcp.DEFAULT_PORT);
					String newPortString = JOptionPane.showInputDialog("Port", port);
					int newPort = Integer.parseInt(newPortString);
					KeyboardExtenderServerTrayIcon.this.preferences.putInt("port", newPort);
					JOptionPane.showMessageDialog(null, "Restart the server to apply the new port.");
				}
				catch (NumberFormatException nfe)
				{
					nfe.printStackTrace();
				}
			}
		});
		menu.add(menuItemPort);
		
		menu.addSeparator();
		
		MenuItem menuItemBluetoothServer = new MenuItem("Bluetooth Server");
		menuItemBluetoothServer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				StringBuilder message = new StringBuilder();
				
				if (KeyboardExtenderServerTrayIcon.this.application.getServerBluetooth() != null)
				{
					message.append("Bluetooth server is running");
				}
				else
				{
					try
					{
						KeyboardExtenderServerApp.serverBluetooth = new KeyboardExtenderServerBluetooth(application);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					message.append("Atempting to start Bluetooth Server");
				}
				
				JOptionPane.showMessageDialog(null, message.toString());
			}
		});
		menu.add(menuItemBluetoothServer);
		
		menu.addSeparator();
		
		MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				KeyboardExtenderServerTrayIcon.this.application.exit();
			}
		});
		menu.add(menuItemExit);
		
		this.trayIcon = new TrayIcon(ImageIO.read(this.getClass().getResourceAsStream("icon.png")));
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.setToolTip("Keyboard Extender");
		this.trayIcon.setPopupMenu(menu);
		
		SystemTray.getSystemTray().add(this.trayIcon);
		
		StringBuilder message = new StringBuilder("Server started\n");
		message.append(this.getTcpListenAddresses());
		
		this.trayIcon.displayMessage("Keyboard Extender", message.toString(), TrayIcon.MessageType.INFO);
	}
	
	private String getTcpListenAddresses()
	{
		int port = this.preferences.getInt("port", KeyboardExtenderConnectionTcp.DEFAULT_PORT);
		
		StringBuilder message = new StringBuilder();
		
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				NetworkInterface currentInterface = interfaces.nextElement();
				
				Enumeration<InetAddress> addresses = currentInterface.getInetAddresses();
				
				while (addresses.hasMoreElements())
				{
					InetAddress currentAddress = addresses.nextElement();
					
					if (!currentAddress.isLoopbackAddress() && !(currentAddress instanceof Inet6Address))
					{
						message.append(currentAddress.getHostAddress() + ":" + port + "\n");
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return message.toString();
	}
}
