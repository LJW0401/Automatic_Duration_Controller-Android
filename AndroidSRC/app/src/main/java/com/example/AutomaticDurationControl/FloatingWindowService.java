package com.example.AutomaticDurationControl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FloatingWindowService extends Service {
    int LAYOUT_FLAG;
    float height,width;
    View mFloatingView;
    TextView TextView_Duration;
    WindowManager windowManager;
    ImageButton Button_Pause_Play;
    ImageButton Button_CloseFloatingWindow;
    Drawable ic_media_play;
    Drawable ic_delete;
    @Override
    public void onCreate() {
        super.onCreate();
        // 在此进行悬浮窗的初始化操作
        // 例如创建悬浮窗视图，设置视图的样式、位置等
    }

    public void getAndroidResources(){
        //获取系统资源
        ic_media_play = getResources().getDrawable(android.R.drawable.ic_media_play);
        ic_media_play = getResources().getDrawable(android.R.drawable.ic_media_pause);
        ic_delete = getResources().getDrawable(android.R.drawable.ic_delete);
    }

    public void getLayoutAssembly(){
        //inflate widget layout
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null);
        TextView_Duration = (TextView) mFloatingView.findViewById(R.id.TextView_Duration);
        Button_Pause_Play = (ImageButton) mFloatingView.findViewById(R.id.Button_Pause_Play);
        Button_CloseFloatingWindow = (ImageButton) mFloatingView.findViewById(R.id.Button_CloseFloatingWindow);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getAndroidResources();//获取系统资源
        getLayoutAssembly();//获取页面组件

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        //初始化位置
        layoutParams.gravity = Gravity.TOP|Gravity.RIGHT;
        layoutParams.x = 0;
        layoutParams.y = 100;

        //添加悬浮窗的窗口内容并显示出来
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(mFloatingView,layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        //设置开始按钮图片
        Button_Pause_Play.setImageDrawable(ic_media_play); // 替换系统内置的媒体播放图标资源
        //设置关闭按钮图片
        Button_CloseFloatingWindow.setImageDrawable(ic_delete); // 替换系统内置的媒体播放图标资源

        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

//        //显示与更新实时时间
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                TextView_Duration.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
//                handler.postDelayed(this,1000);
//            }
//        },10);

        //点击关闭按钮后关闭悬浮窗并回到主界面
        Button_CloseFloatingWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭按钮后，关闭悬浮窗并回到主页面
                closeFloatingWindowAndReturnToMain();
            }
        });

        //拖动悬浮窗时改变悬浮窗位置
        TextView_Duration.setOnTouchListener(new View.OnTouchListener(){
            int initialX,initialY;
            float initialTouchX,initialTouchY;
            long startClickTime;

            int MAX_CLICK_DURATION=200;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){

                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        startClickTime = Calendar.getInstance().getTimeInMillis();

                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        //touch position
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        return true;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis()-startClickTime;

                        layoutParams.x = initialX+(int) (initialTouchX-motionEvent.getRawX());
                        layoutParams.y = initialY+(int) (motionEvent.getRawY()-initialTouchY);

                        if(clickDuration<MAX_CLICK_DURATION)
                        {
                            Toast.makeText(FloatingWindowService.this, "Time:"+TextView_Duration.getText().toString(), Toast.LENGTH_SHORT).show();
                        }else{
                            //remove widget
                            if(layoutParams.y>(height*0.6))
                            {
                                stopSelf();
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //calulate X & Y coordinates of view
                        layoutParams.x = initialX+(int) (initialTouchX-motionEvent.getRawX());
                        layoutParams.y = initialY+(int) (motionEvent.getRawY()-initialTouchY);

                        //update layout width new coordinates
                        windowManager.updateViewLayout(mFloatingView, layoutParams);

                        return true;
                }
                return false;
            }
        });


        return START_STICKY; // 表示服务在被意外终止后将自动重启
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在此进行资源释放，取消悬浮窗等操作
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 如果服务支持绑定，可以在这里返回绑定的接口对象
        return null;
    }

    private void closeFloatingWindowAndReturnToMain() {
        // 移除悬浮窗视图
        windowManager.removeView(mFloatingView);
        // 回到主页面的操作
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 停止当前服务
        stopSelf();
    }
}