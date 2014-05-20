package de.kamson.localdo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;

import de.kamson.localdo.GeofenceRemover;
import de.kamson.localdo.GeofenceRequester;
import de.kamson.localdo.GeofenceUtils.*;
import de.kamson.data.DBHandler;
import de.kamson.data.MyConstants;
import de.kamson.data.MyLocation;
import de.kamson.data.Task;
import de.kamson.data.MyConstants;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * The main displays all tasks to the user separated by active/finished status
 * 
 * @author all
 *
 */
public class MainActivity extends Activity {
	
	/*
	 * Geofence variables
	 */
	// Store the current request
    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;
    
    // Store a list of geofences to add
    List<Geofence> mCurrentGeofences;
    
    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;
    
    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    
    /*
     * Location variables
     */
    // Own Position is requested in GeoRequester
	public static Location mLocation;
	LocationClient mLocationClient;	
	
	/* 
	 * UI variables 
	 */
	// The lists
	ListView active_tasks_lv;
	ListView finished_tasks_lv;
	// The adapter to the list
	TaskAdapter active_adapter;
	TaskAdapter finished_adapter;
	// The titles to display how many tasks
	TextView active_listTitle;
	TextView finished_listTitle;	
	
	/*
	 * List variables
	 */	
	List<Task> tasks;	
	List<Task> active_tasks;
	List<Task> finished_tasks;
	List<MyLocation> locations;	
	// Every task gets exactly one distance assigned
	List<Integer> distancesToMe;
	Task chosenTask;
	
	/*
	 * Some constants that are only use in this activity
	 */
	static final int TASKS_LIST = 1;
	static final int ACTIVE_TASKS_LIST = 2;
	static final int FINISHED_TASKS_LIST = 3;
	static final int LOCATION_LIST = 4;
	
	// Database variables
	DBHandler dbHandler;
		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 		
		setContentView(R.layout.activity_main);		
		
		// Load tasks and locations from database
		loadDataFromDB();
		
		// Build a list of geofences build from locations that are bound to an active task
		createGeofencesList();
		
		// Not working properly right now
		setDeadlineAlarm();
		
		// Build the listviews for active and finished task lists
		updateListViews();
		
		// Set listener for both listsviews
		setListItemClickListener();		
		
		// Display number of tasks		
		updateListTitle();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			addNewTask();
			return true;
		/*
		case R.id.action_settings:
			return true;
		*/
		case R.id.action_manageLocations:
			openManageLocations();
			return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void onStart() {
		super.onStart();
		/*
         * Record the request as an ADD. If a connection error occurs, the app can automatically restart 
         * the add request if Google Play services can fix the error
         */
		mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
		
		 /*
         * Check for Google Play services. Do this after setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to know what type of request was in progress.
         */
		if (servicesConnected()) {
			// Start the request. Fail if there's already a request in progress
	        try {
	            // Try to add geofences
	        	mGeofenceRequester.addGeofences(mCurrentGeofences);
	        } catch (UnsupportedOperationException e) {
	            // Notify user that previous request hasn't finished.
	            Toast.makeText(this, R.string.add_geofences_already_requested_error, Toast.LENGTH_SHORT).show();
	        }
		}
	}
	
	public void onResume() {
		super.onResume();
		if (mGeofenceRequester != null) {
			mGeofenceRequester.getLocation();
		}		
	}
		
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //super.onActivityResult(requestCode, resultCode, intent);
		// Choose what to do based on the request code		
		switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                        // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                    mGeofenceRequester.getRequestPendingIntent());

                            // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }
                
            case MyConstants.REQUESTCODE_SETTASK: {
            	
            	switch(resultCode) {            	
            		case RESULT_OK:
            			
            			// Just reload from DB
            			loadDataFromDB();
            			updateListViews();
            			updateListTitle();
            			
            		break;            		
            	}
            }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(GeofenceUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }
	
