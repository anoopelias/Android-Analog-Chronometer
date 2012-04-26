package com.timtak.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * This widget display an analog clock with two hands either as hours and
 * minutes or as minutes and seconds.
 * 
 * This class will NOT read the time of the device, rather, it will only show
 * the time it is set to. Use {@link #setTime} to set the time on the clock
 * 
 * @attr ref R.styleable#ClockView
 */
public class ClockView extends View {

	private static final String TAG = "ClockView";
	private Drawable mBigHand;
	private Drawable mSmallHand;
	private Drawable mDial;

	private int mDialWidth;
	private int mDialHeight;

	private boolean mAttached;

	private float mBigHandTurn;
	private float mSmallHandTurn;
	private boolean mChanged;

	public ClockView(Context context) {
		this(context, null);
	}

	public ClockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Resources r = getContext().getResources();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ClockView, defStyle, 0);

		mDial = a.getDrawable(R.styleable.ClockView_dial);
		if (mDial == null) {
			mDial = r.getDrawable(R.drawable.clock_dial);
		}

		mBigHand = a.getDrawable(R.styleable.ClockView_big_hand);
		if (mBigHand == null) {
			mBigHand = r.getDrawable(R.drawable.big_needle);
		}

		mSmallHand = a.getDrawable(R.styleable.ClockView_small_hand);
		if (mSmallHand == null) {
			mSmallHand = r.getDrawable(R.drawable.small_needle);
		}

		int hour = a.getInt(R.styleable.ClockView_hour, 0);
		int min = a.getInt(R.styleable.ClockView_min, 0);
		int sec = a.getInt(R.styleable.ClockView_sec, 0);

		Log.d(TAG, "init " + hour + ":" + min + ":" + sec);

		setTime(hour, min, sec);

		mDialWidth = mDial.getIntrinsicWidth();
		mDialHeight = mDial.getIntrinsicHeight();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			mAttached = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float hScale = 1.0f;
		float vScale = 1.0f;

		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
			hScale = (float) widthSize / (float) mDialWidth;
		}

		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
			vScale = (float) heightSize / (float) mDialHeight;
		}

		float scale = Math.min(hScale, vScale);

		setMeasuredDimension(
				resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
				resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		int availableWidth = getRight() - getLeft();
		int availableHeight = getBottom() - getTop();

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = mDial;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(canvas);

		canvas.save();
		canvas.rotate(mBigHandTurn, x, y);
		final Drawable bigHand = mBigHand;
		if (changed) {
			w = bigHand.getIntrinsicWidth();
			h = bigHand.getIntrinsicHeight();
			bigHand.setBounds(x - (w / 2), y - h, x + (w / 2), y);
		}
		bigHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mSmallHandTurn, x, y);

		final Drawable smallHand = mSmallHand;
		if (changed) {
			w = smallHand.getIntrinsicWidth();
			h = smallHand.getIntrinsicHeight();
			smallHand.setBounds(x - (w / 2), y - h, x + (w / 2), y);
		}
		smallHand.draw(canvas);
		canvas.restore();

		if (scaled) {
			canvas.restore();
		}
	}

	/**
	 * To set hour hand and min hand.
	 * 
	 * @param hour
	 * @param min
	 * @param sec
	 */
	public void setTime(int hour, int min, int sec) {
		float mins = (min + sec / 60.0f);
		float hours = hour + mins / 60.0f;
		mSmallHandTurn = mins / 60.0f * 360.0f;
		mBigHandTurn = hours / 12.0f * 360.0f;
		mChanged = true;
	}

	/**
	 * To set Min hand and sec hand.
	 * 
	 * @param min
	 * @param sec
	 */
	public void setTime(int min, int sec) {
		float secs = (float) sec;
		float mins = min + secs / 60.0f;
		mSmallHandTurn = secs / 60.0f * 360.0f;
		mBigHandTurn = mins / 60.0f * 360.0f;
		mChanged = true;
	}

}
