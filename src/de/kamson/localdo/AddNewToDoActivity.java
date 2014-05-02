package de.kamson.localdo;

import java.util.Calendar;

import de.kamson.data.ToDo;
import de.kamson.localdo.SetAlertActivity.DatePickerFragment;
import de.kamson.localdo.SetAlertActivity.TimePickerFragment;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
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

	static EditText et_deadlineDate;
	static EditText et_deadlineTime;
	static CheckBox cb_alertTime;
	static Spinner spinner_alertTime;
	static CheckBox cb_alertLocation;
	static Spinner spinner_alertLocation;
	static EditText et_location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_to_do);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		EditText et_title = (EditText)findViewById(R.id.newToDo_task_title);
		
		et_deadlineDate = (EditText)findViewById(R.id.newToDo_deadlineDate); //onClickListener ist in xml
		et_deadlineTime = (EditText)findViewById(R.id.newToDo_deadlineTime); //onClickListener ist in xml
		final EditText et_color = (EditText)findViewById(R.id.newToDo_color); //onClickListener ist in xml
		cb_alertTime = (CheckBox)findViewById(R.id.newToDo_checkbox_alertTime);
		spinner_alertTime = (Spinner)findViewById(R.id.newToDo_spinner_alertTime);
		cb_alertLocation = (CheckBox)findViewById(R.id.newToDo_checkbox_alertLocation);
		spinner_alertLocation = (Spinner)findViewById(R.id.newToDo_spinner_alertLocation);
		et_location = (EditText)findViewById(R.id.newToDo_location); //onClickListener ist in xml
		
		Spinner colorSpinner = (Spinner)findViewById(R.id.newToDo_color_spinner); 
		
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
		
		
		
		
		
		
		
		EditText et_notes = (EditText)findViewById(R.id.newToDo_notes);
		Intent intent = getIntent();
		if (!(intent.getExtras() == null)){
			et_notes.setText((intent.getExtras()).toString());
			et_title.setText(intent.getStringExtra(MainActivity.TODO_NAME));
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
			Toast.makeText(getApplicationContext(), "Save To-Do", Toast.LENGTH_LONG).show();
			openMain();
			return true;
		case R.id.action_cancel:
			Toast.makeText(getApplicationContext(), "Abort", Toast.LENGTH_LONG).show();
			openMain();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
		
	public void openMain() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
	}
	
	public void openSetAlert() {
		Intent intent = new Intent(getApplicationContext(), SetAlertActivity.class);
		startActivity(intent);
	}
	
	public void openSetLocation(View view) {
		Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
		startActivity(intent);
	}
	
	public void showColorSpinnerDialog (View v) {
		Spinner colorSpinner = (Spinner)findViewById(R.id.newToDo_color_spinner); 
		//Toast.makeText(getApplicationContext(), "spinner", Toast.LENGTH_SHORT).show();
		colorSpinner.performClick();
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
			et_deadlineDate.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT,1));
			et_deadlineTime.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT, 1));
			//et_deadlineTime.setVisibility(View.VISIBLE);
			//et_deadlineTime.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
		}
	}
	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

}



