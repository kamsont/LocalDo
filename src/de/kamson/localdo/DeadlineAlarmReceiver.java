package de.kamson.localdo;

import de.kamson.data.MyConstants;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class DeadlineAlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent resultIntent = new Intent(context, MainActivity.class);
		
		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    context,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		
		// Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Set the notification contents
        builder.setSmallIcon(R.drawable.ic_launcher)
               .setContentTitle(context.getResources().getString(R.string.deadline_alarm_title))
                      
               .setContentText("Deadline for"+intent.getStringExtra(MyConstants.TASK_NAME)+" is coming!")
               .setAutoCancel(true)
               .setSmallIcon(R.drawable.localdo_logo)
               .setContentIntent(resultPendingIntent);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
        Log.d("ALARM", "RECEIVED");
	}

}
