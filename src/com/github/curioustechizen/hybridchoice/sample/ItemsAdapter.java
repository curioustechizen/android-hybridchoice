package com.github.curioustechizen.hybridchoice.sample;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.hybridchoice.HybridChoiceAdapter;
import com.github.curioustechizen.hybridchoice.R;
import com.github.curioustechizen.hybridchoice.dummy.DummyContent;
import com.github.curioustechizen.hybridchoice.dummy.DummyContent.DummyItem;

public class ItemsAdapter extends HybridChoiceAdapter<DummyContent.DummyItem> {

	public ItemsAdapter(Context context, List<DummyItem> objects) {
		super(context, objects, null);
	}

	@Override
	public View getViewHca(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row_item, parent, false);
		}
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(getItem(position).content);
		return convertView;
	}

}
