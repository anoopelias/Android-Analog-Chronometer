package com.timtak.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;

/**
 * This is the analog version of the Chronometer available in Android library.
 * <p>
 * You can give it a start time in the {@link SystemClock#elapsedRealtime}
 * timebase, and it counts up from that, or if you don't give it a base time, it
 * will use the time at which you call {@link #start}.
 * 
 * @attr ref android.R.styleable#Chronometer_format
 */
public class AnalogChronometer extends ClockView {

	/**
	 * A callback that notifies when the chronometer has incremented on its own.
	 */
	public interface OnChronometerTickListener {

		/**
		 * Notification that the chronometer has changed.
		 */
		void onChronometerTick(AnalogChronometer chronometer);

	}

	private long mBase;
	private boolean mVisible;
	private boolean mStarted;
	private boolean mRunning;
	private OnChronometerTickListener mOnChronometerTickListener;

	private static final int TICK_WHAT = 2;

	/**
	 * Initialize this Chronometer object. Sets the base to the current time.
	 */
	public AnalogChronometer(Context context) {
		this(context, null, 0);
	}

	/**
	 * Initialize with standard view layout information. Sets the base to the
	 * current time.
	 */
	public AnalogChronometer(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Initialize with standard view layout information and style. Sets the base
	 * to the current time.
	 */
	public AnalogChronometer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mBase = SystemClock.elapsedRealtime();
		updateTime(mBase);
	}

	/**
	 * Set the time that the count-up timer is in reference to.
	 * 
	 * @param base
	 *            Use the {@link SystemClock#elapsedRealtime} time base.
	 */
	public void setBase(long base) {
		mBase = base;
		dispatchChronometerTick();
		updateTime(SystemClock.elapsedRealtime());
	}

	/**
	 * Return the base time as set through {@link #setBase}.
	 */
	public long getBase() {
		return mBase;
	}

	/**
	 * Sets the listener to be called when the chronometer changes.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void setOnChronometerTickListener(OnChronometerTickListener listener) {
		mOnChronometerTickListener = listener;
	}

	/**
	 * @return The listener (may be null) that is listening for chronometer
	 *         change events.
	 */
	public OnChronometerTickListener getOnChronometerTickListener() {
		return mOnChronometerTickListener;
	}

	/**
	 * Start counting up. This does not affect the base as set from
	 * {@link #setBase}, just the view display.
	 * 
	 * Chronometer works by regularly scheduling messages to the handler, even
	 * when the Widget is not visible. To make sure resource leaks do not occur,
	 * the user should make sure that each start() call has a reciprocal call to
	 * {@link #stop}.
	 */
	public void start() {
		mStarted = true;
		updateRunning();
	}

	/**
	 * Stop counting up. This does not affect the base as set from
	 * {@link #setBase}, just the view display.
	 * 
	 * This stops the messages to the handler, effectively releasing resources
	 * that would be held as the chronometer is running, via {@link #start}.
	 */
	public void stop() {
		mStarted = false;
		updateRunning();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mVisible = false;
		updateRunning();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		mVisible = visibility == VISIBLE;
		updateRunning();
	}

	private synchronized void updateTime(long now) {
		long time = now - mBase;
		time /= 1000;
		int sec = (int) (time % 60);
		time /= 60;
		int min = (int) (time % 60);

		setTime(min, sec);

		invalidate();
	}

	private void updateRunning() {
		boolean running = mVisible && mStarted;
		if (running != mRunning) {
			if (running) {
				updateTime(SystemClock.elapsedRealtime());
				dispatchChronometerTick();
				mHandler.sendMessageDelayed(
						Message.obtain(mHandler, TICK_WHAT), 1000);
			} else {
				mHandler.removeMessages(TICK_WHAT);
			}
			mRunning = running;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message m) {
			if (mRunning) {
				updateTime(SystemClock.elapsedRealtime());
				dispatchChronometerTick();
				sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
			}
		}
	};

	void dispatchChronometerTick() {
		if (mOnChronometerTickListener != null) {
			mOnChronometerTickListener.onChronometerTick(this);
		}
	}
}
