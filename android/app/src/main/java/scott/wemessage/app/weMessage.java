package scott.wemessage.app;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import scott.wemessage.app.messages.MessageDatabase;
import scott.wemessage.app.messages.MessageManager;
import scott.wemessage.app.messages.firebase.NotificationCallbacks;
import scott.wemessage.app.messages.objects.Account;
import scott.wemessage.commons.Constants;

public final class weMessage extends Application implements Constants {

    public static final int DATABASE_VERSION = 1;
    public static final int CONNECTION_TIMEOUT_WAIT = 15;

    public static final String DATABASE_NAME = "weMessage.db";
    public static final String APP_IDENTIFIER = "scott.wemessage.app";
    public static final String IDENTIFIER_PREFIX = "scott.wemessage.app.";
    public static final String ATTACHMENT_FOLDER_NAME = "attachments";
    public static final String NOTIFICATION_CHANNEL_NAME = "weMessage-Notifications";
    public static final String NOTIFICATION_TAG = "weMessage-Notification-";

    public static final int REQUEST_PERMISSION_READ_STORAGE = 5000;
    public static final int REQUEST_PERMISSION_CAMERA = 5002;
    public static final int REQUEST_PERMISSION_RECORD_AUDIO = 5003;

    public static final int REQUEST_CODE_CAMERA = 6000;

    public static final String BUNDLE_HOST = IDENTIFIER_PREFIX + "bundleHost";
    public static final String BUNDLE_EMAIL = IDENTIFIER_PREFIX + "bundleEmail";
    public static final String BUNDLE_PASSWORD = IDENTIFIER_PREFIX + "bundlePassword";
    public static final String BUNDLE_ALERT_TITLE = IDENTIFIER_PREFIX + "bundleAlertTitle";
    public static final String BUNDLE_FAST_CONNECT = IDENTIFIER_PREFIX + "bundleFastConnect";
    public static final String BUNDLE_ALERT_MESSAGE = IDENTIFIER_PREFIX + "bundleAlertMessage";
    public static final String BUNDLE_DIALOG_ANIMATION = IDENTIFIER_PREFIX + "bundleDialogAnimation";
    public static final String BUNDLE_IS_LAUNCHER_STILL_CONNECTING = IDENTIFIER_PREFIX + "bundleIsLauncherStillConnecting";
    public static final String BUNDLE_DISCONNECT_REASON_ALTERNATE_MESSAGE = IDENTIFIER_PREFIX + "bundleDisconnectReasonAlternateMessage";
    public static final String BUNDLE_ACTION_PERFORM_ALTERNATE_ERROR_MESSAGE = IDENTIFIER_PREFIX + "bundleActionPerformAlternateErrorMessage";
    public static final String BUNDLE_LAUNCHER_DO_NOT_TRY_RECONNECT = IDENTIFIER_PREFIX + "bundleLauncherDoNotTryReconnect";
    public static final String BUNDLE_LAUNCHER_GO_TO_CONVERSATION_UUID = IDENTIFIER_PREFIX + "bundleLauncherGoToConversationUuid";

    public static final String BUNDLE_RETURN_POINT = IDENTIFIER_PREFIX + "bundleReturnPoint";
    public static final String BUNDLE_CONVERSATION_CHAT = IDENTIFIER_PREFIX + "bundleConversationChat";
    public static final String BUNDLE_CONVERSATION_GO_BACK_REASON = IDENTIFIER_PREFIX + "bundleConversationGoBackReason";
    public static final String BUNDLE_FULL_SCREEN_IMAGE_URI = IDENTIFIER_PREFIX + "bundleFullScreenImageUri";
    public static final String BUNDLE_FULL_SCREEN_VIDEO_URI = IDENTIFIER_PREFIX + "bundleFullScreenVideoUri";
    public static final String BUNDLE_SELECTED_GALLERY_STORE = IDENTIFIER_PREFIX + "bundleSelectedGalleryStore";
    public static final String BUNDLE_GALLERY_FRAGMENT_OPEN = IDENTIFIER_PREFIX + "bundleGalleryFragmentOpen";
    public static final String BUNDLE_CAMERA_ATTACHMENT_FILE = IDENTIFIER_PREFIX + "bundleCameraAttachmentFile";
    public static final String BUNDLE_VOICE_MESSAGE_INPUT_FILE = IDENTIFIER_PREFIX + "bundleVoiceMessageInputFile";
    public static final String BUNDLE_CREATE_CHAT_CONTACT_UUIDS = IDENTIFIER_PREFIX + "bundleCreateChatContactUuids";
    public static final String BUNDLE_CREATE_CHAT_UNKNOWN_HANDLES = IDENTIFIER_PREFIX + "bundleCreateChatUnknownHandles";
    public static final String BUNDLE_CONTACT_VIEW_UUID = IDENTIFIER_PREFIX + "bundleContactViewUuid";

