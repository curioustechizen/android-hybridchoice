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

/**
 * An {@code ArrayAdapter} that keeps track of the currently opened item as well
 * as currently chosen items. This enables it to provide a "Hybrid" choice mode
 * to the list view it is associated with.
 * 
 * @author Kiran Rao
 * 
 * @param <T>
 */
public abstract class HybridChoiceAdapter<T> extends ArrayAdapter<T> {

	/**
	 * The set of currently chosen items
	 */
	private Set<Integer> chosenItems = new HashSet<Integer>();

	/**
	 * The currently opened item
	 */
	private int openedItem = -1;

	/**
	 * The callback for when the Checkbox in the row item is checked (if any)
	 */
	private RowItemChoiceCallback mCallback;

	/*
	 * The default background color for and opened item
	 */
	private static final int COLOR_OPENED_ITEM = Color.parseColor("#ff0099cc"); // holo_blue_dark

	/*
	 * The default background color for chosen items
	 */
	private static final int COLOR_CHOSEN_ITEM = Color.parseColor("#ff00ddff"); // holo_blue_bright

	/*
	 * The Drawables to set as background for opened and chosen items
	 */
	private Drawable openedBackgroundDrawable, chosenBackgroundDrawable;

	/*
	 * The resource IDs to set as background for opened and chosen items
	 */
	private int openedBackgroundResid, chosenBackgroundResid;

	/*
	 * The colors to set as backgrounds for opened and chosen items
	 */
	private int openedBackgroundColor = -1, chosenBackgroundColor = -1;

	/**
	 * Callback interface for notifying the interested components about when a
	 * checkbox (if any) is clicked.
	 * 
	 * This is required only if the row items have the optional {@link EnhancedCheckBox} in them
	 */
	public interface RowItemChoiceCallback {

		/**
		 * Called when a checkbox is clicked to change the chosen state of the
		 * item
		 * 
		 * @param position
		 *            The position in the adapter where the checkbox click
		 *            happened
		 * @param chosen
		 *            The new state of the checkbox - whether it is checked
		 */
		void onRowItemChosenChanged(int position, boolean chosen);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The {@code Context}
	 * @param objects
	 *            The list of objects to be displayed by the adapter
	 * @param callback
	 *            The implementation of the callback interface for when the
	 *            checkbox in a row is selected. Can be {@code null} if you do
	 *            not have checkboxes in your rows.
	 */
	public HybridChoiceAdapter(Context context, List<T> objects,
			RowItemChoiceCallback callback) {
		super(context, android.R.id.text1, objects);
		this.mCallback = callback;
	}

	/**
	 * Sets the background {@code Drawable} for the opened item 
	 * @param openedDrawable The background Drawable indicating the opened item
	 */
	public void setOpenedBackgroundDrawable(Drawable openedDrawable) {
		this.openedBackgroundDrawable = openedDrawable;
	}

	/**
	 * Sets the background {@code Drawable} for chosen items
	 * @param selectedBackground The drawable that indicates chosen items
	 */
	public void setChosenBackgroundDrawable(Drawable selectedBackground) {
		this.chosenBackgroundDrawable = selectedBackground;
	}

	/**
	 * Sets the resource for opened item
	 * @param openedResid A resource ID that indicates the opened item
	 */
	public void setOpenedBackgroundResource(int openedResid) {
		this.openedBackgroundResid = openedResid;
	}

	/**
	 * Sets the resource chosen items
	 * @param selectedResid A resource ID that indicates the chosen items
	 */
	public void setChosenBackgroundResource(int selectedResid) {
		this.chosenBackgroundResid = selectedResid;
	}

	/**
	 * Sets the background color for the opened item
	 * @param openedColor A color that indicates an opened item
	 */
	public void setOpenedBackgroundColor(int openedColor) {
		this.openedBackgroundColor = openedColor;
	}

	/**
	 * Sets the background color for chosen items
	 * @param selectedColor A color indicating chosen items
	 */
	public void setChosenBackgroundColor(int selectedColor) {
		this.chosenBackgroundColor = selectedColor;
	}

	/**
	 * Changes the chosen state of an item
	 * @param position The position of the item for which the chosen state is being changes
	 * @param chosen Whether the item is to be set as chosen
	 */
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
		
