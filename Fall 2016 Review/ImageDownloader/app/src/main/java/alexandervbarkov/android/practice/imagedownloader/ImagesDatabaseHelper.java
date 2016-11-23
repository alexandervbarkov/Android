package alexandervbarkov.android.practice.imagedownloader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ImagesDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "IMAGES";
	private static final int DATABASE_VERSION = 1;
	public static final String IMAGES = "IMAGES";
	public static final String ID = "_id";
	public static final String IMAGE = "IMAGE";
	public static final String TITLE = "TITLE";
	private static ImagesDatabaseHelper imagesDatabaseHelper;
	private static int numberOfImages;
	private static ImageDatabaseCallbacks imageDatabaseCallbacks;
	private static List<Image> images = new LinkedList<>();

	public interface ImageDatabaseCallbacks {
		void onImagesDownloadProblem();

		void onImagesRetrieved(Cursor cursor);

		void onImagesRetrieveProblem();
	}

	private ImagesDatabaseHelper(Context context, int numberOfImages) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		ImagesDatabaseHelper.numberOfImages = numberOfImages;
		imageDatabaseCallbacks = (ImageDatabaseCallbacks)context;
		getReadableDatabase();
	}

	public static ImagesDatabaseHelper getInstance(Context context, int numberOfImages) {
		if(imagesDatabaseHelper == null)
			imagesDatabaseHelper = new ImagesDatabaseHelper(context, numberOfImages);
		return imagesDatabaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + IMAGES + " (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				IMAGE + " BLOB, " +
				TITLE + " STRING);");
		downloadImages();
		insertImages();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private void downloadImages() {
		new ImageDownloader().execute();
	}

	private void insertImages() {
		new ImageInserter().execute();
	}

	public void retrieveImages() {
		new ImageRetriever().execute();
	}

	public void removeImages() {
		new ImageRemover().execute();
	}

	public void refreshImages() {
		removeImages();
		downloadImages();
		insertImages();
		retrieveImages();
	}

	private class ImageDownloader extends AsyncTask<Void, Void, List<Image>> {
		@Override
		protected List<Image> doInBackground(Void... params) {
			try {
				images = FlickrDownlaoder.getRecentImages(numberOfImages);
				return images;
			}
			catch(IOException | XmlPullParserException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Image> images) {
			if(images == null || images.isEmpty())
				imageDatabaseCallbacks.onImagesDownloadProblem();
		}
	}

	private class ImageInserter extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			SQLiteDatabase db = getWritableDatabase();
			for(Image image : images) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(IMAGE, image.getImage());
				contentValues.put(TITLE, image.getTitle());
				db.insert(IMAGES, null, contentValues);
			}
			return null;
		}
	}

	private class ImageRetriever extends AsyncTask<Void, Void, Cursor> {
		@Override
		protected Cursor doInBackground(Void... params) {
			return getReadableDatabase().query(IMAGES, new String[]{ID, IMAGE, TITLE}, null, null, null, null, null);
		}

		@Override
		protected void onPostExecute(Cursor cursor) {
			if(cursor.getCount() == 0)
				imageDatabaseCallbacks.onImagesRetrieveProblem();
			else
				imageDatabaseCallbacks.onImagesRetrieved(cursor);
		}
	}

	private class ImageRemover extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			getWritableDatabase().delete(IMAGES, null, null);
			return null;
		}
	}
}
