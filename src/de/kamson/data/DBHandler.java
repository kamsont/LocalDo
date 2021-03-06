package de.kamson.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHandler {
	
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allTasksColumns = { DBHelper.TASKS_COLUMN_ID, 
			DBHelper.TASKS_COLUMN_NAME, DBHelper.TASKS_COLUMN_DEADLINE, DBHelper.TASKS_COLUMN_DEADLINE_ALERT,
			DBHelper.TASKS_COLUMN_ACTIVE, DBHelper.TASKS_COLUMN_COLOR, DBHelper.TASKS_COLUMN_NOTES};
	private String[] allLocationsColumns = { DBHelper.LOCATIONS_COLUMN_ID, 
			DBHelper.LOCATIONS_COLUMN_NAME, DBHelper.LOCATIONS_COLUMN_LATITUDE, 
			DBHelper.LOCATIONS_COLUMN_LONGITUDE, DBHelper.LOCATIONS_COLUMN_RANGE, DBHelper.LOCATIONS_COLUMN_ANONYMOUS};
	private String[] allTasksLocationsColumns = { DBHelper.TASKS_LOCATIONS_COLUMN_ID, 
			DBHelper.TASKS_LOCATIONS_COLUMN_TASK_ID, DBHelper.TASKS_LOCATIONS_COLUMN_LOCATION_ID};
	
	public DBHandler(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long createTask(String name, long deadline, long deadline_alert, int active, int color, String notes) {
		ContentValues values = new ContentValues();				
		values.put(DBHelper.TASKS_COLUMN_NAME, name);	
		values.put(DBHelper.TASKS_COLUMN_DEADLINE, deadline);
		values.put(DBHelper.TASKS_COLUMN_DEADLINE_ALERT, deadline_alert);
		values.put(DBHelper.TASKS_COLUMN_ACTIVE, active);
		values.put(DBHelper.TASKS_COLUMN_COLOR, color);
		values.put(DBHelper.TASKS_COLUMN_NOTES, notes);
		
		return database.insert(DBHelper.TABLE_TASKS, null, values);		
	}
	
	public long createLocation(String name, double latitude, double longitude, int range, int anonymous) {
		ContentValues values = new ContentValues();				
		values.put(DBHelper.LOCATIONS_COLUMN_NAME, name);	
		values.put(DBHelper.LOCATIONS_COLUMN_LATITUDE, latitude);
		values.put(DBHelper.LOCATIONS_COLUMN_LONGITUDE, longitude);
		values.put(DBHelper.LOCATIONS_COLUMN_RANGE, range);
		values.put(DBHelper.LOCATIONS_COLUMN_ANONYMOUS, anonymous);
				
		return database.insert(DBHelper.TABLE_LOCATIONS, null, values);
		
	}
	
	public long createTaskLocation(long task_id, long location_id) {
		ContentValues values = new ContentValues();				
		values.put(DBHelper.TASKS_LOCATIONS_COLUMN_TASK_ID, task_id);	
		values.put(DBHelper.TASKS_LOCATIONS_COLUMN_LOCATION_ID, location_id);		
				
		return database.insert(DBHelper.TABLE_TASKS_LOCATIONS, null, values);		
	}
	
	public int updateTasks(long id, String name, long deadline, long deadline_alert, int active, int color, String notes) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.TASKS_COLUMN_NAME, name);
		values.put(DBHelper.TASKS_COLUMN_DEADLINE, deadline);
		values.put(DBHelper.TASKS_COLUMN_DEADLINE_ALERT, deadline_alert);
		values.put(DBHelper.TASKS_COLUMN_ACTIVE, active);
		values.put(DBHelper.TASKS_COLUMN_COLOR, color);
		values.put(DBHelper.TASKS_COLUMN_NOTES, notes);
		
		return database.update(DBHelper.TABLE_TASKS, values, DBHelper.TASKS_COLUMN_ID + " = " + id, null);
	}
	
	public int updateLocations(long id, String name, double latitude, double longitude, int range, int anonymous) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.LOCATIONS_COLUMN_NAME, name);
		values.put(DBHelper.LOCATIONS_COLUMN_LATITUDE, latitude);
		values.put(DBHelper.LOCATIONS_COLUMN_LONGITUDE, longitude);
		values.put(DBHelper.LOCATIONS_COLUMN_RANGE, range);
		values.put(DBHelper.LOCATIONS_COLUMN_ANONYMOUS, anonymous);
		
		return database.update(DBHelper.TABLE_LOCATIONS, values, DBHelper.LOCATIONS_COLUMN_ID + " = " + id, null);
	}
	
	// Not needed
