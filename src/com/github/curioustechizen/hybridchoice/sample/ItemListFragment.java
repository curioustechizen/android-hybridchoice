package com.github.curioustechizen.hybridchoice.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.github.curioustechizen.hybridchoice.HybridChoiceAdapter.RowItemChoiceCallback;
import com.github.curioustechizen.hybridchoice.R;
import com.github.curioustechizen.hybridchoice.dummy.DummyContent;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'opened' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ItemDetailFragment}. Finally, it supports multiple selection by giving
 * items a different 'chosen' state upon long-press.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment implements
		OnItemLongClickListener, RowItemChoiceCallback {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_OPENED_POSITION = "opened_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mOpenedPosition = ListView.INVALID_POSITION;

	private ItemsAdapter mAdapter;
	private ActionMode mActionMode;

	/**
	 * The callbak for handling the ActionMode. Responsible for displaying
	 * contextual actions, and handling contextual action clicks
	 */
	private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// Do Nothing
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			/*
			 * Whenever the action mode is dismissed, clear all chosen items
			 */
			mAdapter.clearChoices();
			mAdapter.notifyDataSetChanged();
			mActionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			if (mActionMode != null)
				return false;
			mActionMode = mode;
			mode.getMenuInflater().inflate(R.menu.list_cab, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if (item.getItemId() == R.id.itemDelete) {
				deleteAll();
				mActionMode.finish();
				return true;
			}
			return false;
		}

	};

	private void deleteAll() {
		Toast.makeText(getActivity(), "Delete action selected",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Create an instance of our implementation of HybridChoiceAdapter, and
		 * set it as the ListAdapter
		 */
		mAdapter = new ItemsAdapter(getActivity(), DummyContent.ITEMS);
		setListAdapter(mAdapter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemLongClickListener(this);
		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_OPENED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_OPENED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		/*
		 * Set the current item as the opened item on the Adapter. 
		 * Also, when an item is opened, clear all chosen items and dismiss the CAB
		 * 
		 * Note that you  may wish to have a different behavior here. 
		 * 
		 * For example, in Google I/O 2013 app, if an item is already chosen, a single click on another
		 * item chooses it.
		 */
		mAdapter.setOpenedItem(position);
		mAdapter.clearChoices();
		if (mActionMode != null) {
			mActionMode.finish();
		}
		mAdapter.notifyDataSetChanged();

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		/*
		 * When an item is long-clicked, toggle the chosen state of that item. Also update the CAB accordingly
		 * 
		 * Note that you may wish to have different behavior here. 
		 * For example, you may want only the first long-click to start the contextual action mode 
		 * and ignore subsequent long presses since you use single-clicks to continue choosing items
		 */
		mAdapter.toggleItem(position);
		updateActionMode();
		return true;
	}

	private void updateActionMode() {
		/*
		 * Start the action mode if it isn't already started
		 */
		if (mActionMode == null) {
			mActionMode = getListView().startActionMode(actionModeCallback);
		}

		/*
		 * Update the title of the CAB to indicate number of items chosen
		 */
		mActionMode.setTitle(String.format("%d chosen",
				mAdapter.getChosenItemsCount()));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRowItemChosenChanged(int position, boolean chosen) {
		mAdapter.setItemChosen(position, chosen);
		updateActionMode();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mOpenedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_OPENED_POSITION, mOpenedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mOpenedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mOpenedPosition = position;
	}

	

}
