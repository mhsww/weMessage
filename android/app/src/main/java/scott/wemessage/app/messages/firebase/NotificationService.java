package scott.wemessage.app.messages.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

import scott.wemessage.R;
import scott.wemessage.app.messages.MessageDatabase;
import scott.wemessage.app.messages.objects.Contact;
import scott.wemessage.app.messages.objects.Handle;
import scott.wemessage.app.messages.objects.chats.Chat;
import scott.wemessage.app.messages.objects.chats.GroupChat;
import scott.wemessage.app.security.CryptoType;
import scott.wemessage.app.security.DecryptionTask;
import scott.wemessage.app.security.KeyTextPair;
import scott.wemessage.app.ui.activities.LaunchActivity;
import scott.wemessage.app.weMessage;
import scott.wemessage.commons.json.message.JSONNotification;
import scott.wemessage.commons.utils.StringUtils;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //TODO: trim message, check if notification email matches, jsonnotification

        initChannel();
        showNotification(remoteMessage);
    }

    private void initChannel(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(weMessage.NOTIFICATION_CHANNEL_NAME) == null) {
            NotificationChannel channel = new NotificationChannel(weMessage.NOTIFICATION_CHANNEL_NAME, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT).build();

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long[]{1000, 1000});
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(RemoteMessage remoteMessage){
        if (weMessage.get().isSignedIn() && weMessage.get().performNotification(remoteMessage.getData().get("chatId"))) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            JSONNotification jsonNotification = new JSONNotification(
                    null,
                    remoteMessage.getData().get("encryptedText"),
                    remoteMessage.getData().get("key"),
                    remoteMessage.getData().get("handleId"),
                    remoteMessage.getData().get("chatId"),
                    remoteMessage.getData().get("chatName")
            );

            MessageDatabase database = weMessage.get().getMessageDatabase();
            Chat chat = database.getChatByMacGuid(jsonNotification.getChatId());
            Handle handle = database.getHandleByHandleID(jsonNotification.getHandleId());

            if (chat != null && chat instanceof GroupChat){
                if (((GroupChat) chat).isDoNotDisturb()) return;
            }

            if (handle != null){
                Contact c = weMessage.get().getMessageDatabase().getContactByHandle(handle);
                if (c.isDoNotDisturb() || c.isBlocked()) return;
            }

            DecryptionTask decryptionTask = new DecryptionTask(new KeyTextPair(jsonNotification.getEncryptedText(), jsonNotification.getKey()), CryptoType.AES);
            decryptionTask.runDecryptTask();

            String displayName = null;
            String message = "";
            Bitmap largeIcon = null;

            if (!StringUtils.isEmpty(jsonNotification.getChatId())) {
                if (chat != null && chat instanceof GroupChat) {
                    displayName = ((GroupChat) chat).getUIDisplayName(false);
                }
            } else if (!StringUtils.isEmpty(jsonNotification.getChatName())) {
                displayName = jsonNotification.getChatName();
            }

            if (!StringUtils.isEmpty(displayName)) {
                if (handle != null) {
                    message = database.getContactByHandle(handle).getUIDisplayName() + ": ";
                } else {
                    message = jsonNotification.getHandleId() + ": ";
                }
            } else {
                if (handle != null) {
                    displayName = database.getContactByHandle(handle).getUIDisplayName();
                } else {
                    displayName = jsonNotification.getHandleId();
                }
            }

            if (chat != null && chat instanceof GroupChat) {
                if (chat.getChatPictureFileLocation() != null && !StringUtils.isEmpty(chat.getChatPictureFileLocation().getFileLocation())) {
                    largeIcon = BitmapFactory.decodeFile(chat.getChatPictureFileLocation().getFileLocation());
                }
            } else {
                if (handle != null) {
                    Contact c = database.getContactByHandle(handle);
                    if (c.getContactPictureFileLocation() != null && !StringUtils.isEmpty(c.getContactPictureFileLocation().getFileLocation())) {
                        largeIcon = BitmapFactory.decodeFile(c.getContactPictureFileLocation().getFileLocation());
                    }
                }
            }
            message += decryptionTask.getDecryptedText();

            Intent intent = new Intent(this, LaunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            if (chat != null){
                intent.putExtra(weMessage.BUNDLE_LAUNCHER_GO_TO_CONVERSATION_UUID, chat.getUuid().toString());
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                builder = new NotificationCompat.Builder(this, weMessage.NOTIFICATION_CHANNEL_NAME);
            } else {
                builder = new NotificationCompat.Builder(this);
                builder.setVibrate(new long[]{1000, 1000})
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }

            builder.setSmallIcon(R.drawable.ic_app_notification_white_small)
                    .setContentTitle(displayName)
                    .setContentText(StringUtils.trimORC(message))
                    .setContentIntent(pendingIntent)
                    .setWhen(remoteMessage.getSentTime());

            if (largeIcon != null) {
                builder.setLargeIcon(largeIcon);
            }

            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            String tag = weMessage.NOTIFICATION_TAG;

            if (chat != null){
                tag += chat.getUuid().toString();
            }

            notificationManager.notify(tag, id, notification);
        }
    }
}