//	public int updateTaskLocations(long id, String name, double latitude, double longitude, int range) {
//		
//	}
	
	public List<Task> getAllTasks() {
		Cursor cursor = database.query(DBHelper.TABLE_TASKS, allTasksColumns, null, null, null, null, null);		
		List<Task> allTasks = new ArrayList<Task>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			long id = cursor.getLong(0);			
			String name= cursor.getString(1);
			long deadline = cursor.getLong(2);
			long deadline_alert = cursor.getLong(3);
			int active = cursor.getInt(4);
			boolean isActive = (active == 1) ? true : false;
			int color = cursor.getInt(5);
			String notes = cursor.getString(6);			
			allTasks.add(new Task(id, name, deadline, deadline_alert, isActive, color, notes));
			cursor.moveToNext();
		}
		return allTasks;
	}
	
	// Not needed
	//	public int updateTaskLocations(long id, String name, double latitude, double longitude, int range) {
	//		
	//	}
		
		public Task getTask(long task_id) {
		Cursor cursor = database.query(DBHelper.TABLE_TASKS, allTasksColumns, DBHelper.TASKS_COLUMN_ID + " = " + task_id, null, null, null, null);
		if (cursor != null){
			cursor.moveToFirst();
			long id = cursor.getLong(0);			
			String name= cursor.getString(1);
			long deadline = cursor.getLong(2);
			long deadline_alert = cursor.getLong(3);
			int active = cursor.getInt(4);
			boolean isActive = (active == 1) ? true : false;
			int color = cursor.getInt(5);
			String notes = cursor.getString(6);	
			
			return new Task(id, name, deadline, deadline_alert, isActive, color, notes);			
		}
		else {
			return null;
		}
	}
	
	public List<MyLocation> getAllLocations() {
		Cursor cursor = database.query(DBHelper.TABLE_LOCATIONS, allLocationsColumns, null, null, null, null, null);		
		List<MyLocation> allLocations = new ArrayList<MyLocation>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			long id = cursor.getLong(0);			
			String name= cursor.getString(1);
			double latitude = cursor.getDouble(2);
			double longitude = cursor.getDouble(3);
			int range = cursor.getInt(4);
			int anonymous = cursor.getInt(5);
			boolean isAnonymous = (anonymous == 1) ? true : false;
			allLocations.add(new MyLocation(id, name, latitude, longitude, range, isAnonymous));
			cursor.moveToNext();
		}
		return allLocations;
	}
	
	public MyLocation getLocation(long location_id) {
		Cursor cursor = database.query(DBHelper.TABLE_LOCATIONS, allLocationsColumns, DBHelper.LOCATIONS_COLUMN_ID + " = " + location_id, null, null, null, null);		
		if (cursor != null){
			cursor.moveToFirst();
			long id = cursor.getLong(0);			
			String name= cursor.getString(1);
			double latitude = cursor.getDouble(2);
			double longitude = cursor.getDouble(3);
			int range = cursor.getInt(4);
			int anonymous = cursor.getInt(5);
			boolean isAnonymous = (anonymous == 1) ? true : false;
			return new MyLocation(id, name, latitude, longitude, range, isAnonymous);		
		}
		else {
			return null;
		}
	}
	
	public List<MyLocation> getLocationsToTask(long task_id) {
		// First get the ID list
		Cursor cursor = database.query(DBHelper.TABLE_TASKS_LOCATIONS, allTasksLocationsColumns, DBHelper.TASKS_LOCATIONS_COLUMN_TASK_ID + " = "+ task_id, null, null, null, null);		
		List<Long> locationIDs = new ArrayList<Long>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			locationIDs.add(cursor.getLong(2));			
			cursor.moveToNext();
		}
		// Get locations by the id list
		List<MyLocation> locations = new ArrayList<MyLocation>();
		for (long id:locationIDs) {
			locations.add(getLocation(id));
		}
		return locations;
	}
	
	public List<Task> getTasksToLocation(long location_id) {
		Cursor cursor = database.query(DBHelper.TABLE_TASKS_LOCATIONS, allTasksLocationsColumns, DBHelper.TASKS_LOCATIONS_COLUMN_LOCATION_ID + " = "+ location_id, null, null, null, null);		
		List<Long> taskIDs = new ArrayList<Long>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			taskIDs.add(cursor.getLong(1));
			cursor.moveToNext();
		}		
		List<Task> tasks = new ArrayList<Task>();
		for (long id:taskIDs) {
			tasks.add(getTask(id));
		}
		return tasks;
	}
	
	public List<String> getAllTasksLocations() {
		Cursor cursor = database.query(DBHelper.TABLE_TASKS_LOCATIONS, allTasksLocationsColumns, null, null, null, null, null);		
		List<String> allTasks = new ArrayList<String>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){				
			allTasks.add("TaskID:"+cursor.getLong(1)+"-"+"LocationID:"+cursor.getLong(2));
			cursor.moveToNext();
		}
		return allTasks;
	}
	
	public void deleteTask(long id){
	    System.out.println("Task deleted with id: " + id);
	    
	    // Delete the task from tasks table
	    database.delete(DBHelper.TABLE_TASKS, DBHelper.TASKS_COLUMN_ID
	        + " = " + id, null);
	    
	   deleteTaskFromTaskLocations(id);
	}
	
	public void deleteLocation(long id){
	    System.out.println("Location deleted with id: " + id);
	    
	    // Check if location is bound to any task should already be done in calling activity ?
	    
	    // Delete the locations from locations table
	    database.delete(DBHelper.TABLE_LOCATIONS, DBHelper.LOCATIONS_COLUMN_ID
	        + " = " + id, null);
	    
	    // Delete all entries bound to task from tasks_locations table should never be needed because of precheck
	    //database.delete(dbHelper.TABLE_TASKS_LOCATIONS, dbHelper.TASKS_COLUMN_ID
		//        + " = " + id, null);	    
	}
	
	public void deleteTaskFromTaskLocations(long id) {
		 // Delete all entries bound to task from tasks_locations table
	    database.delete(DBHelper.TABLE_TASKS_LOCATIONS, DBHelper.TASKS_LOCATIONS_COLUMN_TASK_ID
		        + " = " + id, null);
	}
	
	public void clearTasks(){
		database.delete(DBHelper.TABLE_TASKS, null, null);
	}
	
	public void clearLocations(){
		database.delete(DBHelper.TABLE_LOCATIONS, null, null);
	}
	
	public void clearTasks_Locations(){
		database.delete(DBHelper.TABLE_TASKS_LOCATIONS, null, null);
	}

}