	/**
	 * Load all needed data from DB, create two lists for active/finished
	 */
	private void loadDataFromDB() {
		
		// Get the handler for the database 
		dbHandler = new DBHandler(this);
		
		// Instantiate database
		dbHandler.open();
		
		//  Read all tasks from database 
		tasks = dbHandler.getAllTasks();			
		
		// No task found
		if (tasks == null) {
			tasks = new ArrayList<Task>();
		}
		
		// Fill the lists for active and passive tasks from all tasks list		
		// Reset the lists
		active_tasks = new ArrayList<Task>();
		finished_tasks = new ArrayList<Task>();
		// The field isActive decides where to put
		for (Task tmpTask: tasks) {			
			if(tmpTask.isActive)				   
			    active_tasks.add(tmpTask);
			else
				finished_tasks.add(tmpTask);
		}
		
		// Read all locations
		locations = dbHandler.getAllLocations();	
		
		// No locations found
		if (locations == null) {
			locations = new ArrayList<MyLocation>();
		}
		
		// Debugging
		List<String> taskloc = dbHandler.getAllTasksLocations();
		for(Task t:tasks) {
			Log.d("DB Tasks", ""+t.id+" "+t.name+" "+t.deadline+" "+t.deadline_alert+" "+t.isActive+" "+t.color+" "+t.notes);
		}
		for(MyLocation l:locations) {
			Log.d("DB Locations", ""+l.id+" "+l.name+" "+l.lat+" "+l.lng+" "+l.range+" "+l.isAnonymous);			
		}
		for(String s:taskloc) {
			Log.d("DB TtoL", s);
		}
			
		
	}
	
	/**
	 * Build geofences from locations bound to active tasks
	 */
	private void createGeofencesList() {
		
		// Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);
        
        // Instantiate the current List of geofences
		mCurrentGeofences = new  ArrayList<Geofence>();
		
		// Build Geofence-objects and add them to current list of geofences
		// for now expiration time is set to endless
		List<MyLocation> task_locations;
		for(Task task: active_tasks) {
			task_locations = dbHandler.getLocationsToTask(task.id);
			for(MyLocation location: task_locations) {
				// Radius of 0.0 not allowed so we have to sort out location with range 0
				if (location.range != 0) {
					mCurrentGeofences.add(new Geofence.Builder()
								.setRequestId(String.valueOf(task.name+" - "+location.name))
								.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
								.setCircularRegion(location.lat, location.lng, location.range)
								.setExpirationDuration(Geofence.NEVER_EXPIRE)
								.build()
								);
				}
			}
		}
		
	}
	
	// This method seems to be replacable by notifydatasetchange...
	private void updateListViews() {
		
		// Get the listviews
		active_tasks_lv = (ListView)findViewById(R.id.active_tasks_list);
		finished_tasks_lv = (ListView)findViewById(R.id.finished_tasks_list);
		
		// Instantiate the adapters for the listviews
		active_adapter = new TaskAdapter(this, R.layout.rowlayout_active, active_tasks, dbHandler, mGeofenceRequester);
		finished_adapter = new TaskAdapter(this, R.layout.rowlayout_finished, finished_tasks, dbHandler, mGeofenceRequester);
		
		// Assign the adapters to the corresponding listviews
		active_tasks_lv.setAdapter(active_adapter);
		finished_tasks_lv.setAdapter(finished_adapter);
	} 
	
	private void setListItemClickListener() {
		
		// For the active tasks list
		active_tasks_lv.setOnItemClickListener(new OnItemClickListener() {
			// Call activity to edit chosen active task
			@Override			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				chosenTask = (Task)active_tasks_lv.getItemAtPosition(position);				
				editTask();
			}
		});
		
