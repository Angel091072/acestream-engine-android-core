package org.acestream.engine.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.acestream.engine.AceStreamEngineBaseApplication;
import org.acestream.engine.notification.NotificationData;

import java.io.File;
import java.util.Map;

public class IntentHelper {
    private final static String TAG = "AceStream/IHelper";

    public static void sendBrowserIntent(@NonNull Context context, @NonNull NotificationData notificationData) {
        boolean skipWebView = false;

        if(notificationData.target_url == null) {
            throw new NullPointerException("null target_url");
        }
        if(notificationData.target_url.url == null) {
            throw new NullPointerException("null url");
        }

        // For Android "default" means webview, "system" means "other browser, installed in the system"
        if (TextUtils.equals(notificationData.target_url.where, "system")) {
            skipWebView = true;
        }
        Intent browserIntent = AceStreamEngineBaseApplication.getBrowserIntent(context, notificationData.target_url.url, skipWebView);
        AceStreamEngineBaseApplication.startBrowserIntent(context, browserIntent);
    }

    public static boolean sendImplicitActivityIntent(@NonNull Context context, @NonNull NotificationData notificationData) {
        if(notificationData.action == null) {
            throw new NullPointerException("null action");
        }

        Intent intent = new Intent();
        intent.setAction(notificationData.action);
        intent.setFlags(notificationData.flags);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if(notificationData.uri != null) {
            Uri uri = Uri.parse(notificationData.uri);
            Uri contentUri;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && TextUtils.equals(uri.getScheme(), "file")) {
                contentUri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".fileprovider",
                        new File(uri.getPath()));
            }
            else {
                contentUri = uri;
            }

            if(notificationData.mime == null) {
                intent.setData(contentUri);
            }
            else {
                intent.setDataAndType(contentUri, notificationData.mime);
            }
        }

        try {
            context.startActivity(intent);
            return true;
        }
        catch(Exception e) {
            Log.e(TAG, "notification: failed to start intent", e);
            return false;
        }
    }

    public static boolean sendExplicitActivityIntent(
            @NonNull Context context,
            @NonNull String className,
            @NonNull NotificationData notificationData) {

        // for backward compatibility
        if(TextUtils.equals(className, "org.acestream.engine.NotificationActivity")) {
            className = "org.acestream.engine.notification.NotificationActivity";
        }

        Intent intent = new Intent();
        intent.setClassName(AceStreamEngineBaseApplication.context(), className);
        intent.setFlags(notificationData.flags);

        if(notificationData.extras != null) {
            for(Map.Entry<String,String> item: notificationData.extras.entrySet()) {
                intent.putExtra(item.getKey(), item.getValue());
            }
        }

        try {
            context.startActivity(intent);
            return true;
        }
        catch(Exception e) {
            Log.e(TAG, "notification: failed to start intent", e);
            return false;
        }
    }
}