    public static final String ARG_HOST = IDENTIFIER_PREFIX + "hostArg";
    public static final String ARG_PORT = IDENTIFIER_PREFIX + "portArg";
    public static final String ARG_EMAIL = IDENTIFIER_PREFIX + "emailArg";
    public static final String ARG_PASSWORD = IDENTIFIER_PREFIX + "passwordArg";
    public static final String ARG_PASSWORD_ALREADY_HASHED = IDENTIFIER_PREFIX + "passwordAlreadyHashed";
    public static final String ARG_FAST_CONNECT = IDENTIFIER_PREFIX + "fastConnectArg";
    public static final String ARG_ATTACHMENT_GALLERY_CACHE = IDENTIFIER_PREFIX + "attachmentGalleryCacheArg";
    public static final String ARG_CAMERA_ATTACHMENT_FILE = IDENTIFIER_PREFIX + "argCameraAttachmentFile";
    public static final String ARG_ATTACHMENT_POPUP_CAMERA_RESULT_CODE = IDENTIFIER_PREFIX + "argAttachmentPopupCameraResultCode";
    public static final String ARG_ATTACHMENT_POPUP_CAMERA_INTENT = IDENTIFIER_PREFIX + "argAttachmentPopupCameraIntent";
    public static final String ARG_VOICE_RECORDING_FILE = IDENTIFIER_PREFIX + "argVoiceRecordingFile";

    public static final String BROADCAST_LOGIN_TIMEOUT = IDENTIFIER_PREFIX + "LoginTimeout";
    public static final String BROADCAST_LOGIN_ERROR = IDENTIFIER_PREFIX + "LoginError";
    public static final String BROADCAST_CONNECTION_SERVICE_STOPPED = IDENTIFIER_PREFIX + "ConnectionServiceStopped";

    public static final String BROADCAST_LOGIN_SUCCESSFUL = IDENTIFIER_PREFIX + "LoginSuccessful";
    public static final String BROADCAST_DISCONNECT_REASON_ALREADY_CONNECTED = IDENTIFIER_PREFIX + "DisconnectReasonAlreadyConnected";
    public static final String BROADCAST_DISCONNECT_REASON_INVALID_LOGIN = IDENTIFIER_PREFIX + "DisconnectReasonInvalidLogin";
    public static final String BROADCAST_DISCONNECT_REASON_SERVER_CLOSED = IDENTIFIER_PREFIX + "DisconnectReasonServerClosed";
    public static final String BROADCAST_DISCONNECT_REASON_ERROR = IDENTIFIER_PREFIX + "DisconnectReasonError";
    public static final String BROADCAST_DISCONNECT_REASON_FORCED = IDENTIFIER_PREFIX + "DisconnectReasonForced";
    public static final String BROADCAST_DISCONNECT_REASON_CLIENT_DISCONNECTED = IDENTIFIER_PREFIX + "DisconnectReasonClientDisconnected";
    public static final String BROADCAST_DISCONNECT_REASON_INCORRECT_VERSION = IDENTIFIER_PREFIX + "DisconnectReasonIncorrectVersion";

