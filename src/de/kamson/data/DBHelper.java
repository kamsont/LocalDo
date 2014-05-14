package de.kamson.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	
	private static final int DATABASE_VERSION = 1; 
	private static final String DATABASE_NAME = "localdo.db"; 
	  
	/*
	 * Task-table
	 */
	public static final String TABLE_TASKS = "tasks";	  
	public static final String TASKS_COLUMN_ID = "_id";	  
	public static final String TASKS_COLUMN_NAME = "name";
	public static final String TASKS_COLUMN_DEADLINE = "deadline";
	public static final String TASKS_COLUMN_DEADLINE_ALERT = "deadline_alert";
	public static final String TASKS_COLUMN_ACTIVE = "active";
	public static final String TASKS_COLUMN_COLOR = "color";
	public static final String TASKS_COLUMN_NOTES = "notes";
	
	private static final String TABLE_TASKS_CREATE = "CREATE TABLE " +  
	        TABLE_TASKS + " (" + TASKS_COLUMN_ID + " integer primary key autoincrement, " + 	 
	        TASKS_COLUMN_NAME + " String, " +
	        TASKS_COLUMN_DEADLINE + " integer, " +
	        TASKS_COLUMN_DEADLINE_ALERT + " integer, " +
	        TASKS_COLUMN_ACTIVE + " integer, " + 
	        TASKS_COLUMN_COLOR + " integer, " + 
	        TASKS_COLUMN_NOTES + " String);";
	
	/*
	 * Location-table
	 */
	public static final String TABLE_LOCATIONS = "locations";	  
	public static final String LOCATIONS_COLUMN_ID = "_id";	  
	public static final String LOCATIONS_COLUMN_NAME = "name";
	public static final String LOCATIONS_COLUMN_LATITUDE = "latitude";
	public static final String LOCATIONS_COLUMN_LONGITUDE = "longitude";
	public static final String LOCATIONS_COLUMN_RANGE = "range";
	
	private static final String TABLE_LOCATIONS_CREATE = "CREATE TABLE " +  
	        TABLE_LOCATIONS + " (" + LOCATIONS_COLUMN_ID + " integer primary key autoincrement, " + 	 
	        LOCATIONS_COLUMN_NAME + " String, " +
	        LOCATIONS_COLUMN_LATITUDE + " integer, " +
	        LOCATIONS_COLUMN_LONGITUDE + " integer, " +
	        LOCATIONS_COLUMN_RANGE + " integer);";
	
	/*
	 * Task_Location-table
	 */
	public static final String TABLE_TASKS_LOCATIONS = "tasks_locations";	  
	public static final String TASKS_LOCATIONS_COLUMN_ID = "_id";	  
	public static final String TASKS_LOCATIONS_COLUMN_TASK_ID = "task_id";
	public static final String TASKS_LOCATIONS_COLUMN_LOCATION_ID = "location_id";	
	
	private static final String TABLE_TASKS_LOCATIONS_CREATE = "CREATE TABLE " +  
	        TABLE_TASKS_LOCATIONS + " (" + TASKS_LOCATIONS_COLUMN_ID + " integer primary key autoincrement, " + 	 
	        TASKS_LOCATIONS_COLUMN_TASK_ID + " integer, " +
	        TASKS_LOCATIONS_COLUMN_LOCATION_ID + " integer);";
	  
	  

	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_TASKS_CREATE);
		db.execSQL(TABLE_LOCATIONS_CREATE);
		db.execSQL(TABLE_TASKS_LOCATIONS_CREATE);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS_LOCATIONS);
	        onCreate(db);
	}
	
}