package alexandervbarkov.android.hf.navigationdrawer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	public static String ARG_NUMBER = "ARG_NUMBER";
	private DrawerLayout drawerLayout;
	private ListView lvDrawer;
	private ActionBarDrawerToggle actionBarDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Set drawer
		drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
		lvDrawer = (ListView)findViewById(R.id.lv_drawer);
		List<Integer> numbers = new ArrayList<>();
		for(int i = 0; i < 10; ++i) {
			numbers.add(i);
		}
		lvDrawer.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, numbers));
		lvDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setDetailFragment(id);
				drawerLayout.closeDrawer(lvDrawer);
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.abdt_open, R.string.abdt_close);
		// Set default fragment
		if(savedInstanceState == null)
			setDetailFragment(0);
		drawerLayout.setDrawerListener(actionBarDrawerToggle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(actionBarDrawerToggle.onOptionsItemSelected(item))
			return true;
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);

	}

	private void setDetailFragment(long number) {
		Bundle bundle = new Bundle();
		bundle.putLong(ARG_NUMBER, number);
		DetailFragment detailFragment = new DetailFragment();
		detailFragment.setArguments(bundle);
		getFragmentManager().beginTransaction()
				.replace(R.id.fl_detail_fragment_container, detailFragment)
				.addToBackStack(null)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
	}
}
