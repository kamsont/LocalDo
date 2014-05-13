package de.kamson.localdo;

import java.util.Calendar;
import java.util.Date;

import de.kamson.data.MyLocation;
import de.kamson.data.ToDo;
import de.kamson.data.ToDoUtils;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;

public class AddNewToDoActivity extends Activity {

	static Context mContext;
	Intent intent;
	static final int ADDLOCATION = 100;
	
	/*
	 * UI Variables
	 */
	static EditText et_deadlineDate;
	static EditText et_deadlineTime;
	static EditText et_location;
	static CheckBox cb_alertTime;
	static Spinner spinner_alertTime;
	static CheckBox cb_alertLocation;
	static Spinner spinner_alertLocation;
	
	static final int REMOVE_TODO = 1;
	static final int SAVE_TODO = 2;
	ToDo toDo;
	GlobalState gs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		intent = getIntent();
		gs = (GlobalState) getApplication();
		setContentView(R.layout.activity_add_new_to_do);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Load data into local toDo-objct
		createToDoFromData();
		// UI
		EditText et_title = (EditText)findViewById(R.id.newToDo_task_title);		
		et_deadlineDate = (EditText)findViewById(R.id.newToDo_deadlineDate); //onClickListener ist in xml
		et_deadlineTime = (EditText)findViewById(R.id.newToDo_deadlineTime); //onClickListener ist in xml
		et_location = (EditText)findViewById(R.id.newToDo_location);
		final EditText et_color = (EditText)findViewById(R.id.newToDo_color); //onClickListener ist in xml
		cb_alertTime = (CheckBox)findViewById(R.id.newToDo_checkbox_alertTime);
		spinner_alertTime = (Spinner)findViewById(R.id.newToDo_spinner_alertTime);
		cb_alertLocation = (CheckBox)findViewById(R.id.newToDo_checkbox_alertLocation);
		spinner_alertLocation = (Spinner)findViewById(R.id.newToDo_spinner_alertLocation);
		final EditText et_location = (EditText)findViewById(R.id.newToDo_location); //onClickListener ist in xml
		
		Spinner colorSpinner = (Spinner)findViewById(R.id.newToDo_color_spinner);
		Spinner locationSpinner = (Spinner)findViewById(R.id.newToDo_location_spinner);
		
		/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.setColor_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
		// Apply the adapter to the spinner
		colorSpinner.setAdapter(adapter);*/
		
		colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override			
			public void onItemSelected(AdapterView<?> l, View v, int position, long id) {
				Toast.makeText(getApplicationContext(), "spinner", Toast.LENGTH_SHORT).show();
				et_color.setText(l.getItemAtPosition(position).toString());
				}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override			
			public void onItemSelected(AdapterView<?> l, View v, int position, long id) {
				Toast.makeText(getApplicationContext(), "spinner", Toast.LENGTH_SHORT).show();
				et_location.setText(l.getItemAtPosition(position).toString());
				}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
		
		EditText et_notes = (EditText)findViewById(R.id.newToDo_notes);
		
		// get Data from MainActivity		
		if (!(toDo == null)){			
			
			// set Task title
			et_title.setText(toDo.name);
			
			// set Deadline
			long deadline = toDo.deadline;			
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
				cb_alertTime.setVisibility(View.VISIBLE);// checkbox alert anzeigen
				// Spinner
				spinner_alertTime.setVisibility(View.VISIBLE);// spinner alert time anzeigen
			}
			// check number of locations and display them
			String[] names = toDo.getLocationNames();
			if (names == null) {
				// do nothing
			}
			else if (names.length == 1) {
				et_location.setText(names[0]);
			}
			else {
				// add more editviews for each location
			}
			
