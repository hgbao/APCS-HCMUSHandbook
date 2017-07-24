package com.hgbao.hcmushandbook;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.hgbao.provider.DataProvider;

public class GCMIntentService extends GCMBaseIntentService{
    @Override
    protected void onMessage(Context context, Intent intent) {
        if (intent != null) {
            String message = intent.getStringExtra("message");
            if (message != null && !message.isEmpty())
                generateNotification(context, message);
        }
    }

    @Override
    protected void onError(Context context, String errorId) {
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        //ServerTask.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            GCMRegistrar.setRegisteredOnServer(context, false);
            //ServerTask.post_unregister(context, registrationId);
        }
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void generateNotification(Context context, String message) {
        //Create title and content
        String title = context.getString(R.string.message_new);
        String content = "";
        for (String token: message.split(";")){
            if (token.equalsIgnoreCase(DataProvider.GCM_ACTIVITY))
                content = content + context.getString(R.string.message_new_activity) + " ";
            if (token.equalsIgnoreCase(DataProvider.GCM_ENTERTAINMENT))
                content = content + context.getString(R.string.message_new_entertainment) + " ";
            if (token.equalsIgnoreCase(DataProvider.GCM_ENTERTAINMENTPHOTO))
                content = content + context.getString(R.string.message_new_photo) + " ";
            if (token.equalsIgnoreCase(DataProvider.GCM_SCHOLARSHIP))
                content = content + context.getString(R.string.message_new_scholarship) + " ";
        }
        //Notification
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent iOpen = new Intent(context, LoadingActivity.class);
        PendingIntent iPending = PendingIntent.getActivity(context, 0, iOpen, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_app)
                .setAutoCancel(true)
                .setContentIntent(iPending)
                .setSound(alarmSound);
        Notification notification = builder.build();
        manager.notify(0, notification);
    }
}