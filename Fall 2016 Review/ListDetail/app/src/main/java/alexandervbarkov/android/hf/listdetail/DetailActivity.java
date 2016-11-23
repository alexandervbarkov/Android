package alexandervbarkov.android.hf.listdetail;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ShareActionProvider;

public class DetailActivity extends Activity implements DrawerFragment.DrawerClick {
	private DrawerLayout drawerLayout;
	private FrameLayout flDrawer;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private DrawerFragment drawerFragment;
	private long number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		// Setup fragment
		DetailFragment detailFragment = new DetailFragment();
		detailFragment.setArguments(getIntent().getExtras());
		if(savedInstanceState == null) {
			number = getIntent().getLongExtra(ListDetailActivity.EXTRA_NUMBER, 0);
			getFragmentManager().beginTransaction().add(R.id.fl_detail_fragment_cont, detailFragment, Long.toString(number)).commit();
		}
		else {
			number = savedInstanceState.getLong(ListDetailActivity.EXTRA_NUMBER);
		}
		// Setup actionbar
		getActionBar().setTitle(Long.toString(number));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				String tag = getFragmentManager().findFragmentById(R.id.fl_detail_fragment_cont).getTag();
				number = Long.parseLong(tag);
				getActionBar().setTitle(tag);
			}
		});
		// Setup drawer
		drawerFragment = new DrawerFragment();
		drawerFragment.setArguments(getIntent().getExtras());
		drawerLayout = (DrawerLayout)findViewById(R.id.dl);
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
				getFragmentManager().beginTransaction().add(R.id.fl_list_fragment_cont, drawerFragment).commit();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu();
				getFragmentManager().beginTransaction().remove(drawerFragment).commit();
			}
		};
		drawerLayout.setDrawerListener(actionBarDrawerToggle);
		flDrawer = (FrameLayout)findViewById(R.id.fl_list_fragment_cont);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ListDetailActivity.EXTRA_NUMBER, number);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_detail, menu);
		((ShareActionProvider)menu.findItem(R.id.action_share).getActionProvider())
				.setShareIntent(new Intent(Intent.ACTION_SEND)
						.setType("text/plain")
						.putExtra(Intent.EXTRA_TEXT, getIntent().getLongExtra(ListDetailActivity.EXTRA_NUMBER, 0)));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(actionBarDrawerToggle.onOptionsItemSelected(item))
			return true;
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_share).setVisible(!drawerLayout.isDrawerOpen(flDrawer));
		return super.onPrepareOptionsMenu(menu);
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

	@Override
	public void onClick(long number) {
		// Swap fragments
		this.number = number;
		Bundle bundle = new Bundle();
		bundle.putLong(ListDetailActivity.EXTRA_NUMBER, number);
		DetailFragment detailFragment = new DetailFragment();
		detailFragment.setArguments(bundle);
		getFragmentManager().beginTransaction()
				.replace(R.id.fl_detail_fragment_cont, detailFragment, Long.toString(number))
				.addToBackStack(null)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
		// Update title
		getActionBar().setTitle(Long.toString(number));
		// Close drawer
		drawerLayout.closeDrawer(flDrawer);
	}
}
