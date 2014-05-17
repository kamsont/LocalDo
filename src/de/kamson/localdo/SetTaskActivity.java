package de.kamson.localdo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.android.gms.drive.internal.GetMetadataRequest;

import de.kamson.data.DBHandler;
import de.kamson.data.MyLocation;
import de.kamson.data.Task;
import de.kamson.data.MyConstants;
import de.kamson.localdo.SetAlertActivity.DatePickerFragment;
import de.kamson.localdo.SetAlertActivity.TimePickerFragment;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;

public class SetTaskActivity extends Activity {

	static Context mContext;
	Intent intent;
	
	DBHandler dbHandler;
	/*
	 * UI Variables
	 */
	static EditText et_taskName;
	static EditText et_deadlineDate;
	static EditText et_deadlineTime;
	static LinearLayout ll_locations;	
	static EditText et_color;
	static CheckBox cb_alertTime;
	static Spinner spinner_alertTime;
	static CheckBox cb_alertLocation;
	static Spinner spinner_alertLocation;
	static EditText et_notes;
	//static EditText et_clickedLocation;
	
	int tag;
	static Task task;
	static MyLocation location;
	List<MyLocation> new_locations;
	static List<MyLocation> task_locations;
	static List<MyLocation> all_locations;
	
	private int operating_mode;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Toast.makeText(this, "SetTask onCreate called", Toast.LENGTH_SHORT).show();
		mContext = getApplicationContext();
		intent = getIntent();
		
		setContentView(R.layout.activity_set_task);
		
		// Load task and bound locations from database
		loadDataFromDB();
				
		// Show the Up button in the action bar.
		setupActionBar();
		
		// UI
		et_taskName = (EditText)findViewById(R.id.setTask_task_name);		
		et_deadlineDate = (EditText)findViewById(R.id.setTask_deadlineDate); //onClickListener ist in xml
		et_deadlineTime = (EditText)findViewById(R.id.setTask_deadlineTime); //onClickListener ist in xml
		ll_locations = (LinearLayout)findViewById(R.id.locations_linearlayout);
		//et_locations = new ArrayList<EditText>();
		//et_locations.add((EditText)findViewById(R.id.setTask_location));
		et_color = (EditText)findViewById(R.id.setTask_color); //onClickListener ist in xml
		cb_alertTime = (CheckBox)findViewById(R.id.setTask_checkbox_alertTime);
		spinner_alertTime = (Spinner)findViewById(R.id.setTask_spinner_alertTime);
		cb_alertLocation = (CheckBox)findViewById(R.id.setTask_checkbox_alertLocation);
		spinner_alertLocation = (Spinner)findViewById(R.id.setTask_spinner_alertLocation);
		//final EditText et_location = (EditText)findViewById(R.id.setTask_location); //onClickListener ist in xml
		et_notes = (EditText)findViewById(R.id.setTask_notes);
		
