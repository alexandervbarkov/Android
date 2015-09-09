package alexandervbarkov.android.bnr.locationtracker;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class DatabaseHelperRecord extends SQLiteOpenHelper {
	private static final String DB_NAME = "records.sqlite";
	private static final int DB_VERSION = 1;
	
	private static final String TABLE_RECORDS = "records";
	private static final String COLUMN_RECORDS_ID = "_id";
	private static final String COLUMN_RECORDS_TITLE = "title";
	private static final String COLUMN_RECORDS_START_TIME = "start_time";
	private static final String COLUMN_RECORDS_DISTANCE = "distance";
	
	private static final String TABLE_LOCATIONS = "locations";
	private static final String COLUMN_LOCATIONS_RECORD_ID = "record_id";
	private static final String COLUMN_LOCATIONS_TIMESTAMP = "timestamp";
	private static final String COLUMN_LOCATIONS_LATITUDE = "latitude";
	private static final String COLUMN_LOCATIONS_LONGITUDE = "longitude";
	private static final String COLUMN_LOCATIONS_ALTITUDE = "altitude";
	private static final String COLUMN_LOCATIONS_PROVIDER = "provider";
	
	public DatabaseHelperRecord(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the records table
		db.execSQL("create table " + TABLE_RECORDS + " (" +	
				COLUMN_RECORDS_ID + " integer primary key autoincrement, " +
				COLUMN_RECORDS_TITLE + " text, " +
				COLUMN_RECORDS_START_TIME +	" integer, " +
				COLUMN_RECORDS_DISTANCE + " real)");
		// Create the locations table
		db.execSQL("create table " + TABLE_LOCATIONS + " (" +
				COLUMN_LOCATIONS_RECORD_ID + " integer references records(_id), " +
				COLUMN_LOCATIONS_TIMESTAMP + " integer, " +
				COLUMN_LOCATIONS_LATITUDE + " real, " +
				COLUMN_LOCATIONS_LONGITUDE + " real, " +
				COLUMN_LOCATIONS_ALTITUDE + " real, " +
				COLUMN_LOCATIONS_PROVIDER + " varchar(100))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Implement schema changes and data message here when upgrading
	}
	
	public long insertRecord(Record record) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_RECORDS_START_TIME, record.getStartTime().getTime());
		return getWritableDatabase().insert(TABLE_RECORDS, null, cv);
	}
	
	public long updateRecordTitle(Record record) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_RECORDS_TITLE, record.getTitle());
		return getWritableDatabase().update(TABLE_RECORDS, cv, COLUMN_RECORDS_ID + " = ?", new String[] {Long.toString(record.getId())});
	}
	
	public long updateRecordDistance(long id, double distance) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_RECORDS_DISTANCE, distance);
		return getWritableDatabase().update(TABLE_RECORDS, cv, COLUMN_RECORDS_ID + " = ?", new String[] {Long.toString(id)});
	}
	
	public CursorRecords queryRecords() {
		// Equivalent to "select * from records order by start_time asc"
		Cursor wrapped = getReadableDatabase().query(TABLE_RECORDS, null, null, null, null, null, COLUMN_RECORDS_START_TIME + " asc");
		return new CursorRecords(wrapped);
	}
	
	public CursorRecords queryRecords(long id) {
		Cursor wrapped = getReadableDatabase().query(TABLE_RECORDS, 
				null, // All columns
				COLUMN_RECORDS_ID + " = ?", // Look for a record id
				new String[] {String.valueOf(id)}, // where value is equal to id
				null, // group by
				null, // order by
				null, // having, 
				"1"); // limit 1 row
		return new CursorRecords(wrapped);
	}
	
	public long deleteRecord(Record record) {
		return getWritableDatabase().delete(TABLE_RECORDS, COLUMN_RECORDS_ID + " = ?", new String[] {Long.toString(record.getId())});
	}
	
	public long insertLocation(long recordId, Location location) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LOCATIONS_RECORD_ID, recordId);
		cv.put(COLUMN_LOCATIONS_TIMESTAMP, location.getTime());
		cv.put(COLUMN_LOCATIONS_LATITUDE, location.getLatitude());
		cv.put(COLUMN_LOCATIONS_LONGITUDE, location.getLongitude());
		cv.put(COLUMN_LOCATIONS_ALTITUDE, location.getAltitude());
		cv.put(COLUMN_LOCATIONS_PROVIDER, location.getProvider());
		return getWritableDatabase().insert(TABLE_LOCATIONS, null, cv);
	}
	
	public CursorLocations queryLastLocationForRecord(long recordId) {
		Cursor wrapped = getReadableDatabase().query(TABLE_LOCATIONS,
				null,
				COLUMN_LOCATIONS_RECORD_ID + " = ?", 
				new String[]{String.valueOf(recordId)},
				null,
				null,
				COLUMN_LOCATIONS_TIMESTAMP + " desc",
				"1");
		return new CursorLocations(wrapped);
	}
	
	public CursorLocations queryAllLocationForRecord(long recordId) {
		Cursor wrapped = getReadableDatabase().query(TABLE_LOCATIONS,
				null,
				COLUMN_LOCATIONS_RECORD_ID + " = ?", 
				new String[] {String.valueOf(recordId)},
				null,
				null,
				COLUMN_LOCATIONS_TIMESTAMP + " asc");
		return new CursorLocations(wrapped);
	}
	
	public long deleteAllLocationsForRecord(Record record) {
		return getWritableDatabase().delete(TABLE_LOCATIONS, COLUMN_LOCATIONS_RECORD_ID + " = ?", new String[] {Long.toString(record.getId())});
	}
	
	public static class CursorRecords extends CursorWrapper {
		public CursorRecords(Cursor cursor) {
			super(cursor);
		}
		
		public Record getRecord() {
			if(isBeforeFirst() || isAfterLast()) 
				return null;
			Record record = new Record();
			long recordId = getLong(getColumnIndex(COLUMN_RECORDS_ID));
			record.setId(recordId);
			String title = getString(getColumnIndex(COLUMN_RECORDS_TITLE));
			record.setTitle(title);
			long startTime = getLong(getColumnIndex(COLUMN_RECORDS_START_TIME));
			record.setStartTime(new Date(startTime));
			double distance = getDouble(getColumnIndex(COLUMN_RECORDS_DISTANCE));
			record.setDistance(distance);
			return record;
		}
	}
	
	public static class CursorLocations extends CursorWrapper {
		public CursorLocations(Cursor cursor) {
			super(cursor);
		}
		
		public Location getLocation() {
			if(isBeforeFirst() || isAfterLast())
				return null;
			String provider = getString(getColumnIndex(COLUMN_LOCATIONS_PROVIDER));
			Location location = new Location(provider);
			location.setLatitude(getDouble(getColumnIndex(COLUMN_LOCATIONS_LATITUDE)));
			location.setLongitude(getDouble(getColumnIndex(COLUMN_LOCATIONS_LONGITUDE)));
			location.setAltitude(getDouble(getColumnIndex(COLUMN_LOCATIONS_ALTITUDE)));
			location.setTime(getLong(getColumnIndex(COLUMN_LOCATIONS_TIMESTAMP)));
			return location;
		}
	}
}