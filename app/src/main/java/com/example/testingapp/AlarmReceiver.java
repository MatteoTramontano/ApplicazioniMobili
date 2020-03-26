package com.example.testingapp;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.testingapp.MainActivity;
import com.example.testingapp.R;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("TAG", "Alarm received");
        sendNotification(context, intent.getStringExtra("data"), intent.getStringExtra("category"), intent.getIntExtra("code",0));
    }

    public static void sendNotification(Context mcontext, String messageBody, String category, int requestCode) {
        //in modo che al click della notifica si apra edit task
        Intent intent = new Intent(mcontext, editTask.class);
        intent.putExtra("title", messageBody);
        intent.putExtra("cat", category);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //fa si che venga attivato l'intent sopra quando viene cliccato permettendoti di passare dalla scheramta all'appliazione
        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(mcontext.getString(R.string.default_notification_channel_id), "Rewards Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mcontext, mcontext.getString(R.string.default_notification_channel_id))
                .setContentTitle(messageBody)
                .setSmallIcon(R.drawable.ic_darth_vader)
                .setContentText(category)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        notificationManager.notify(requestCode, notificationBuilder.build());
    }
}