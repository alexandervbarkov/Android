package alexandervbarkov.android.hf.listdetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Random;

public class ListDetailActivity extends Activity implements MyListFragment.MyListClick {
	public static String EXTRA_NUMBER = "extra number";
	public static String ARG_LIST_SIZE = "list size";
	public static int LIST_SIZE = 15;
	private boolean isPortrait;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_detail);
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_LIST_SIZE, LIST_SIZE);
		MyListFragment myListFragment = new MyListFragment();
		myListFragment.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.fl_list_fragment_cont, myListFragment).commit();
		isPortrait = findViewById(R.id.fl_detail_fragment_cont) == null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_list_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_suggestion:
				openAlertDialogSuggestion();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(long number) {
		if(isPortrait)
			startIntent(number);
		else
			openFragment(number);
	}

	private void openFragment(long number) {
		Bundle bundle = new Bundle();
		bundle.putLong(EXTRA_NUMBER, number);
		DetailFragment detailFragment = new DetailFragment();
		detailFragment.setArguments(bundle);
		getFragmentManager().beginTransaction()
				.replace(R.id.fl_detail_fragment_cont, detailFragment)
				.addToBackStack(null)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
	}

	private void startIntent(long number) {
		startActivity(new Intent(this, DetailActivity.class).putExtra(EXTRA_NUMBER, number).putExtra(ARG_LIST_SIZE, LIST_SIZE));
	}

	private void openAlertDialogSuggestion() {
		new AlertDialog.Builder(this)
				.setTitle("Number suggestion")
				.setMessage("Try number " + Integer.toString(new Random().nextInt(LIST_SIZE)))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
	}
}
