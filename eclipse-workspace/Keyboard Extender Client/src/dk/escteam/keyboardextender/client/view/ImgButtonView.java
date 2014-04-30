package dk.escteam.keyboardextender.client.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import dk.escteam.keyboardextender.client.R;
import dk.escteam.keyboardextender.client.activity.MainActivity;
import dk.escteam.keyboardextender.client.app.KeyboardExtender;
import dk.escteam.keyboardextender.protocol.action.KeyboardAction;

public class ImgButtonView extends ImageButton
{
	private MainActivity mainActivity;
	private KeyboardExtender application;
	
	private boolean hold;
	private long holdDelay;
	
	public ImgButtonView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.mainActivity = (MainActivity) context;
		this.application = (KeyboardExtender) this.mainActivity.getApplication();
		
		this.hold = false;
		
		this.holdDelay = Long.parseLong(this.application.getPreferences().getString("control_hold_delay", "300"));
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
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		android.support.v7.widget.GridLayout gl = (GridLayout) this.getParent();
		
		int idealChildWidth = (int) (gl.getWidth() / gl.getColumnCount());
		int idealChildHeight = (int) (gl.getHeight() / gl.getRowCount());
		
		GridLayout.LayoutParams layout = (GridLayout.LayoutParams) this.getLayoutParams();
		if (layout.columnSpec.equals(GridLayout.spec(3, 2, GridLayout.START)))
		{
			this.setMaxWidth(idealChildWidth * 2);
			this.setMinimumWidth(idealChildWidth * 2);
		}
		else
		{
			this.setMaxWidth(idealChildWidth);
			this.setMinimumWidth(idealChildWidth);
		}
		if (layout.rowSpec.equals(GridLayout.spec(3, 2, GridLayout.START)))
		{
			this.setMaxHeight(idealChildHeight * 2);
			this.setMinimumHeight(idealChildHeight * 2);
		}
		else
		{
			this.setMaxHeight(idealChildHeight);
			this.setMinimumHeight(idealChildHeight);
		}
		
	}
	
	private void onTouchDown(MotionEvent event)
	{
		this.setPressed(true);
		
		this.application.vibrate(30);
	}
	
	private void onTouchMove(MotionEvent event)
	{
		if (!this.hold && event.getEventTime() - event.getDownTime() >= this.holdDelay)
		{
			this.hold = true;
			this.application.vibrate(100);
			// Implement Longpress Features
			switch (this.getId())
			{
				case R.id.button_sin:
					this.application.sendAction(new KeyboardAction(115));
					this.application.sendAction(new KeyboardAction(105));
					this.application.sendAction(new KeyboardAction(110));
					this.application.sendAction(new KeyboardAction(94));
					this.application.sendAction(new KeyboardAction(45));
					this.application.sendAction(new KeyboardAction(49));
					break;
				case R.id.button_cos:
					this.application.sendAction(new KeyboardAction(99));
					this.application.sendAction(new KeyboardAction(111));
					this.application.sendAction(new KeyboardAction(115));
					this.application.sendAction(new KeyboardAction(94));
					this.application.sendAction(new KeyboardAction(45));
					this.application.sendAction(new KeyboardAction(49));
					break;
				case R.id.button_tan:
					this.application.sendAction(new KeyboardAction(116));
					this.application.sendAction(new KeyboardAction(97));
					this.application.sendAction(new KeyboardAction(110));
					this.application.sendAction(new KeyboardAction(94));
					this.application.sendAction(new KeyboardAction(45));
					this.application.sendAction(new KeyboardAction(49));
					break;
				default:
					break;
			}
		}
	}
	
	private void onTouchUp(MotionEvent event)
	{
		if (!hold)
		{
			switch (this.getId())
			{
				case R.id.button_log:
					this.application.sendAction(new KeyboardAction(108));
					this.application.sendAction(new KeyboardAction(111));
					this.application.sendAction(new KeyboardAction(103));
					break;
				case R.id.button_sin:
					this.application.sendAction(new KeyboardAction(115));
					this.application.sendAction(new KeyboardAction(105));
					this.application.sendAction(new KeyboardAction(110));
					break;
				case R.id.button_cos:
					this.application.sendAction(new KeyboardAction(99));
					this.application.sendAction(new KeyboardAction(111));
					this.application.sendAction(new KeyboardAction(115));
					break;
				case R.id.button_tan:
					this.application.sendAction(new KeyboardAction(116));
					this.application.sendAction(new KeyboardAction(97));
					this.application.sendAction(new KeyboardAction(110));
					break;
				case R.id.button_pi:
					this.application.sendAction(new KeyboardAction(80));
					this.application.sendAction(new KeyboardAction(105));
					break;
				case R.id.button_1:
					this.application.sendAction(new KeyboardAction(49));
					break;
				case R.id.button_2:
					this.application.sendAction(new KeyboardAction(50));
					break;
				case R.id.button_3:
					this.application.sendAction(new KeyboardAction(51));
					break;
				case R.id.button_4:
					this.application.sendAction(new KeyboardAction(52));
					break;
				case R.id.button_5:
					this.application.sendAction(new KeyboardAction(53));
					break;
				case R.id.button_6:
					this.application.sendAction(new KeyboardAction(54));
					break;
				case R.id.button_7:
					this.application.sendAction(new KeyboardAction(55));
					break;
				case R.id.button_8:
					this.application.sendAction(new KeyboardAction(56));
					break;
				case R.id.button_9:
					this.application.sendAction(new KeyboardAction(57));
					break;
				case R.id.button_0:
					this.application.sendAction(new KeyboardAction(48));
					break;
				case R.id.button_dot:
					this.application.sendAction(new KeyboardAction(46));
					break;
				case R.id.button_comma:
					this.application.sendAction(new KeyboardAction(44));
					break;
				case R.id.button_backspace:
					this.application.sendAction(new KeyboardAction(-1));
					break;
				case R.id.button_forwarddash:
					this.application.sendAction(new KeyboardAction(47));
					break;
				case R.id.button_asterisk:
					this.application.sendAction(new KeyboardAction(42));
					break;
				case R.id.button_minus:
					this.application.sendAction(new KeyboardAction(45));
					break;
				case R.id.button_plus:
					this.application.sendAction(new KeyboardAction(43));
					break;
				case R.id.button_enter:
					this.application.sendAction(new KeyboardAction(10));
					break;
				case R.id.button_xpoty:
					this.application.sendAction(new KeyboardAction(94));
					break;
				case R.id.button_sqrt:
					this.application.sendAction(new KeyboardAction(115));
					this.application.sendAction(new KeyboardAction(113));
					this.application.sendAction(new KeyboardAction(114));
					this.application.sendAction(new KeyboardAction(116));
					this.application.sendAction(new KeyboardAction(40));
					break;
				case R.id.button_xpot2:
					this.application.sendAction(new KeyboardAction(94));
					this.application.sendAction(new KeyboardAction(50));
					break;
				case R.id.button_procent:
					this.application.sendAction(new KeyboardAction(37));
					break;
				case R.id.button_verticalbar:
					this.application.sendAction(new KeyboardAction(124));
					break;
				case R.id.button_parent1:
					this.application.sendAction(new KeyboardAction(40));
					break;
				case R.id.button_parent2:
					this.application.sendAction(new KeyboardAction(41));
					break;
				default:
					this.application.sendAction(new KeyboardAction(49));
					break;
			}
		}
		this.hold = false;
		this.setPressed(false);
	}
}