package dk.escteam.keyboardextender.client.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.activity.MainActivity;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.protocol.action.MouseClickAction;

public class ClickView extends Button
{
	private MainActivity mainActivity;
	private KeyboardExtender application;
	
	private byte button;
	private boolean hold;
	private long holdDelay;
	
	public ClickView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.mainActivity = (MainActivity) context;
		this.application = (KeyboardExtender) this.mainActivity.getApplication();
		
		switch (this.getId())
		{
			case R.id.leftClickView:
				this.button = MouseClickAction.BUTTON_LEFT;
				break;
			case R.id.middleClickView:
				this.button = MouseClickAction.BUTTON_MIDDLE;
				break;
			case R.id.rightClickView:
				this.button = MouseClickAction.BUTTON_RIGHT;
				break;
			default:
				this.button = MouseClickAction.BUTTON_NONE;
				break;
		}
		
		this.hold = false;
		
		this.holdDelay = Long.parseLong(this.application.getPreferences().getString("control_hold_delay", null));
	}
	
	public boolean isHold()
	{
		return hold;
	}
	
	public void setHold(boolean hold)
	{
		this.hold = hold;
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
		if (!this.hold)
		{
			this.mainActivity.mouseClick(this.button, MouseClickAction.STATE_DOWN);
			
			this.setPressed(true);
			
			this.application.vibrate(50);
		}
		else
		{
			this.hold = false;
		}
	}
	
	private void onTouchMove(MotionEvent event)
	{
		if (!this.hold && event.getEventTime() - event.getDownTime() >= this.holdDelay)
		{
			this.hold = true;
			
			this.application.vibrate(100);
		}
	}
	
	private void onTouchUp(MotionEvent event)
	{
		if (!this.hold)
		{
			this.mainActivity.mouseClick(this.button, MouseClickAction.STATE_UP);
			
			this.setPressed(false);
		}
	}
}