		// For the finished tasks list
		finished_tasks_lv.setOnItemClickListener(new OnItemClickListener() {
			// Call activity to edit chosen finished task
			@Override			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				chosenTask = (Task)finished_tasks_lv.getItemAtPosition(position);
				editTask();
			}
		});
	}
	
	/**
	 * Shift a task from active to finished and vice versa
	 * @param pos	Position in the list
	 * @param list	Active or finished
	 */
	public void shiftItem(int pos, int list){
		
		// Remove task from active list and add to finished list
		Task t;
		if(list == ACTIVE_TASKS_LIST){
			
			// DB update because of change in field isActive
			t = (Task)active_tasks.get(pos);
			dbHandler.updateTasks(t.id, t.name, t.deadline, t.deadline_alert, 0, t.color, t.notes);
			
			// Visual update
			finished_tasks.add(active_tasks.remove(pos));
		}
		
		// Remove task from finished list and add to active list
		else{
			
			// DB update  because of change in field isActive
			t = (Task)finished_tasks.get(pos);
			dbHandler.updateTasks(t.id, t.name, t.deadline, t.deadline_alert, 1, t.color, t.notes);
			
			// Visual update
			active_tasks.add(finished_tasks.remove(pos));
		}
		
		// The number of tasks has changed in both lists
		updateListTitle();
		active_adapter.notifyDataSetChanged();
		finished_adapter.notifyDataSetChanged();
	}
	
	/**
	 * Refresh the titles for lists whenever a change occurs
	 */
	private void updateListTitle() {
		
		active_listTitle = (TextView)findViewById(R.id.active_tasks_title);
		// Could be outsourced to res/strings
		active_listTitle.setText(active_tasks.size() + " tasks to do");
		
		// If there is no task in the active list we add some space between for a better look 
		View v = (View)findViewById(R.id.invisible_space);
		if (active_tasks.size() == 0) {			
			v.setVisibility(View.VISIBLE);
		}
		else
			v.setVisibility(View.GONE);
		
		finished_listTitle = (TextView)findViewById(R.id.finished_tasks_title);
		finished_listTitle.setText(finished_tasks.size() + " tasks finished");
	}
	
	/**
	 * 	Jumps to SetTask to create a new task
	 */
	private void addNewTask() {
		// No data to send, to ensure that no other data exist when we jump back assign NULL here
		chosenTask = null;		
		
		// Create intent for the activity
		Intent intent = new Intent(getApplicationContext(), SetTaskActivity.class);
		
		// Send the operating mode
		intent.putExtra(MyConstants.OPERATING_MODE, MyConstants.MODE_ADD);
		
		startActivityForResult(intent, MyConstants.REQUESTCODE_SETTASK);
	}
	
	/**
	 * 	Jumps to Manage Locations
	 */
	private void openManageLocations() {
		
		// Create intent for the activity
		Intent intent = new Intent(getApplicationContext(), ManageLocationActivity.class);		
		startActivity(intent);
	}
	
	/**
	 * Jumps to SetTask to edit an existing task
	 */
	private void editTask() {
		
		// Create intent for new activity to edit task
		Intent intent = new Intent(getApplicationContext(), SetTaskActivity.class);
		
		// Send operating mode for SetTaskActivity 
		intent.putExtra(MyConstants.OPERATING_MODE, MyConstants.MODE_EDIT);
		
		// Only send the ID of the chosen task and reload the task from DB in the other activity
		intent.putExtra(MyConstants.TASK_ID, chosenTask.id);
		
		startActivityForResult(intent, MyConstants.REQUESTCODE_SETTASK);
	}

	 private boolean servicesConnected() {

	        // Check that Google Play services is available
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) {

	            // In debug mode, log the status
	            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));

	            // Continue
	            return true;

	        // Google Play services was not available for some reason
	        } else {

	            // Display an error dialog
//	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
//	            if (dialog != null) {
//	                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
//	                errorFragment.setDialog(dialog);
//	                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
//	            }
	        	Toast.makeText(getApplicationContext(), "Play services was not available", Toast.LENGTH_SHORT).show();
	            return false;
	        }
	    }
	 
	 /**
	  * Set the deadline alarm for every active task
	  */
	 private void setDeadlineAlarm() {		 
			for(Task task: active_tasks) {
					
				// If an alert time exist
				if (task.deadline_alert != 0) {
					
					Intent intentAlarm = new Intent(this, DeadlineAlarmReceiver.class);
					intentAlarm.putExtra(MyConstants.TASK_NAME, task.name);
		            
					AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		            
		            // Alarm Time is calculated 
		            long time = task.deadline-task.deadline_alert;
		           
		            // Set the alarm for particular time
		            alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		            
		            // Debbugging
		            long now = (new Date().getTime());
		            Log.d("ALARM SET NOW" , ""+ now);
		            Log.d("ALARM SET TIME" , ""+ time);
		            Log.d("ALARM SET DIFF" , ""+ (now-time));
				}
			}
				
	 }
	 
	 /**
	  * Clear all tables - mostly for debugging
	  * @param v 	Button to be clicked
	  */
	 public void clearTables(View v) {
		 //dbHandler.clearTasks();
		 //dbHandler.clearLocations();
		 dbHandler.clearTasks_Locations();
		 Toast.makeText(getApplicationContext(), "Table cleared", Toast.LENGTH_SHORT).show();
	 }
	 
	 /**
	  * Sort the task by the distance of the nearest bound location. 
	  * Tasks without location will be aligned at the bottom
	  * @param item 	Actionbar item to be clicked
	  */
	 public void sortActiveTasks(MenuItem item) {
		 
		 // First we calculate the distances new if location has changed
		 calculateDistances();		 
		 
		 List<Task> tmpList = new ArrayList<Task>();
		 int pos = 0;
		 int length = active_tasks.size();
		 for (int i = 0; i<length;i++) {
			 
			 // Get the task with nearest location
			 pos = getMinFromList(distancesToMe);
			 
			 // Rebuild the list - nearest on top
			 tmpList.add(i, active_tasks.remove(pos));
			 distancesToMe.remove(pos);
			 
		 }
		 
		 active_tasks = tmpList;
		 updateListViews();
		 updateListTitle();
		 active_adapter.notifyDataSetChanged();
	 }
	 
	 /**
	  * This calculates the distance from the nearest bound location every task and stores them in a list
	  */
	 private void calculateDistances() {
		 
		 distancesToMe = new ArrayList<Integer>();
		 mGeofenceRequester.getLocation();
		 Location location;
		 List<MyLocation> tmp_locs;
		 int i = 0;
		 int distance = 0;
		 
		 // If there are no tasks or our position is unknown
		 if ((active_tasks.size() != 0) && (MainActivity.mLocation != null)) {
			 
			 for(Task t:active_tasks) {
				 
				 tmp_locs = dbHandler.getLocationsToTask(t.id);
				 
				 // Index needed if there is more than one location
				 int j = 0;
				 
				 if (tmp_locs.size() != 0) {
					 for(MyLocation mLoc:tmp_locs) {
						 
						 // Create a location obejct
						 location = new Location("");
						 location.setLatitude(mLoc.lat);
						 location.setLongitude(mLoc.lng);
						
						 // Calculate distance to this location
						 distance = (int)location.distanceTo(MainActivity.mLocation);
						 
						 // For more than one location - compare the distances
						 if (j>0) {
							
							 // Replace distance if smaller
							if (distance < distancesToMe.get(i)) {
								distancesToMe.set(i, distance);
							}							
						 } 
						 // No value existed before - only one location
						 else {
							distancesToMe.add(distance);
						 }
						 j++;
					 }
				 }
				 
				 // If no location exist we assign a MaxInteger as distance to location
				 else {
					 distancesToMe.add(Integer.MAX_VALUE); 
				 }
				 i++;
			 }
		 }
	 }
	 
	 /**
	  * Get the index of the smallest item in a list
	  * @param list		The distances list
	  * @return			The position of the item
	  */
	 static int getMinFromList(List<Integer> list) {
		 int index = 0;
		 int small = (int)list.get(index); 
		 for (int i=1;i<list.size(); i++) {
			 if ((int)list.get(i)<small) {
				 index = i;
				 small = (int)list.get(i); 
			 }
		 }
		 return index;
	 }
	 
	 /*
		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			String msg = "Updated Location: " +
	                Double.toString(mLocation.getLatitude()) + "," +
	                Double.toString(mLocation.getLongitude());
	        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}
		

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnected(Bundle connectionHint) {
			// TODO Auto-generated method stub
			mLocation = mLocationClient.getLastLocation();
			// Get the PendingIntent for the request
	        mTransitionPendingIntent = getTransitionPendingIntent();
	        // Send a request to add the current geofences
	        mLocationClient.addGeofences(myGeofences, pendingIntent, this);

			if(mLocation == null) {
				mLocationClient.requestLocationUpdates(mLocationRequest, this);
			} else {
				Toast.makeText(this, "Connected! Location: " + mLocation.getLatitude() + ", " + mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			Toast.makeText(this, "Disconnected. Please re-connect.",
	                Toast.LENGTH_SHORT).show();

		}
	*/

	 
//	 public static class ErrorDialogFragment extends DialogFragment {
//
//	        // Global field to contain the error dialog
//	        private Dialog mDialog;
//
//	        /**
//	         * Default constructor. Sets the dialog field to null
//	         */
//	        public ErrorDialogFragment() {
//	            super();
//	            mDialog = null;
//	        }
//
//	        /**
//	         * Set the dialog to display
//	         *
//	         * @param dialog An error dialog
//	         */
//	        public void setDialog(Dialog dialog) {
//	            mDialog = dialog;
//	        }
//
//	        /*
//	         * This method must return a Dialog to the DialogFragment.
//	         */
//	        @Override
//	        public Dialog onCreateDialog(Bundle savedInstanceState) {
//	            return mDialog;
//	        }
//	    }

}