    public static final String BROADCAST_SEND_MESSAGE_ERROR = IDENTIFIER_PREFIX + "SendMessageError";
    public static final String BROADCAST_NEW_MESSAGE_ERROR = IDENTIFIER_PREFIX + "NewMessageError";
    public static final String BROADCAST_MESSAGE_UPDATE_ERROR = IDENTIFIER_PREFIX + "MessageUpdateError";
    public static final String BROADCAST_ACTION_PERFORM_ERROR = IDENTIFIER_PREFIX + "ActionPerformError";
    public static final String BROADCAST_RESULT_PROCESS_ERROR = IDENTIFIER_PREFIX + "ResultProcessError";
    public static final String BROADCAST_LOAD_ATTACHMENT_ERROR = IDENTIFIER_PREFIX + "LoadAttachmentError";
    public static final String BROADCAST_PLAY_AUDIO_ATTACHMENT_ERROR = IDENTIFIER_PREFIX + "PlayAudioAttachmentError";

    public static final String BROADCAST_IMAGE_FULLSCREEN_ACTIVITY_START = IDENTIFIER_PREFIX + "ImageFullScreenActivityStart";
    public static final String BROADCAST_VIDEO_FULLSCREEN_ACTIVITY_START = IDENTIFIER_PREFIX + "VideoFullScreenActivityStart";

    public static final String SHARED_PREFERENCES_LAST_HOST = IDENTIFIER_PREFIX + "lastHost";
    public static final String SHARED_PREFERENCES_LAST_EMAIL = IDENTIFIER_PREFIX + "lastEmail";
    public static final String SHARED_PREFERENCES_LAST_HASHED_PASSWORD = IDENTIFIER_PREFIX + "lastHashedPassword";
    public static final String SHARED_PREFERENCES_SIGNED_OUT = IDENTIFIER_PREFIX + "signedOut";

    private static weMessage instance;
    private MessageDatabase messageDatabase;
    private MessageManager messageManager;
    private Account currentAccount;
    private File attachmentFolder;
    private NotificationCallbacks notificationCallbacks;

    public static weMessage get(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        File attachmentFolder = new File(getFilesDir(), weMessage.ATTACHMENT_FOLDER_NAME);

        attachmentFolder.mkdir();
        this.attachmentFolder = attachmentFolder;

        this.messageDatabase = new MessageDatabase(this);

        instance = this;
    }

    public synchronized File getAttachmentFolder(){
        return attachmentFolder;
    }

    public synchronized MessageDatabase getMessageDatabase(){
        return messageDatabase;
    }

    public synchronized MessageManager getMessageManager(){
        if (messageManager == null){
            messageManager = new MessageManager(this);
        }
        return messageManager;
    }

    public synchronized Account getCurrentAccount(){
        if (currentAccount == null) throw new MessageDatabase.AccountNotLoggedInException();

        return currentAccount;
    }

    public synchronized boolean isSignedIn(){
        SharedPreferences sharedPreferences = getSharedPreferences(weMessage.APP_IDENTIFIER, Context.MODE_PRIVATE);

        return !sharedPreferences.getBoolean(weMessage.SHARED_PREFERENCES_SIGNED_OUT, false);
    }

    public synchronized boolean performNotification(String macGuid){
        if (notificationCallbacks == null) return true;

        return notificationCallbacks.onNotification(macGuid);
    }

    public synchronized void setCurrentAccount(Account account){
        this.currentAccount = account;
    }

    public synchronized void signIn(){
        SharedPreferences.Editor editor = getSharedPreferences(weMessage.APP_IDENTIFIER, Context.MODE_PRIVATE).edit();

        editor.putBoolean(weMessage.SHARED_PREFERENCES_SIGNED_OUT, false);
        editor.apply();

        //TODO: Set current Account here, move up / out of connection handler
    }

    public synchronized void signOut(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences.Editor editor = getSharedPreferences(weMessage.APP_IDENTIFIER, Context.MODE_PRIVATE).edit();

        notificationManager.cancelAll();
        editor.putBoolean(weMessage.SHARED_PREFERENCES_SIGNED_OUT, true);
        editor.apply();

        //TODO: Dump all goes here
    }

    public synchronized void setNotificationCallbacks(NotificationCallbacks notificationCallbacks){
        this.notificationCallbacks = notificationCallbacks;
    }

    public synchronized void dumpMessageManager(){
        messageManager.dumpAll(this);
        messageManager = null;
    }
}