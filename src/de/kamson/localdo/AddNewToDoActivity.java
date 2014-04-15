package de.kamson.localdo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class AddNewToDoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_to_do);
		// Show the Up button in the action bar.
		setupActionBar();
		
		EditText et_alert = (EditText)findViewById(R.id.newToDo_alert);
		et_alert.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openSetAlert();
			}
		});
		
		EditText et_title = (EditText)findViewById(R.id.newToDo_task_title);
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

}
