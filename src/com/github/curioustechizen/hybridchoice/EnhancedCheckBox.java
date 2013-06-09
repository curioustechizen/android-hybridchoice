package com.github.curioustechizen.hybridchoice;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * An enhanced {@code CheckBox} that differentiates between user clicks and
 * programmatic clicks. In particular, the {@code OnCheckedChangeListener} is
 * <strong>not</strong> triggered when the state of the checkbox is changed
 * programmatically.
 * 
 * @author Kiran Rao
 * 
 */
public class EnhancedCheckBox extends CheckBox {

	private CompoundButton.OnCheckedChangeListener mListener = null;

	public EnhancedCheckBox(Context context) {
		super(context);
	}

	public EnhancedCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EnhancedCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setOnCheckedChangeListener(
			CompoundButton.OnCheckedChangeListener listener) {
		if (this.mListener == null)
			this.mListener = listener;
		super.setOnCheckedChangeListener(listener);
	}

	/**
	 * Set the checked state of the checkbox programmatically. This is to differentiate it from a user click
	 * @param checked Whether to check the checkbox
	 */
	public void setCheckedProgrammatically(boolean checked) {
		super.setOnCheckedChangeListener(null);
		super.setChecked(checked);
		super.setOnCheckedChangeListener(mListener);
	}

}
