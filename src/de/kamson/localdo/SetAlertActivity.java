package de.kamson.localdo;

import java.util.Calendar;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;

public class SetAlertActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alert);
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.set_alert, menu);
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
			Toast.makeText(getApplicationContext(), "Save alert", Toast.LENGTH_LONG).show();
			openAddNew();
			return true;
		case R.id.action_cancel:
			Toast.makeText(getApplicationContext(), "Abort", Toast.LENGTH_LONG).show();
			openAddNew();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	
	public void openAddNew() {
		Intent intent = new Intent(getApplicationContext(), AddNewToDoActivity.class);
		startActivity(intent);
	}
	
	@SuppressLint("NewApi")
	public void createNotification(View v) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
		
		Notification not = new Notification.Builder(getApplicationContext())
		.setContentTitle("Return book")
		.setContentText("You are close to: Library")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent).build();
		NotificationManager notMnger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		not.flags |= Notification.FLAG_AUTO_CANCEL;
		notMnger.notify(0, not);
	}
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		public Dialog onCreateDialog(Bundle saveInstanceState) {
			final Calendar c = Calendar.getInstance();
			int h = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);
			
			return new TimePickerDialog(getActivity(), this, h, min, DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hour, int min) {
			
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
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			
		}
	}
	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

}
