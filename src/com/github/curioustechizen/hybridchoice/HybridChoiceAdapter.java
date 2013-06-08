package com.github.curioustechizen.hybridchoice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public abstract class HybridChoiceAdapter<T> extends ArrayAdapter<T> {

	private Set<Integer> chosenItems = new HashSet<Integer>();
	private int openedItem = -1;
	private RowItemChoiceCallback mCallback;
	private static final int COLOR_OPENED_ITEM = Color.parseColor("#ff0099cc"); //holo_blue_dark
	private static final int COLOR_CHOSEN_ITEM = Color
			.parseColor("#ff00ddff"); //holo_blue_bright
	private Drawable openedBackgroundDrawable, chosenBackgroundDrawable;
	private int openedBackgroundResid, chosenBackgroundResid;
	private int openedBackgroundColor = -1, chosenBackgroundColor = -1;

	public interface RowItemChoiceCallback {
		void onRowItemChosenChanged(int position, boolean chosen);
	}

	public HybridChoiceAdapter(Context context, List<T> objects,
			RowItemChoiceCallback callback) {
		super(context, android.R.id.text1, objects);
		this.mCallback = callback;
	}

	public void setOpenedBackgroundDrawable(Drawable openedDrawable) {
		this.openedBackgroundDrawable = openedDrawable;
	}

	public void setChosenBackgroundDrawable(Drawable selectedBackground) {
		this.chosenBackgroundDrawable = selectedBackground;
	}

	public void setOpenedBackgroundResource(int openedResid) {
		this.openedBackgroundResid = openedResid;
	}

	public void setChosenBackgroundResource(int selectedResid) {
		this.chosenBackgroundResid = selectedResid;
	}

	public void setOpenedBackgroundColor(int openedColor) {
		this.openedBackgroundColor = openedColor;
	}

	public void setChosenBackgroundColor(int selectedColor) {
		this.chosenBackgroundColor = selectedColor;
	}

	public void setItemChosen(int position, boolean chosen) {
		if (!chosen && isItemChosen(position)) {
			chosenItems.remove(position);
		} else if (chosen && !isItemChosen(position)) {
			chosenItems.add(position);
		}
	}

	@Override
	public final View getView(final int position, View convertView,
			ViewGroup parent) {
		View v = getViewHca(position, convertView, parent);
		EnhancedCheckBox checkbox = (EnhancedCheckBox) v
				.findViewById(android.R.id.checkbox);
		if (checkbox != null) {
			checkbox.setCheckedProgrammatically(false);
			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					mCallback.onRowItemChosenChanged(position, isChecked);

				}
			});
		}
		v.setBackgroundResource(0);
		if (isItemOpened(position)) {
			setViewAsOpened(v);
		}
		
		if (isItemChosen(position)) {
			if (checkbox != null) {
				checkbox.setCheckedProgrammatically(true);
			}
			setViewAsChosen(v);
		}
		
		return v;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
	private void setViewAsChosen(View v) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (this.chosenBackgroundDrawable != null) {
				v.setBackground(chosenBackgroundDrawable);
				return;
			}
		}
		if (this.chosenBackgroundResid != 0) {
			v.setBackgroundResource(this.chosenBackgroundResid);
			return;
		}
		if (this.chosenBackgroundColor != -1) {
			v.setBackgroundColor(this.chosenBackgroundColor);
			return;
		}
		v.setBackgroundColor(COLOR_CHOSEN_ITEM);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
	private void setViewAsOpened(View v) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (this.openedBackgroundDrawable != null) {
				v.setBackground(openedBackgroundDrawable);
				return;
			}
		}
		if (this.openedBackgroundResid != 0) {
			v.setBackgroundResource(this.openedBackgroundResid);
			return;
		}
		if (this.openedBackgroundColor != -1) {
			v.setBackgroundColor(this.openedBackgroundColor);
			return;
		}
		v.setBackgroundColor(COLOR_OPENED_ITEM);
	}

	public abstract View getViewHca(int position, View convertView,
			ViewGroup parent);

	public boolean isItemChosen(int position) {
		return chosenItems.contains(position);
	}

	public Set<Integer> getChosenItems() {
		return chosenItems;
	}

	public void setOpenedItem(int position) {
		this.openedItem = position;
	}

	public int getOpenedItem() {
		return this.openedItem;
	}

	public boolean isItemOpened(int position) {
		return this.openedItem == position;
	}

	public void clearChoices() {
		chosenItems.clear();
	}

	public void toggleItem(int position) {
		if (isItemChosen(position)) {
			chosenItems.remove(position);
		} else {
			chosenItems.add(position);
		}
	}
	
	public int getChosenItemsCount(){
		return this.chosenItems.size();
	}
}
