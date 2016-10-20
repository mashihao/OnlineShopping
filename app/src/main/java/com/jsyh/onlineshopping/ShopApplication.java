package com.jsyh.onlineshopping;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jsyh.onlineshopping.umeng.push.CustomNotificationHandler;
import com.orhanobut.logger.Logger;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.io.File;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ShopApplication extends Application {
    private File extStorageAppBasePath;
    private File extStorageAppCachePath;

    private PushAgent mPushAgent;
    @Override
    public void onCreate() {
        super.onCreate();
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {


                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();

                    }
                });
            }
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView);
                        builder.setContentTitle(msg.title)
                                .setContentText(msg.text)
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        Notification mNotification = builder.build();
                        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            if (externalStorageDir != null) {
                // {SD_PATH}/Android/data/
                extStorageAppBasePath = new File(externalStorageDir.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getPackageName());

            }

            if (extStorageAppBasePath != null) {
                extStorageAppCachePath = new File(extStorageAppBasePath.getAbsolutePath() + File.separator + "webViewCache");
                boolean isCachePathAvailable = true;
                if (!extStorageAppCachePath.exists()) {
                    isCachePathAvailable = extStorageAppCachePath.mkdirs();
                    if (!isCachePathAvailable) {
                        extStorageAppCachePath = null;
                    }
                }
            }
        }
        initLogger();
    }

    private void initLogger() {
        Logger.init("tag");
    }

    @Override
    public File getCacheDir() {

        if (extStorageAppCachePath != null) {
            return extStorageAppCachePath;
        } else {
            return super.getCacheDir();
        }
    }
}
