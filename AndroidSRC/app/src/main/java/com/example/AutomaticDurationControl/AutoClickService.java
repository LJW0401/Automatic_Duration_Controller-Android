package com.example.AutomaticDurationControl;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.core.app.NotificationCompat;

public class AutoClickService extends AccessibilityService {
    private final String TAG = getClass().getCanonicalName();

    View mFloatingView;
    public void setFloatingView(View floatingView) {
        // 在这里你可以保存 mFloatingView，或者直接使用其中的按钮
        // 例如：Button_Pause_Play = floatingView.findViewById(R.id.Button_Pause_Play);
        mFloatingView = floatingView;
    }
    Drawable Drawable_ic_media_play;
    Drawable Drawable_ic_media_pause;
    Drawable Drawable_ic_delete;
    /**
     * @brief          获取系统资源包括图标
     * @author         小企鹅
     * @return         none
     */
    public void getAndroidResources(){
        Drawable_ic_media_play = getResources().getDrawable(android.R.drawable.ic_media_play);
        Drawable_ic_media_pause = getResources().getDrawable(android.R.drawable.ic_media_pause);
        Drawable_ic_delete = getResources().getDrawable(android.R.drawable.ic_delete);
        Log.d(TAG, "已获取系统资源包括图标");
    }
    TextView TextView_Duration;
    ImageButton Button_Pause_Play;
    ImageButton Button_CloseFloatingWindow;
    /**
     * @brief          获取页面布局中的组件
     * @author         小企鹅
     * @return         none
     */
    public void getLayoutAssembly(){
        TextView_Duration = (TextView) mFloatingView.findViewById(R.id.TextView_Duration);
        Button_Pause_Play = (ImageButton) mFloatingView.findViewById(R.id.Button_Pause_Play);
        Button_CloseFloatingWindow = (ImageButton) mFloatingView.findViewById(R.id.Button_CloseFloatingWindow);
        Log.d(TAG, "已获取页面布局中的组件");
    }
    @Override
    protected void onServiceConnected() {
        getLayoutAssembly();
        getAndroidResources();
        showNotification();
        //设置启停按钮的功能
        Button_Pause_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button_Pause_Play.setImageDrawable(Drawable_ic_media_pause); // 替换系统内置的媒体播放图标资源
                Toast.makeText(getApplicationContext(), "按钮被点击啦", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "按钮被点击啦");
            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理无障碍事件
    }

    /**
     * @brief          实现自动点击的方法
     * @author         小企鹅
     * @param[in]      x : 要点击的位置的x坐标
     * @param[in]      y : 要点击的位置的y坐标
     * @return         none
     */
    private void performClick(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "auto click x:" + x + "  y:" + y);
            Path path = new Path();
            path.moveTo(x, y);

            GestureDescription gestureDescription = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 100L, 100L))
                    .build();

            dispatchGesture(gestureDescription, gestureCallback, null);
        }
    }

    @Override
    public void onInterrupt() {
        // 服务中断时的回调
        Toast.makeText(this, "Interrupted", Toast.LENGTH_SHORT).show();
    }

    /**
     * @brief          检测是否完成点击，如果完成了点击操作就Log记录一下
     * @author         小企鹅
     * @return         none
     */
    private AccessibilityService.GestureResultCallback gestureCallback = new AccessibilityService.GestureResultCallback() {
        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            // 模拟点击完成
            Log.d(TAG, "已点击");
        }
        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            // 模拟点击取消
            Log.d(TAG, "取消点击");
        }
    };
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "auto_click_channel";
    private static final CharSequence CHANNEL_NAME = "Auto Click Service Channel";
    /**
     * @brief          使用通知栏保活，防止被杀
     * @author         小企鹅
     * @return         none
     */
    private void showNotification() {
        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to launch the MainActivity when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("自动刷时长器")
                .setContentText("正在后台运行")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 这里使用对应的资源ID
                .setContentIntent(pendingIntent)
                .build();

        // Start the service in foreground mode with the notification
        startForeground(NOTIFICATION_ID, notification);
    }
}