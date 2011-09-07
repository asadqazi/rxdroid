/**
 * Copyright (C) 2011 Joseph Lehner <joseph.c.lehner@gmail.com>
 *
 * This file is part of RxDroid.
 *
 * RxDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RxDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RxDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package at.caspase.rxdroid;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TimePicker;

public class TimePreference extends Preference implements OnTimeSetListener, OnPreferenceClickListener
{
	@SuppressWarnings("unused")
	private static final String TAG = TimePreference.class.getName();
	
	private String mDefaultValue;
	private DumbTime mTime;
	
	private DumbTime[] mConstraintTimes = new DumbTime[2];
	private String[] mConstraintKeys = new String[2];
	
	public TimePreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TimePreference(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
				
		mDefaultValue = getStringAttribute(context, attrs, NS_ANDROID, "defaultValue", "00:00");		
				
		final String[] attributeNames = { "after", "before" };
		
		for(int i = 0; i != attributeNames.length; ++i)
		{
			String value = attrs.getAttributeValue(NS_PREF, attributeNames[i]);
			try
			{
				mConstraintTimes[i] = DumbTime.valueOf(value);
			}
			catch(IllegalArgumentException e)
			{
				mConstraintKeys[i] = value;
			}			
		}
		
		setOnPreferenceClickListener(this);		
		updateTime();
	}
	
	@Override
	public CharSequence getSummary() {
		return mTime == null ? null : mTime.toString();
	}

	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		showDialog();
		return true;
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute)
	{
		mTime = new DumbTime(hourOfDay, minute);
		
		if(shouldPersist())
			persistString(mTime.toString());
		
		notifyChanged();		
	}
	
	private void showDialog()
	{
		updateTime();
		
		final boolean is24HourView = DateFormat.is24HourFormat(getContext());
		TimePickerDialog dialog = new TimePickerDialog(getContext(), this, mTime.getHours(), mTime.getMinutes(), is24HourView);
		
		dialog.setMessage(generateDialogMessage());		
		dialog.show();
	}
	
	private String generateDialogMessage()
	{
		int msgId = -1;

		DumbTime after = getConstraint(IDX_AFTER);
		DumbTime before = getConstraint(IDX_BEFORE);
		
		if(after != null && before != null)
			msgId = R.string._msg_constraints_ab;
		else if(after != null)
			msgId = R.string._msg_constraints_a;
		else if(before != null)
			msgId = R.string._msg_constraints_b;

		if(msgId == -1)
			return null;
		
		return getContext().getString(msgId, after, before);		
	}
	
	private DumbTime getConstraint(int index)
	{
		if(mConstraintTimes[index] != null)
			return mConstraintTimes[index];
		
		final String key = mConstraintKeys[index];
		if(key != null)
		{
			final TimePreference constraintPref = (TimePreference) findPreferenceInHierarchy(key);
			if(constraintPref == null)
				throw new IllegalStateException("No such TimePreference: " + key);
			
			return constraintPref.mTime;
		}
		
		return null;
	}
	
	private void updateTime() {
		mTime = DumbTime.valueOf(getPersistedString(mDefaultValue));
	}
	
	private static String getStringAttribute(Context context, AttributeSet attrs, String namespace, String attribute, String defaultValue)
	{
		int resId = attrs.getAttributeResourceValue(namespace, attribute, -1);
		String value;
		
		if(resId == -1)
			value = attrs.getAttributeValue(namespace, attribute);
		else
			value = context.getString(resId);
		
		return value == null ? defaultValue : value;		
	}
	
	private static final String NS_BASE = "http://schemas.android.com/apk/res/";
	private static final String NS_ANDROID = NS_BASE + "android";
	private static final String NS_PREF = NS_BASE + TimePreference.class.getPackage().getName();
	
	private static final int IDX_AFTER = 0;
	private static final int IDX_BEFORE = 1;
}
