package de.kamson.localdo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kamson.data.DBHandler;
import de.kamson.data.MyLocation;
import de.kamson.data.Task;
import de.kamson.data.TaskUtils;
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

public class AddNewTaskActivity extends Activity {

	static Context mContext;
	Intent intent;
	static final int ADDLOCATION = 200;
	DBHandler dbHandler;
	/*
	 * UI Variables
	 */
	static EditText et_taskName;
	static EditText et_deadlineDate;
	static EditText et_deadlineTime;
	static LinearLayout ll_locations;
	static EditText et_location;
	static EditText et_color;
	static CheckBox cb_alertTime;
	static Spinner spinner_alertTime;
	static CheckBox cb_alertLocation;
	static Spinner spinner_alertLocation;
	static EditText et_notes;
	
	static final int REMOVE_TASK = 1;
	static final int SAVE_TASK = 2;
	static final int ABORT = 3;
	static Task task;
	static List<MyLocation> task_locations;
	List<MyLocation> all_locations;
	GlobalState gs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		intent = getIntent();
		// Clean start
		//task = null;
		
		setContentView(R.layout.activity_add_new_task);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Load task and bound locations from database
		loadDataFromDB();
		
		// UI
		et_taskName = (EditText)findViewById(R.id.newTask_task_name);		
		et_deadlineDate = (EditText)findViewById(R.id.newTask_deadlineDate); //onClickListener ist in xml
		et_deadlineTime = (EditText)findViewById(R.id.newTask_deadlineTime); //onClickListener ist in xml
		ll_locations = (LinearLayout)findViewById(R.id.locations_linearlayout);
		et_location = (EditText)findViewById(R.id.newTask_location);
		et_color = (EditText)findViewById(R.id.newTask_color); //onClickListener ist in xml
		cb_alertTime = (CheckBox)findViewById(R.id.newTask_checkbox_alertTime);
		spinner_alertTime = (Spinner)findViewById(R.id.newTask_spinner_alertTime);
		cb_alertLocation = (CheckBox)findViewById(R.id.newTask_checkbox_alertLocation);
		spinner_alertLocation = (Spinner)findViewById(R.id.newTask_spinner_alertLocation);
		final EditText et_location = (EditText)findViewById(R.id.newTask_location); //onClickListener ist in xml
		
		
		
		
		et_notes = (EditText)findViewById(R.id.newTask_notes);
		
