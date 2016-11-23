package alexandervbarkov.android.hf.listdetail;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MyListFragment extends ListFragment {
	private MyListClick click;

	public interface MyListClick {
		void onClick(long number);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		List<Integer> numbers = new ArrayList<>();
		int listSize = getArguments().getInt(ListDetailActivity.ARG_LIST_SIZE);
		for(int i = 0; i < listSize; ++i)
			numbers.add(i);
		setListAdapter(new ArrayAdapter<Integer>(inflater.getContext(), android.R.layout.simple_list_item_1, numbers));
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		click = (MyListClick)context;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		click = (MyListClick)activity;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(click != null)
			click.onClick(id);
	}
}
