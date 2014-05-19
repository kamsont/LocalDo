package de.kamson.localdo;

import de.kamson.data.MyConstants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class MyDialogFragment extends DialogFragment {
	 
	static MyDialogFragment newInstance(String message) {
	    MyDialogFragment f = new MyDialogFragment();	    
	    Bundle args = new Bundle();
	    args.putString(MyConstants.DIALOG_MESSAGE, message);
	    f.setArguments(args);

	    return f;
	}
	
	@Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) { 
	  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	  //builder.setTitle(getArguments().getString(MyConstants.DIALOG_MESSAGE));
	  builder.setMessage(getArguments().getString(MyConstants.DIALOG_MESSAGE));	 
	  Dialog dialog = builder.create();
	  return dialog;
	 }
}
