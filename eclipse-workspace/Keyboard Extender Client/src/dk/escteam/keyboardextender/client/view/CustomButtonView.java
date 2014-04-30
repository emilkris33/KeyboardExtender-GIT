package dk.escteam.keyboardextender.client.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import dk.escteam.keyboardextender.client.activity.MainActivity;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.protocol.action.KeyboardAction;

public class CustomButtonView extends Button
{
	private MainActivity activity;
	private KeyboardExtender application;
	
	private boolean hold;
	private long holdDelay;
	
	public int row;
	public int col;
	public int rows;
	public int cols;
	public String output = "";
	
	public CustomButtonView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.activity = (MainActivity) context;
		this.application = (KeyboardExtender) this.activity.getApplication();
		
		this.hold = false;
		
		this.holdDelay = Long.parseLong(this.application.getPreferences().getString("control_hold_delay", "300"));
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		fitButton();
	}
	
	public void fitButton()
	{
		android.support.v7.widget.GridLayout gl = (GridLayout) this.getParent();
		
		int idealChildWidth = (int) (gl.getWidth() / cols);
		int idealChildHeight = (int) (gl.getHeight() / rows);
		int idealTextSize = (int) (idealChildHeight * 0.3);
		
		this.setWidth(idealChildWidth);
		this.setHeight(idealChildHeight);
		this.setTextSize(0, idealTextSize);
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_MOVE:
			{
				this.onTouchMove(event);
				break;
			}
			
			case MotionEvent.ACTION_DOWN:
			{
				this.onTouchDown(event);
				break;
			}
			
			case MotionEvent.ACTION_UP:
			{
				this.onTouchUp(event);
				break;
			}
			
			default:
				break;
		}
		
		return true;
	}
	
	private void onTouchDown(MotionEvent event)
	{
		setPressed(true);
		this.application.vibrate(30);
	}
	
	private void onTouchMove(MotionEvent event)
	{
		if (!this.hold && event.getEventTime() - event.getDownTime() >= this.holdDelay)
		{
			this.hold = true;
			this.application.vibrate(100);
			// Implement Longpress Features
		}
	}
	
	private void onTouchUp(MotionEvent event)
	{
		if (!hold)
		{
			char[] outputChars = output.toCharArray();
			for (int i = 0; i < outputChars.length; i++)
			{
				this.application.sendAction(new KeyboardAction((int) outputChars[i]));
			}
		}
		this.hold = false;
		this.setPressed(false);
	}
	
	public final static class Debug
	{
		private Debug()
		{
		}
		
		public static void out(Object msg)
		{
			Log.i("info", msg.toString());
		}
	}
}