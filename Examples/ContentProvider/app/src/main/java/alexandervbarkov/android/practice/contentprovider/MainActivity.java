package alexandervbarkov.android.practice.contentprovider;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final CursorAdapter listAdapter = new SimpleCursorAdapter(MainActivity.this,
				android.R.layout.simple_list_item_1,
				null,
				new String[]{ContactsContract.Contacts.DISPLAY_NAME},
				new int[]{android.R.id.text1},
				0);
		setListAdapter(listAdapter);
		getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {
				return new CursorLoader(MainActivity.this,
						ContactsContract.Contacts.CONTENT_URI,
						new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
						ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1",
						null,
						ContactsContract.Contacts.DISPLAY_NAME);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
				Toast.makeText(MainActivity.this, "size: " + data.getCount(), Toast.LENGTH_LONG).show();
				listAdapter.swapCursor(data);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				listAdapter.swapCursor(null);
			}
		});
	}
}
