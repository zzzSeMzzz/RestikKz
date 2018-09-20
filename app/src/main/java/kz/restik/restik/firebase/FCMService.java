package kz.restik.restik.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kz.restik.restik.MainActivity;
import kz.restik.restik.R;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    public static final String DEF_NOTIF_ID = "1";
    public static final String DEF_NOTIF_CHANNEL = "News";

    public FCMService() {
        //Log.d(TAG, "FCMService: subscribe topic news");
        //FirebaseMessaging.getInstance().subscribeToTopic("news");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "registration token: "+FirebaseInstanceId.getInstance().getToken());
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "to: " + remoteMessage.getTo());
        Log.d(TAG, "message type: " + remoteMessage.getMessageType());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " +
                    remoteMessage.getNotification().getBody());
            String body = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            showNotify(DEF_NOTIF_ID, DEF_NOTIF_CHANNEL, title, body);
        }

    }

    private void showNotify(String nId, String channel, String title, String content){

        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc =
                    new NotificationChannel(channel, channel,
                            NotificationManager.IMPORTANCE_HIGH);
            nc.enableLights(true);
            nc.enableVibration(true);
            //nc.setLightColor(led_color);
            mNotificationManager.createNotificationChannel(nc);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, channel);
        mBuilder.setSmallIcon(R.drawable.ic_main_notif);
        /*if(largeIcon != -1) {
            Bitmap largeIconB =
                    BitmapFactory.decodeResource(context.getResources(), largeIcon);
            mBuilder.setLargeIcon(largeIconB);
        }*/
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        //mBuilder.setColor(data.getIntExtra(AlarmHelper.COLOR, -1));
        mBuilder.setVibrate(new long[] {1000, 1000, 1000});
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);
        addOnNotifClick(this, mBuilder);//клик на само уведомление
        /*addActionService(context, mBuilder, "Отложить на 1 день",true,
                Integer.valueOf(nId), historyId);
        setDeleteIntent(context, mBuilder, Integer.valueOf(nId));*/
        Notification notification = mBuilder.build();

        notification.flags = Notification.FLAG_SHOW_LIGHTS|Notification.FLAG_AUTO_CANCEL;
        notification.ledOffMS = 1000;
        mBuilder.setAutoCancel(true);

        mNotificationManager.notify(Integer.valueOf(nId), notification);
    }

    private void addOnNotifClick(Context context, NotificationCompat.Builder mBuilder){
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
    }

    /*@Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        Log.d(TAG, "onSendError: "+s);
        Log.d(TAG, "onSendError: "+e.getMessage());
    }*/


}
