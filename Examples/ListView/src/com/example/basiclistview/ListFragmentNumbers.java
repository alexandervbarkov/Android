package com.example.basiclistview;

import java.util.ArrayList;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListFragmentNumbers extends ListFragment {
	private ArrayList<Number> mNumbers;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNumbers = Numbers.getNumbers(getActivity()).getAllNumbers();
		ArrayAdapterNumbers aan = new ArrayAdapterNumbers(mNumbers);
		setListAdapter(aan);
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Number n = (Number)getListAdapter().getItem(position);
		Toast.makeText(getActivity(), n.toString() + " was clicked", Toast.LENGTH_SHORT).show();
	}
	
	private class ArrayAdapterNumbers extends ArrayAdapter<Number> {
		public ArrayAdapterNumbers(ArrayList<Number> numbers) {
			super(getActivity(), 0, numbers);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_number, null);
			
			Number n = getItem(position);
			
			TextView tvValue = (TextView)convertView.findViewById(R.id.tv_number);
			tvValue.setText(n.toString());
			
			CheckBox cbOdd = (CheckBox)convertView.findViewById(R.id.cb_odd);
			cbOdd.setChecked(n.isOdd());
			
			return convertView;
		}
	}
}
