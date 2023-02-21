package ca.app.assasins.taskappsassassinsandroid.common.helper;


import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ca.app.assasins.taskappsassassinsandroid.R;

public class TaskNotification {
    private static final String CHANNEL_ID = "5000";
    private final Application application;

    public TaskNotification(Application application) {
        this.application = application;
    }

    public NotificationCompat.Builder notificationBuilder(Class<?> tClass) {

        createNotificationChannel();
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(application, tClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.outline_task_alt_24)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder;

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = application.getString(R.string.app_name);
            String description = application.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(application.getApplicationContext(), NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
