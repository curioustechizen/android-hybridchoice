package com.github.curioustechizen.hybridchoice;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class EnhancedCheckBox extends CheckBox {
    
    private CompoundButton.OnCheckedChangeListener mListener = null;

    public EnhancedCheckBox(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public EnhancedCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public EnhancedCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void setOnCheckedChangeListener(
        CompoundButton.OnCheckedChangeListener listener){
        if(this.mListener == null)
            this.mListener = listener;
        super.setOnCheckedChangeListener(listener);
    }

    public void setCheckedProgrammatically(boolean checked){
        super.setOnCheckedChangeListener(null);
        super.setChecked(checked);
        super.setOnCheckedChangeListener(mListener);
    }

}