		fillInData();
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// Check on intent here because loadDateFromDB is not called yet 
		if (operating_mode == MyConstants.MODE_ADD) {
			getMenuInflater().inflate(R.menu.add_new_task, menu);
		}
		else {
			getMenuInflater().inflate(R.menu.edit_task, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//			
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_accept:
			Toast.makeText(getApplicationContext(), "Save Task", Toast.LENGTH_SHORT).show();
			backToMain(MyConstants.ACTION_ACCEPT);
			return true; 
		case R.id.action_cancel:
			Toast.makeText(getApplicationContext(), "Abort", Toast.LENGTH_SHORT).show();
			backToMain(MyConstants.ACTION_CANCEL);
			return true;
			// Not available
//		case R.id.action_edit:			
//			Toast.makeText(getApplicationContext(), "Edit Task", Toast.LENGTH_SHORT).show();
//			openMain(-1);
//			return true;
		case R.id.action_discard:
			Toast.makeText(getApplicationContext(), "Delete Task", Toast.LENGTH_SHORT).show();
			backToMain(MyConstants.ACTION_DISCARD);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
		
	public void backToMain(int action) {
		Intent intent = new Intent();
		switch (action) {
			case MyConstants.ACTION_DISCARD:
				// Sending back ID of deleted task
				intent.putExtra(MyConstants.TASK_ID, task.id);
				intent.putExtra(MyConstants.TASK_ISACTIVE, task.isActive);
				// Delete task from database here
				dbHandler.deleteTask(task.id);
				Toast.makeText(getApplicationContext(), "Task deleted", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK, intent);				
				break;
			case MyConstants.ACTION_ACCEPT:				
				long id = -1;
				// For now no correctness check of user input
				readUserInput();
				String name = task.name;
				long deadline = task.deadline;
				long deadline_alert = task.deadline_alert;
				int active = task.isActive ? 1 : 0;					
				int color = task.color;
				String notes = task.notes;
				// No task ID existed before means new task
				if (task.id == -1) {
					id = dbHandler.createTask(name, deadline, deadline_alert, active, color, notes);
					Toast.makeText(getApplicationContext(), "Task created", Toast.LENGTH_SHORT).show();
				}
				// Task ID existed so we can update the database
				else {
					id = task.id;
					dbHandler.updateTasks(id, name, deadline, deadline_alert, active, color, notes);
					Toast.makeText(getApplicationContext(), "Task updated", Toast.LENGTH_SHORT).show();
				}
				
				// Update task_location table  
				// First get rid the old entries 
				dbHandler.deleteTaskFromTaskLocations(id);
				
				// Store the changed locations list for this task
				for (MyLocation loc: task_locations) {
					dbHandler.createTaskLocation(task.id, loc.id);
					Toast.makeText(getApplicationContext(), "Task-Location created", Toast.LENGTH_SHORT).show();
				}
				
				// Sending back ID of new or updated task
				intent.putExtra(MyConstants.TASK_ID, id);
				intent.putExtra(MyConstants.TASK_NAME, task.name);
				setResult(RESULT_OK, intent);
				break;
			case MyConstants.ACTION_CANCEL:
				// No action needed
				break;
		}
		
		finish();
	}
	
	public void openSetAlert() {
		Intent intent = new Intent(getApplicationContext(), SetAlertActivity.class);
		startActivity(intent);
	}
	
	// Remember which field was clicked User clicks on it
	public  void openSetLocation(View v) {
		tag = (Integer)v.getTag();
//		String tmp = et_clickedLocation.getHint().toString();
		Toast.makeText(getApplicationContext(), tag+" clicked", Toast.LENGTH_SHORT).show();
		showLocationDialog(v);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MyConstants.REQUESTCODE_SETLOCATION) {
			if(resultCode == RESULT_OK) {				
				
				long id = data.getLongExtra(MyConstants.LOCATION_ID, -1);
				// Not needed
				//String tmp = data.getStringExtra(MyConstants.LOCATION_NAME);				
				
				updateLocationLists(id);
				
				buildLocationViews();
			}
		}
	}
	
	private void loadDataFromDB() {
		
		intent = getIntent();
		
		// Instantiate database access
		dbHandler = new DBHandler(mContext);
		dbHandler.open();
		
		// Set operating mode
		operating_mode = intent.getIntExtra(MyConstants.OPERATING_MODE, -1); 
		
		// Check if to edit or add a new task
		if (operating_mode == MyConstants.MODE_EDIT) {			
			
			// To edit the task take its given id and fetch it from database
			task = dbHandler.getTask(intent.getLongExtra(MyConstants.TASK_ID, -1));
			
			// Also get all bound location to the task
			task_locations = dbHandler.getLocationsToTask(task.id);
			
		}
		else {
			
			// To add a new task instantiate a new task-object with id=-1
			task = new Task();
			
		}
		
		// If no task bound found
		if (task_locations == null)
			task_locations = new ArrayList<MyLocation>();
		
		// Prepare to collect new locations
		new_locations = new ArrayList<MyLocation>();
		
		// In any case we will need all locations to display as choice list
		all_locations = dbHandler.getAllLocations();
		
		String tmp = "TaskID: "+task.id+" -> ";
		for(MyLocation loc:all_locations) {			
			tmp += loc.id+"-"+loc.name+"-"+loc.lat+"-"+loc.lng+"-"+loc.range+":+:"; 
		}
		et_notes = (EditText)findViewById(R.id.setTask_notes);
		et_notes.setText(tmp);
	}
	
	
	public void readUserInput() {
		task.name = et_taskName.getText().toString();
		//task.deadline already exist
		//task.deadline_alert already exist
		task.isActive = true;
		//task.color already exist
		task.notes = et_notes.getText().toString();
	}
	
	public void showColorSpinnerDialog (View v) {
		Spinner colorSpinner = (Spinner)findViewById(R.id.setTask_color_spinner); 
		//Toast.makeText(getApplicationContext(), "spinner", Toast.LENGTH_SHORT).show();
		colorSpinner.performClick();
	}
	
	public static class LocationDialogFragment extends DialogFragment {		
		public Dialog onCreateDialog(Bundle savedInstanceState) { 
			// Setup the choice list consisting of all locations
			String[] choices;
			
			// No location exist
			if (all_locations.size() == 0) {
				choices = new String[1];				
			}
			
			// Locations exist, set choice list size to all_locations+1 because of New location
			else {
				choices = new String[all_locations.size()+1];
				int i = 1;
				for (MyLocation location:all_locations) {
					choices[i] = location.name;
					i++;
				}
			}
			// In any case assign the possibility to add a new location as first choice
			choices[0] = "New Location";
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.setTask_location_spinner_prompt)
				.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//((SetTaskActivity)getActivity()).openSetAlert();
						Intent intent = new Intent(mContext, SetLocationActivity.class);
						if (which == 0) { // 0 = New location						
							intent.putExtra(MyConstants.OPERATING_MODE, MyConstants.MODE_ADD);
						}
						else {
							// The choice corresponds to the all locations list so get the location her
							// The parameter needs to be incremented because of New location on position 1 that does not exist in all locations
							location = all_locations.get(which-1);
							intent.putExtra(MyConstants.OPERATING_MODE, MyConstants.MODE_EDIT);
							intent.putExtra(MyConstants.LOCATION_ID, location.id);
						}
						getActivity().startActivityForResult(intent, MyConstants.REQUESTCODE_SETLOCATION);
						dialog.dismiss();
					}
				});
			return builder.create();
		}
	}
	
	public void showLocationDialog(View v) {
		DialogFragment newFragment = new LocationDialogFragment();
		newFragment.show(getFragmentManager(), "locationDialog");
	}
	
	public static class ColorDialogFragment extends DialogFragment {		
		public Dialog onCreateDialog(Bundle savedInstanceState) {  
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.setTask_colors_spinner_prompt)
				.setSingleChoiceItems(R.array.setTask_colors_array, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// Get the colorstring from dialog
						ListView lv = ((AlertDialog)dialog).getListView();
						et_color.setText((String)lv.getItemAtPosition(which));
						// Lookup which color value matches colorstring
						task.color = MyConstants.STRING_TO_COLOR.get(et_color.getText().toString());
						dialog.dismiss();
						
					}
				});
			return builder.create();
		}
	}
	
	public void showColorDialog(View v) {
		DialogFragment newFragment = new ColorDialogFragment();
		newFragment.show(getFragmentManager(), "colorDialog");
	}
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		public Dialog onCreateDialog(Bundle saveInstanceState) {
			final Calendar c = Calendar.getInstance();
			int h = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);
			
			return new TimePickerDialog(getActivity(), this, h, min, DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hour, int min) {
			task.deadline += ((hour*3600)+(min*60))*1000;
			et_deadlineTime.setText(hour + ":" + min);
			activateDeadlineAlert();
			
		}
	}
	
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timepicker");
	}
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		
		public Dialog onCreateDialog(Bundle saveInstanceState) {
			final Calendar c = Calendar.getInstance();
			int y = c.get(Calendar.YEAR);
			int m = c.get(Calendar.MONTH);
			int d = c.get(Calendar.DAY_OF_MONTH);
			
			return new DatePickerDialog(getActivity(), this, y, m, d);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day){
			task.deadline = new GregorianCalendar(year, month, day).getTimeInMillis();
			et_deadlineDate.setText(day + "." + (month+1) + "." + year);
			et_deadlineDate.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT, 1));
			et_deadlineTime.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT, 1));
			et_deadlineTime.setVisibility(View.VISIBLE);
			//et_deadlineTime.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
		}
	}
	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}
	
	private static void activateDeadlineAlert() {
		cb_alertTime.setVisibility(View.VISIBLE);// checkbox alert anzeigen
		spinner_alertTime.setVisibility(View.VISIBLE);// spinner alert time anzeigen
		spinner_alertTime.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(mContext, arg0.getItemAtPosition(arg2) + "selected", Toast.LENGTH_SHORT).show();
				// For now set fix to 1h
				task.deadline_alert = 3600000;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void buildLocationViews() {
		
		// Get rid of old views and rebuild
		ll_locations.removeAllViews();
		int locationsCount = 0;
		// Check number of locations and display them
		// What if task_locations.size() == 0? -> null?
		if (task_locations != null) {
			locationsCount = task_locations.size();
			// No location bound to the task			
				// Add one EditText for one location plus one last for "Add Location"
				for (int i = 0; i<locationsCount+1; i++) {
					
					// Instantiate new EditText
					EditText et_tmp = new EditText(mContext);
					
					// Set color
					et_tmp.setTextColor(0xff000000);
					
					// Fill in the name of the location 
					// Last element is filled with "Add location"
					if (i==locationsCount) {
						et_tmp.setHint(R.string.setTask_location);
					}
					else {	
						et_tmp.setText(task_locations.get(i).name);
					}
					
					// Tag the view to identify it afterwards
					et_tmp.setTag(i);
					// Deactivate keyboard
					et_tmp.setFocusableInTouchMode(false);
					
					// Add listener
					et_tmp.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							openSetLocation(v);
						}
					});
					
					// Add Editview to the ViewGroup holding bound location names						
					ll_locations.addView(et_tmp);
				}
			}				
	}
	
	private void fillInData() {
		// Fill in task data into fields		
				if (task.id != -1){			
					
					// Set task title
					et_taskName.setText(task.name);
					
					// Set Deadline
					long deadline = task.deadline;			
					if(deadline != -1) {
						// Date
						et_deadlineDate.setText(DateFormat.getDateFormat(getApplicationContext()).format(new Date(deadline)));				
						// Time
						et_deadlineTime.setText(DateFormat.getTimeFormat(getApplicationContext()).format(new Date(deadline)));
						// reset layout for deadline
						et_deadlineDate.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT, 1));
						et_deadlineTime.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT, 1));
						et_deadlineTime.setVisibility(View.VISIBLE);
						// Checkbox set checked
						cb_alertTime.setChecked(true);
						activateDeadlineAlert();
					}					
					
					// set Color					
					et_color.setText(MyConstants.COLOR_TO_STRING.get(task.color));
				}
				buildLocationViews();
	}
	
	private void updateLocationLists(long id) {
		// Update task_location list
		location = dbHandler.getLocation(id);
		if (task_locations != null) {
			Log.d("updateLocationList", "Tag:"+tag+"-Size:"+task_locations.size());
			Toast.makeText(getApplicationContext(), "Tag:"+tag+"-Size:"+task_locations.size(), Toast.LENGTH_SHORT).show();
			// Tag indicating last element means add
			if (tag == task_locations.size()) {				
				task_locations.add(location);					
				}
			
			// Tag indicating not last element means replace
			else {
				task_locations.set(tag, location);
			}			
			
		}
		// In case there was a deletion in SetLocations
		all_locations = dbHandler.getAllLocations();
	}
	/*
	public void onStart() {
		super.onStart();
		Toast.makeText(this, "SetTask onStart called", Toast.LENGTH_SHORT).show();
		
	}
	
	public void onResume() {
		super.onResume();
		Toast.makeText(this, "SetTask onResume called", Toast.LENGTH_SHORT).show();
	}
	
	public void onPause() {
		super.onPause();
		Toast.makeText(this, "SetTask onPause called", Toast.LENGTH_SHORT).show();
	}
	
	public void onStop() {		
        Toast.makeText(this, "SetTask onStop called", Toast.LENGTH_SHORT).show();
		super.onStop();
	}
	*/

}



