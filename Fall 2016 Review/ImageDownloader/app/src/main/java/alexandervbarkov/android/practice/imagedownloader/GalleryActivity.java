package alexandervbarkov.android.practice.imagedownloader;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class GalleryActivity extends Activity implements ImagesDatabaseHelper.ImageDatabaseCallbacks {
	private static final int NUMEBR_OF_IMAGES = 10; // How many images will be downloaded, stored, and displayed
	private static ImagesDatabaseHelper db;
	private static Cursor cursor;
	private static RecyclerView rvImages;
	private static ProgressBar pbImages;
	private static ImagesAdapter imagesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		db = ImagesDatabaseHelper.getInstance(this, NUMEBR_OF_IMAGES);
		rvImages = (RecyclerView)findViewById(R.id.rv_images);
		rvImages.setAdapter(null);
		rvImages.setLayoutManager(new StaggeredGridLayoutManager(getNumberOfColumns(FlickrDownlaoder.MAX_IMAGE_SIZE), StaggeredGridLayoutManager.VERTICAL));
		pbImages = (ProgressBar)findViewById(R.id.pb_images);
	}

	@Override
	protected void onResume() {
		super.onResume();
		db.retrieveImages();
	}
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		if(cursor != null)
//			cursor.close();
//		if(db != null)
//			db.close();
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_gallery, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_refresh:
				db.refreshImages();
				pbImages.setVisibility(View.VISIBLE);
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onImagesDownloadProblem() {
		pbImages.setVisibility(View.GONE);
		Toast.makeText(this, "Problem downloading images", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onImagesRetrieved(Cursor cursor) {
		pbImages.setVisibility(View.GONE);
//		Toast.makeText(this, "Successfully got " + cursor.getCount() + " images", Toast.LENGTH_LONG).show();
		if(GalleryActivity.cursor != null)
			GalleryActivity.cursor.close();
		GalleryActivity.cursor = cursor;
//		if(imagesAdapter == null) {
		imagesAdapter = new ImagesAdapter(cursor);
		rvImages.setAdapter(imagesAdapter);
//		}
//		else {
//			imagesAdapter.changeCursor(cursor);
//		}
	}

	@Override
	public void onImagesRetrieveProblem() {
		pbImages.setVisibility(View.GONE);
		Toast.makeText(this, "Problem retrieving images", Toast.LENGTH_LONG).show();
	}

	/**
	 * @param viewWidth is the width of the columns.
	 * @return how many columns will fit on the screen.
	 */
	private int getNumberOfColumns(int viewWidth) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		return displayMetrics.widthPixels / viewWidth;
	}
}
