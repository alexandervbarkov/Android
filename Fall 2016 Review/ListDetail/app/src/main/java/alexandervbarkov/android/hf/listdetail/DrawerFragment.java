package alexandervbarkov.android.hf.listdetail;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DrawerFragment extends Fragment {
	private DrawerClick drawerClick;

	public interface DrawerClick {
		void onClick(long number);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_drawer, container, false);
		List<Integer> numbers = new ArrayList<>();
		for(int i = 0; i < getArguments().getInt(ListDetailActivity.ARG_LIST_SIZE); ++i)
			numbers.add(i);
		ListView drawer = (ListView)view.findViewById(R.id.lv_drawer);
		drawer.setAdapter(new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_list_item_1, numbers));
		drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				drawerClick.onClick(id);
			}
		});
		return view;
	}

	@Override
	public void onAttach(Context context) {
		drawerClick = (DrawerClick)context;
		super.onAttach(context);
	}

	@Override
	public void onAttach(Activity activity) {
		drawerClick = (DrawerClick)activity;
		super.onAttach(activity);
	}
}
