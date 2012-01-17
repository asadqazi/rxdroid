package at.caspase.rxdroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Simple wrapper for a NumberPicker.
 * <p>
 * If using this class on a pre-Honeycomb device, a custom implementation
 * by <a href="http://www.quietlycoding.com">Mike Novak</a> is used. This
 * class implements only a very limited subset of the functions
 * provided by the <code>android.widget.NumberPicker</code> widget introduced in Honeycomb.
 * In theory, this class should be replaceable with said widget,
 * requiring no changes to the source (other than the type specifications of course).
 *
 * @see android.widget.NumberPicker
 * @author Joseph Lehner
 *
 */
public class NumberPickerWrapper extends LinearLayout
{
	private static final int MIN_DEFAULT = 0;
	private static final int MAX_DEFAULT = 99999;

	private final com.quietlycoding.android.picker.NumberPicker mNumberPickerOld;
	private final android.widget.NumberPicker mNumberPickerNew;

	// As the old NumberPicker only provides a setRange method,
	// we have to store the value that we're currently NOT setting, so
	// that it can be passed to setRange when using setMinValue or
	// setMaxValue
	private int mMinValue;
	private int mMaxValue;

	private boolean mWrapSelectorWheel;

	private OnValueChangeListener mListener;

	public interface OnValueChangeListener
	{
		void onValueChange(NumberPickerWrapper picker, int oldVal, int newVal);
	}

	public NumberPickerWrapper(Context context) {
		this(context, null, 0);
	}

	public NumberPickerWrapper(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NumberPickerWrapper(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs);

		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.number_picker_wrapper, this, true);

		if(Version.SDK_IS_PRE_HONEYCOMB)
		{
			mNumberPickerNew = null;
			mNumberPickerOld =
					(com.quietlycoding.android.picker.NumberPicker) findViewById(R.id.picker);
		}
		else
		{
			mNumberPickerOld = null;
			mNumberPickerNew =
					(android.widget.NumberPicker) findViewById(R.id.picker);

			mNumberPickerNew.setSelected(false);
		}

		setMinValue(MIN_DEFAULT);
		setMaxValue(MAX_DEFAULT);
	}

	public int getValue()
	{
		if(Version.SDK_IS_PRE_HONEYCOMB)
			return mNumberPickerOld.getCurrent();
		else
			return mNumberPickerNew.getValue();
	}

	public void setValue(int value)
	{
		if(Version.SDK_IS_PRE_HONEYCOMB)
			mNumberPickerOld.setCurrentAndNotify(value);
		else
			mNumberPickerNew.setValue(value);
	}

	public int getMinValue() {
		return mMinValue;
	}

	public void setMinValue(int minValue)
	{
		if(Version.SDK_IS_PRE_HONEYCOMB)
		{
			mNumberPickerOld.setRange(mMinValue = minValue, mMaxValue);
			maybeEnableWrap();
		}
		else
			mNumberPickerNew.setMinValue(mMinValue = minValue);
	}

	public int getMaxValue() {
		return mMaxValue;
	}

	public void setMaxValue(int maxValue)
	{
		if(Version.SDK_IS_PRE_HONEYCOMB)
		{
			mNumberPickerOld.setRange(mMinValue, mMaxValue = maxValue);
			maybeEnableWrap();
		}
		else
			mNumberPickerNew.setMaxValue(mMaxValue = maxValue);
	}

	public boolean getWrapSelectorWheel() {
		return mWrapSelectorWheel;
	}

	public void setWrapSelectorWheel(boolean wrapSelectorWheel)
	{
		if(Version.SDK_IS_PRE_HONEYCOMB)
			mNumberPickerOld.setWrap(mWrapSelectorWheel = wrapSelectorWheel);
		else
			mNumberPickerNew.setWrapSelectorWheel(mWrapSelectorWheel = wrapSelectorWheel);
	}

	public OnValueChangeListener getOnValueChangeListener() {
		return mListener;
	}

	public void setOnValueChangeListener(OnValueChangeListener l)
	{
		mListener = l;

		if(Version.SDK_IS_PRE_HONEYCOMB)
		{
			if(l == null)
			{
				mNumberPickerOld.setOnChangeListener(null);
				return;
			}

			mNumberPickerOld.setOnChangeListener(
					new com.quietlycoding.android.picker.NumberPicker.OnChangedListener() {

				@Override
				public void onChanged(com.quietlycoding.android.picker.NumberPicker picker,
						int oldVal, int newVal)
				{
					mListener.onValueChange(NumberPickerWrapper.this, oldVal, newVal);
				}
			});
		}
		else
		{
			if(l == null)
			{
				mNumberPickerNew.setOnValueChangedListener(null);
				return;
			}

			mNumberPickerNew.setOnValueChangedListener(
					new android.widget.NumberPicker.OnValueChangeListener() {

				@Override
				public void onValueChange(android.widget.NumberPicker picker,
						int oldVal, int newVal)
				{
					mListener.onValueChange(NumberPickerWrapper.this, oldVal, newVal);
				}
			});
		}
	}

	public void setOnLongPressUpdateInterval(long intervalMillis)
	{
		if(Version.SDK_IS_PRE_HONEYCOMB)
			mNumberPickerOld.setSpeed(intervalMillis);
		else
			mNumberPickerNew.setOnLongPressUpdateInterval(intervalMillis);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		if(Version.SDK_IS_PRE_HONEYCOMB)
			mNumberPickerOld.setEnabled(enabled);
		else
			mNumberPickerNew.setEnabled(enabled);
	}

	private void maybeEnableWrap()
	{
		if(Version.SDK_IS_PRE_HONEYCOMB)
		{
			if(Math.abs(mMaxValue - mMinValue) > 5)
				setWrapSelectorWheel(true);
		}
	}
}