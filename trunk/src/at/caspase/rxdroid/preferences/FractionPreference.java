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

package at.caspase.rxdroid.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import at.caspase.androidutils.MyDialogPreference;
import at.caspase.androidutils.StateSaver;
import at.caspase.androidutils.StateSaver.SaveState;
import at.caspase.rxdroid.Fraction;
import at.caspase.rxdroid.FractionInputDialog;
import at.caspase.rxdroid.FractionInputDialog.OnFractionSetListener;

/**
 * A preference for storing fractions.
 *
 * @author Joseph Lehner
 *
 */
public class FractionPreference extends MyDialogPreference implements OnFractionSetListener
{
	private static final String TAG = FractionPreference.class.getName();

	private static final String KEY_VALUE = "value";

	Fraction mValue;
	@SaveState
	Fraction mLongClickSummand;

	//FractionInputDialog2 mDialog;

	public FractionPreference(Context context) {
		this(context, null);
	}

	public FractionPreference(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.dialogPreferenceStyle);
	}

	public FractionPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mValue = Fraction.decode(getPersistedString("0"));
	}

	public void setLongClickSummand(Fraction value) {
		mLongClickSummand = value;
	}

	@Override
	public CharSequence getSummary()
	{
		CharSequence summary = super.getSummary();
		if(summary == null)
			return mValue.toString();

		return summary;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		super.onClick();

		if(which == DialogInterface.BUTTON_NEUTRAL)
		{
			FractionInputDialog myDialog = (FractionInputDialog) dialog;
			onFractionSet(myDialog, myDialog.getValue().add(mLongClickSummand));
		}
	}

	@Override
	public void onFractionSet(FractionInputDialog dialog, Fraction value)
	{
		mValue = value;

		if(callChangeListener(mValue))
			persistString(mValue.toString());

		notifyChanged();
	}

	@Override
	public void setValue(Object value)
	{
		mValue = (Fraction) value;

		if(super.getSummary() == null)
			setSummary(mValue.toString());
	}

	@Override
	public Fraction getValue() {
		return mValue;
	}

	@Override
	protected Dialog onGetCustomDialog()
	{
		FractionInputDialog dialog = new FractionInputDialog(getContext(), mValue, this);
		dialog.setTitle(getDialogTitle());
		dialog.setIcon(getDialogIcon());
		dialog.setAutoInputModeEnabled(true);
		return dialog;
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		// do nothing
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		String value = a.getString(index);
		Log.d(TAG, "onGetDefaultValue: value=" + value);
		return value != null ? value : "0";
	}

	@Override
	protected Parcelable onSaveInstanceState()
	{
		Bundle extras = new Bundle();
		FractionInputDialog dialog = (FractionInputDialog) getDialog();
		final Fraction value;

		if(dialog != null)
			value = dialog.getValue();
		else
			value = mValue;

		extras.putSerializable(KEY_VALUE, value);
		Parcelable superState = super.onSaveInstanceState();
		return StateSaver.createInstanceState(this, superState, extras);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		StateSaver.restoreInstanceState(this, state);

		Bundle extras = StateSaver.getExtras(state);
		if(extras != null)
		{
			mValue = (Fraction) extras.getSerializable(KEY_VALUE);
			if(mValue == null)
				mValue = new Fraction();
		}

		super.onRestoreInstanceState(StateSaver.getSuperState(state));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// this stuff is handled in onFractionSet
	}
}