			// set Color
			int color = toDo.color;
			et_color.setText(ToDoUtils.colorStrings.get(color));
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
		getMenuInflater().inflate(R.menu.add_new_to_do, menu);
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
			openMain(SAVE_TODO);
			return true;
		case R.id.action_cancel:
			// Not available
			Toast.makeText(getApplicationContext(), "Abort", Toast.LENGTH_SHORT).show();
			openMain(-1);
			return true;
		case R.id.action_edit:
			// Not available
			Toast.makeText(getApplicationContext(), "Edit To-Do", Toast.LENGTH_SHORT).show();
			openMain(-1);
			return true;
		case R.id.action_discard:
			Toast.makeText(getApplicationContext(), "Delete To-Do", Toast.LENGTH_SHORT).show();
			openMain(REMOVE_TODO);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
		
	public void openMain(int mode) {
		Intent intent = new Intent();
		switch (mode) {
			case REMOVE_TODO:
				intent.putExtra(ToDoUtils.TODO_ID, toDo.id);
				intent.putExtra(ToDoUtils.TODO_ISACTIVE, toDo.isActive);
				gs.setChosenToDo(toDo);
				setResult(RESULT_FIRST_USER, intent);				
				break;
			case SAVE_TODO:
				intent.putExtra(ToDoUtils.TODO_ID, toDo.id);
				intent.putExtra(ToDoUtils.TODO_NAME, toDo.name);
				intent.putExtra(ToDoUtils.TODO_DEADLINE, toDo.deadline);
				intent.putExtra(ToDoUtils.TODO_TIMETODEADLINE, toDo.timeToDeadline);
				intent.putExtra(ToDoUtils.TODO_LOCATIONS, toDo.getLocationNames());
				//intent.putExtra(ToDoUtils.TODO_RANGE, chosenToDo.range);
				intent.putExtra(ToDoUtils.TODO_COLOR, toDo.color);
				gs.setChosenToDo(toDo);
				setResult(RESULT_OK, intent);
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
	
	public void createToDoFromData() {
//		if (!(intent.getExtras() == null)) {
//			long id = intent.getLongExtra(ToDoUtils.TODO_ID, -1);
//			String name = intent.getStringExtra(ToDoUtils.TODO_NAME);
//			long deadline = intent.getLongExtra(ToDoUtils.TODO_DEADLINE, -1);
//			long timeToDeadline = intent.getLongExtra(ToDoUtils.TODO_TIMETODEADLINE, -1);
//			String[] locations = intent.getStringArrayExtra(ToDoUtils.TODO_LOCATIONS);
//			boolean isActive = intent.getBooleanExtra(ToDoUtils.TODO_ISACTIVE, false);
//			int color = intent.getIntExtra(ToDoUtils.TODO_COLOR, -1);
//			
//		}
//		toDo = new ToDo(id, name, deadline, timeToDeadline, locations, isActive, color);
		
		toDo = gs.getChosenToDo();
	}
	
	public void showColorSpinnerDialog (View v) {
		Spinner colorSpinner = (Spinner)findViewById(R.id.newToDo_color_spinner); 
		//Toast.makeText(getApplicationContext(), "spinner", Toast.LENGTH_SHORT).show();
		colorSpinner.performClick();
	}
	
	public static class MyDialogFragment extends DialogFragment {		
		public Dialog onCreateDialog(Bundle savedInstanceState) {  
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.newToDo_location_spinner_prompt)
				.setSingleChoiceItems(R.array.newToDo_locations_array, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(which == 3) { //at the moment 3 is New location
							Intent intent = new Intent(mContext, AddLocationActivity.class);
							getActivity().startActivityForResult(intent, ADDLOCATION);
							dialog.dismiss();
						}
					}
				});
			return builder.create();
		}
	}
	
	public void showMyDialog(View v) {
		DialogFragment newFragment = new MyDialogFragment();
		newFragment.show(getFragmentManager(), "mydialog");
	}
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		public Dialog onCreateDialog(Bundle saveInstanceState) {
			final Calendar c = Calendar.getInstance();
			int h = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);
			
			return new TimePickerDialog(getActivity(), this, h, min, DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hour, int min) {
			
			et_deadlineTime.setText(hour + ":" + min);
			cb_alertTime.setVisibility(View.VISIBLE);// checkbox alert anzeigen
			spinner_alertTime.setVisibility(View.VISIBLE);// spinner alert time anzeigen
			
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

}



