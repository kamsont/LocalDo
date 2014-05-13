package de.kamson.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBHandler {
	
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allTasksColumns = { DBHelper.TASKS_COLUMN_ID, 
			DBHelper.TASKS_COLUMN_NAME, DBHelper.TASKS_COLUMN_DEADLINE, DBHelper.TASKS_COLUMN_DEADLINE_ALERT,
			DBHelper.TASKS_COLUMN_ACTIVE, DBHelper.TASKS_COLUMN_COLOR, DBHelper.TASKS_COLUMN_NOTES};
	private String[] allLocationsColumns = { DBHelper.LOCATIONS_COLUMN_ID, 
			DBHelper.LOCATIONS_COLUMN_NAME, DBHelper.LOCATIONS_COLUMN_LATITUDE, 
			DBHelper.LOCATIONS_COLUMN_LONGITUDE, DBHelper.LOCATIONS_COLUMN_RANGE};
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
	
	public long createLocation(String name, double latitude, double longitude, int range) {
		ContentValues values = new ContentValues();				
		values.put(DBHelper.LOCATIONS_COLUMN_NAME, name);	
		values.put(DBHelper.LOCATIONS_COLUMN_LATITUDE, latitude);
		values.put(DBHelper.LOCATIONS_COLUMN_LONGITUDE, longitude);
		values.put(DBHelper.LOCATIONS_COLUMN_RANGE, range);
				
		return database.insert(DBHelper.TABLE_LOCATIONS, null, values);
		
	}
	
	public long createTaskLocation(long task_id, long location_id) {
		ContentValues values = new ContentValues();				
		values.put(DBHelper.TASKS_LOCATIONS_COLUMN_TASK_ID, task_id);	
		values.put(DBHelper.TASKS_LOCATIONS_COLUMN_LOCATION_ID, location_id);		
				
		return database.insert(DBHelper.TABLE_TASKS_LOCATIONS, null, values);		
	}
	
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
			boolean isActive = false;
			if (active == 1) {
				isActive = true;
			}
			int color = cursor.getInt(5);
			String notes = cursor.getString(6);			
			allTasks.add(new Task(id, name, deadline, deadline_alert, isActive, color, notes));
			cursor.moveToNext();
		}
		return allTasks;
	}
	
	public Task getTask(long task_id) {
		Cursor cursor = database.query(DBHelper.TABLE_TASKS, allTasksColumns, DBHelper.TASKS_COLUMN_ID + " = " + task_id, null, null, null, null);		
		if (cursor.getCount() > 0){
			long id = cursor.getLong(0);			
			String name= cursor.getString(1);
			long deadline = cursor.getLong(2);
			long deadline_alert = cursor.getLong(3);
			int active = cursor.getInt(4);
			boolean isActive = false;
			if (active == 1) {
				isActive = true;
			}
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
			allLocations.add(new MyLocation(id, name, latitude, longitude, range));
			cursor.moveToNext();
		}
		return allLocations;
	}
	
	public MyLocation getLocation(long location_id) {
		Cursor cursor = database.query(DBHelper.TABLE_LOCATIONS, allLocationsColumns, DBHelper.LOCATIONS_COLUMN_ID + " = " + location_id, null, null, null, null);		
		if (cursor.getCount() > 0){
			long id = cursor.getLong(0);			
			String name= cursor.getString(1);
			double latitude = cursor.getDouble(2);
			double longitude = cursor.getDouble(3);
			int range = cursor.getInt(4);		
			return new MyLocation(id, name, latitude, longitude, range);		
		}
		else {
			return null;
		}
	}
	
	public List<MyLocation> getLocationsToTask(long task_id) {
		Cursor cursor = database.query(DBHelper.TABLE_TASKS_LOCATIONS, allTasksLocationsColumns, DBHelper.TASKS_LOCATIONS_COLUMN_TASK_ID + " = "+ task_id, null, null, null, null);		
		List<MyLocation> locations = new ArrayList<MyLocation>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			long id = cursor.getLong(0);			
			String name= cursor.getString(1);
			double latitude = cursor.getDouble(2);
			double longitude = cursor.getDouble(3);
			int range = cursor.getInt(4);		
			locations.add(new MyLocation(id, name, latitude, longitude, range));
			cursor.moveToNext();
		}
		return locations;
	}
	
	public void deleteTask(long id){
	    System.out.println("Task deleted with id: " + id);
	    
	    // Delete the task from tasks table
	    database.delete(DBHelper.TABLE_TASKS, DBHelper.TASKS_COLUMN_ID
	        + " = " + id, null);
	    
	    // Delete all entries bound to task from tasks_locations table
	    database.delete(DBHelper.TABLE_TASKS_LOCATIONS, DBHelper.TASKS_LOCATIONS_COLUMN_TASK_ID
		        + " = " + id, null);
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
