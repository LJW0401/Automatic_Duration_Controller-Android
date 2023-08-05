package com.example.AutomaticDurationControl;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Binder;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class AutoClickService extends AccessibilityService {
    private final String TAG = getClass().getCanonicalName();

    FrameLayout mLayout;
    @Override
    protected void onServiceConnected() {
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
//        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);

        configurePowerButton();
    }

    private int clickCount = 0;
    private Timer timer;
    private void configurePowerButton() {
//        Button powerButton = (Button) mLayout.findViewById(R.id.power);
//        powerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    public void run() {
//                        if (clickCount < 5) {
//                            performClick(200, 400);
//                            clickCount++;
//                        } else {
//                            clickCount=0;
//                            timer.cancel();
//                        }
//                    }
//                }, 0, 500);
//            }
//        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理无障碍事件
        final int eventType = event.getEventType();
    }

    private void showToastAtPosition(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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

}