		// Fill in task data into fields		
		if (!(task.id == -1)){			
			
			// Set task title
			et_taskName.setText(task.name);
			
			// Set Deadline
			long deadline = task.deadline;			
			if(!(deadline == -1)) {
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
			// Check number of locations and display them
			// What if task_locations.size() == 0? -> null?
			int locationsCount = task_locations.size();
			// No location bound to the task
			if (locationsCount == 0) {
				// do nothing
			}
			// Exactly one location bound
			if (locationsCount >= 1) {
				// Display name of first location in list since we only have one item
				et_location.setText(task_locations.get(0).name);				
				// More than one location found
				if (locationsCount > 1) {
					// Create an array of Edittexts for the variable number of Edittexts to use
					// Size of the array is the number of locations without the first one
					EditText[] et_extra_locations = new EditText[locationsCount-1];
					// Add one editview for each more location
					for (int i = 1; i<locationsCount; i++) {
						// Instantiate new EditText
						et_extra_locations[i-1] = new EditText(mContext);
						// Fill in the name of the location starting at number 2
						et_extra_locations[i-1].setText(task_locations.get(i).name);
						// Add listener
						et_extra_locations[i-1].setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								showLocationDialog(v);
							}
						});
						// Add Editview to the ViewGroup holding all location names
						ll_locations.addView(et_extra_locations[i-1]);
					}
				}
				// Add another "Add location field"
				EditText et_location_extra = new EditText(mContext);
				et_location_extra.setHint(R.id.newTask_location);
				ll_locations.addView(et_location_extra);
			}
			
			// set Color
			int color = task.color;
			et_color.setText(TaskUtils.colorStrings.get(color));
		}
		
		
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
		if (intent.getExtras() == null) {
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
			Toast.makeText(getApplicationContext(), "Save To-Do", Toast.LENGTH_SHORT).show();
			openMain(SAVE_TASK);
			return true;
		case R.id.action_cancel:
			Toast.makeText(getApplicationContext(), "Abort", Toast.LENGTH_SHORT).show();
			openMain(ABORT);
			return true;
			// Not available
//		case R.id.action_edit:			
//			Toast.makeText(getApplicationContext(), "Edit To-Do", Toast.LENGTH_SHORT).show();
//			openMain(-1);
//			return true;
		case R.id.action_discard:
			Toast.makeText(getApplicationContext(), "Delete To-Do", Toast.LENGTH_SHORT).show();
			openMain(REMOVE_TASK);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
		
	public void openMain(int mode) {
		Intent intent = new Intent();
		switch (mode) {
			case REMOVE_TASK:
				// Sending back ID of deleted task
				intent.putExtra(TaskUtils.TASK_ID, task.id);
				intent.putExtra(TaskUtils.TASK_ISACTIVE, task.isActive);
				// Delete task from database here
				dbHandler.deleteTask(task.id);
				setResult(RESULT_OK, intent);				
				break;
			case SAVE_TASK:				
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
				}
				// Task ID existed so we can update the database
				else {
					id = task.id;
					dbHandler.updateTasks(id, name, deadline, deadline_alert, active, color, notes);
				}
				// Sending back ID of new or updated task
				intent.putExtra(TaskUtils.TASK_ID, id);
				intent.putExtra(TaskUtils.TASK_NAME, task.name);
				setResult(RESULT_OK, intent);
				break;
			case ABORT:
				// No action needed
				break;
		}
		
		finish();
	}
	
	public void openSetAlert() {
		Intent intent = new Intent(getApplicationContext(), SetAlertActivity.class);
		startActivity(intent);
	}
	
	public  void openSetLocation(View v) {
		Intent intent = new Intent(mContext, AddLocationActivity.class);
		startActivityForResult(intent, ADDLOCATION);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ADDLOCATION) {
			if(resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(), "back", Toast.LENGTH_SHORT).show();
				String tmp = data.getExtras().get("loc_name").toString();				
				et_location.setText(tmp);
			}
		}
	}
	
	public void loadDataFromDB() {
		intent = getIntent();
		// Instantiate database access
		dbHandler = new DBHandler(mContext);
		dbHandler.open();
		// Check if a task was send from the MainActivity
		if (!(intent.getExtras() == null)) {			
			task = dbHandler.getTask(intent.getLongExtra(TaskUtils.TASK_ID, -1));
			task_locations = dbHandler.getLocationsToTask(task.id);
		}
		else {
			task = new Task();
		}
		// In any case we will need all locations to display as choice list
		all_locations = dbHandler.getAllLocations();
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
		Spinner colorSpinner = (Spinner)findViewById(R.id.newTask_color_spinner); 
		//Toast.makeText(getApplicationContext(), "spinner", Toast.LENGTH_SHORT).show();
		colorSpinner.performClick();
	}
	
	public static class LocationDialogFragment extends DialogFragment {		
		public Dialog onCreateDialog(Bundle savedInstanceState) { 
			String[] locationNames;
			if (task_locations.size() == 0)
				locationNames = new String[1];
			else
				locationNames = new String[task_locations.size()];
			locationNames[0] = "New Location";
			int i = 1;
			for (MyLocation location:task_locations) {
				locationNames[i] = location.name;
				i++;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.newTask_location_spinner_prompt)
				.setSingleChoiceItems(locationNames, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(which == 0) { //at the moment 0 is New location
							Intent intent = new Intent(mContext, AddLocationActivity.class);
							getActivity().startActivityForResult(intent, ADDLOCATION);
							dialog.dismiss();
						}
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
			builder.setTitle(R.string.setColor_spinner_prompt)
				.setSingleChoiceItems(R.array.setColor_array, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						task.color = TaskUtils.RED;
						ListView lv = ((AlertDialog)dialog).getListView();
						et_color.setText((String)lv.getItemAtPosition(which));
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

}