		/*
		 * Get the row view from the user supplied implementation
		 */
		View v = getViewHca(position, convertView, parent);
		
		/*
		 * If it contains an EnhancedCheckbox, set listeners for checked changes
		 */
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
		
		/*
		 * First, clear the background
		 */
		v.setBackgroundResource(0);
		
		/*
		 * If an item is opened, update the UI to indicate this visually
		 */
		if (isItemOpened(position)) {
			setViewAsOpened(v);
		}

		/*
		 * If an item is chosen, update the UI to indicate this visually
		 */
		if (isItemChosen(position)) {
			if (checkbox != null) {
				checkbox.setCheckedProgrammatically(true);
			}
			setViewAsChosen(v);
		}

		return v;
	}

	/**
	 * Set the row view UI to indicate that it is chosen. The default implementation works as follows:
	 * <ol>
	 *   <li>In API 16 and above, if a {@link #setChosenBackgroundDrawable(Drawable)} has been called, then set that drawable as background
	 *   <li>Else, if {@link #setChosenBackgroundResource(int)} has been called, then set that resource as the background resource
	 *   <li>Else, if {@link #setChosenBackgroundColor(int)} has been called, then set that color as the background color.
	 *   <li>Else, set the default (holo_blue_bright) as the background color
	 * </ol>
	 * @param v
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setViewAsChosen(View v) {
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

	
	/**
	 * Set the row view UI to indicate that it is opened. The default implementation works as follows:
	 * <ol>
	 *   <li>In API 16 and above, if a {@link #setOpenedBackgroundDrawable(Drawable)} has been called, then set that drawable as background
	 *   <li>Else, if {@link #setOpenedBackgroundResource(int)} has been called, then set that resource as the background resource
	 *   <li>Else, if {@link #setOpenedBackgroundColor(int)} has been called, then set that color as the background color.
	 *   <li>Else, set the default (holo_blue_dark) as the background color
	 * </ol>
	 * @param v
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setViewAsOpened(View v) {
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

	/**
	 * Get the view for a row item. This MUST be overridden to return the row view. Analogous to {@link ArrayAdapter#getView(int, View, ViewGroup)}
	 * @param position The position in the list adapter for which the row vie has to be returned
	 * @param convertView An existing view to be used for recycling
	 * @param parent The parent - useful for inflation
	 * @return The view representing the current row
	 */
	public abstract View getViewHca(int position, View convertView,
			ViewGroup parent);

	/**
	 * Whether an item is chosen
	 * @param position The position of the item
	 * @return {@code true} if the item at that position is chosen, {@code false} otherwise
	 */
	public boolean isItemChosen(int position) {
		return chosenItems.contains(position);
	}

	/**
	 * Get the currently chosen items
	 * @return The currently chosen items as a set of their positions. If no items are chosen, returns an empty set. 
	 */
	public Set<Integer> getChosenItems() {
		return chosenItems;
	}

	/**
	 * Set the currently opened item
	 * @param position The position of the currently opened item
	 */
	public void setOpenedItem(int position) {
		this.openedItem = position;
	}

	/**
	 * Get the currently opened item
	 * @return The position of the currently opened item
	 */
	public int getOpenedItem() {
		return this.openedItem;
	}

	/**
	 * Whether an item is opened
	 * @param position The position of the item
	 * @return {@code true} if the item is opened, {@code false} otherwise
	 */
	public boolean isItemOpened(int position) {
		return this.openedItem == position;
	}

	/**
	 * Clear all choices. This sets all items to the not chosen state
	 */
	public void clearChoices() {
		chosenItems.clear();
	}

	/**
	 * Toggle the chosen state of an item
	 * @param position The position of the item whose chosen state should be toggled
	 */
	public void toggleItem(int position) {
		if (isItemChosen(position)) {
			chosenItems.remove(position);
		} else {
			chosenItems.add(position);
		}
	}

	/**
	 * Get the number of chosen items. Useful for displaying in the Contextual Action Bar for example.
	 * @return The number of chosen items
	 */
	public int getChosenItemsCount() {
		return this.chosenItems.size();
	}
}
