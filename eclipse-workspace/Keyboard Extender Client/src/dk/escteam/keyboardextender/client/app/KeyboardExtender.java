package dk.escteam.keyboardextender.client.app;

import java.io.IOException;
import java.util.HashSet;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.connection.Connection;
import dk.escteam.keyboardextender.client.connection.ConnectionList;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderActionReceiver;
import dk.escteam.keyboardextender.protocol.KeyboardExtenderConnection;
import dk.escteam.keyboardextender.protocol.action.AuthentificationAction;
import dk.escteam.keyboardextender.protocol.action.AuthentificationResponseAction;
import dk.escteam.keyboardextender.protocol.action.KeyboardExtenderAction;

public class KeyboardExtender extends Application implements Runnable
{
	private static final long CONNECTION_CLOSE_DELAY = 3000;
	
	private SharedPreferences preferences;
	private Vibrator vibrator;
	
	private KeyboardExtenderConnection[] connection;
	
	private HashSet<KeyboardExtenderActionReceiver> actionReceivers;
	
	private Handler handler;
	
	private CloseConnectionScheduler closeConnectionScheduler;
	
	private ConnectionList connections;
	
	private boolean requestEnableBluetooth;
	
	public void onCreate()
	{
		super.onCreate();
		
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
		
		this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		this.actionReceivers = new HashSet<KeyboardExtenderActionReceiver>();
		
		this.handler = new Handler();
		
		this.connection = new KeyboardExtenderConnection[1];
		
		this.closeConnectionScheduler = new CloseConnectionScheduler();
		
		this.connections = new ConnectionList(this.preferences);
		
		this.requestEnableBluetooth = true;
	}
	
	public CharSequence getPageTitle(int position)
	{
		return (CharSequence) this.preferences.getStringSet("active_screens", null).toArray()[0];
	}
	
	public SharedPreferences getPreferences()
	{
		return this.preferences;
	}
	
	public ConnectionList getConnections()
	{
		return this.connections;
	}
	
	public void vibrate(long l)
	{
		if (this.preferences.getBoolean("feedback_vibration", true))
		{
			this.vibrator.vibrate(l);
		}
	}
	
	public boolean requestEnableBluetooth()
	{
		boolean b = this.requestEnableBluetooth;
		
		this.requestEnableBluetooth = false;
		
		return b;
	}
	
	public synchronized void run()
	{
		Connection co = this.connections.getUsed();
		
		if (co != null)
		{
			KeyboardExtenderConnection c = null;
			
			try
			{
				c = co.connect(this);
				
				synchronized (this.connection)
				{
					this.connection[0] = c;
				}
				
				try
				{
					this.showInternalToast(R.string.text_connection_established);
					
					String password = co.getPassword();
					this.sendAction(new AuthentificationAction(password));
					
					while (true)
					{
						KeyboardExtenderAction action = c.receiveAction();
						
						this.receiveAction(action);
					}
				}
				finally
				{
					synchronized (this.connection)
					{
						this.connection[0] = null;
					}
					
					c.close();
				}
			}
			catch (IOException e)
			{
				this.debug(e);
				
				if (c == null)
				{
					this.showInternalToast(R.string.text_connection_refused);
				}
				else
				{
					this.showInternalToast(R.string.text_connection_closed);
				}
			}
			catch (IllegalArgumentException e)
			{
				this.debug(e);
				
				this.showInternalToast(R.string.text_illegal_connection_parameter);
			}
		}
		else
		{
			this.showInternalToast(R.string.text_no_connection_selected);
		}
	}
	
	public void sendAction(KeyboardExtenderAction action)
	{
		synchronized (this.connection)
		{
			if (this.connection[0] != null)
			{
				try
				{
					this.connection[0].sendAction(action);
				}
				catch (IOException e)
				{
					this.debug(e);
				}
			}
		}
	}
	
	public void showInternalToast(int resId)
	{
		if (this.isInternalToast())
		{
			this.showToast(resId);
		}
	}
	
	public void showInternalToast(String message)
	{
		if (this.isInternalToast())
		{
			this.showToast(message);
		}
	}
	
	public boolean isInternalToast()
	{
		synchronized (this.actionReceivers)
		{
			return !this.actionReceivers.isEmpty();
		}
	}
	
	public void showToast(int resId)
	{
		this.showToast(this.getResources().getString(resId));
	}
	
	public void showToast(final String message)
	{
		this.handler.post(new Runnable()
		{
			public void run()
			{
				Toast.makeText(KeyboardExtender.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void receiveAction(KeyboardExtenderAction action)
	{
		synchronized (this.actionReceivers)
		{
			for (KeyboardExtenderActionReceiver actionReceiver : this.actionReceivers)
			{
				actionReceiver.receiveAction(action);
			}
		}
		
		if (action instanceof AuthentificationResponseAction)
		{
			this.receiveAuthentificationResponseAction((AuthentificationResponseAction) action);
		}
	}
	
	private void receiveAuthentificationResponseAction(AuthentificationResponseAction action)
	{
		if (action.authentificated)
		{
			this.showInternalToast(R.string.text_authentificated);
		}
		else
		{
			this.showInternalToast(R.string.text_not_authentificated);
		}
	}
	
	public void registerActionReceiver(KeyboardExtenderActionReceiver actionReceiver)
	{
		synchronized (this.actionReceivers)
		{
			this.actionReceivers.add(actionReceiver);
			
			if (this.actionReceivers.size() > 0)
			{
				synchronized (this.connection)
				{
					if (this.connection[0] == null)
					{
						(new Thread(this)).start();
					}
				}
			}
		}
	}
	
	public void unregisterActionReceiver(KeyboardExtenderActionReceiver actionReceiver)
	{
		synchronized (this.actionReceivers)
		{
			this.actionReceivers.remove(actionReceiver);
			
			if (this.actionReceivers.size() == 0)
			{
				this.closeConnectionScheduler.schedule();
			}
		}
	}
	
	public void debug(Exception e)
	{
		if (this.preferences.getBoolean("debug_enabled", false))
		{
			Log.d(this.getResources().getString(R.string.app_name), null, e);
		}
	}
	
	private class CloseConnectionScheduler implements Runnable
	{
		private Thread currentThread;
		
		public synchronized void run()
		{
			try
			{
				this.wait(KeyboardExtender.CONNECTION_CLOSE_DELAY);
				
				synchronized (KeyboardExtender.this.actionReceivers)
				{
					if (KeyboardExtender.this.actionReceivers.size() == 0)
					{
						synchronized (KeyboardExtender.this.connection)
						{
							if (KeyboardExtender.this.connection[0] != null)
							{
								KeyboardExtender.this.connection[0].close();
								
								KeyboardExtender.this.connection[0] = null;
							}
						}
					}
				}
				
				this.currentThread = null;
			}
			catch (InterruptedException e)
			{
				KeyboardExtender.this.debug(e);
			}
			catch (IOException e)
			{
				KeyboardExtender.this.debug(e);
			}
		}
		
		public synchronized void schedule()
		{
			if (this.currentThread != null)
			{
				this.currentThread.interrupt();
			}
			
			this.currentThread = new Thread(this);
			
			this.currentThread.start();
		}
	}
}
