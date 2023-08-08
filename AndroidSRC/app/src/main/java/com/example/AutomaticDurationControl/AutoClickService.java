package com.example.AutomaticDurationControl;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.util.Calendar;


public class AutoClickService extends AccessibilityService {
    int IS_CLICKING = 0;
    int IS_NOT_CLICKING = 1;
    int AutoClickState = IS_NOT_CLICKING;

    private final String TAG = getClass().getCanonicalName();
    final Handler handler = new Handler();
    float height,width;
    int LAYOUT_FLAG;
    WindowManager windowManager;


    int screenWidth;
    int screenHeight;
    Drawable Drawable_ic_media_play;
    Drawable Drawable_ic_media_pause;
    Drawable Drawable_ic_delete;
    Drawable Drawable_ic_input_add;
    /**
     * @brief          获取系统资源和设备信息
     * @author         小企鹅
     * @return         none
     */
    public void getAndroidResources(){
        //获取安卓自带的资源图标
        Drawable_ic_media_play = getResources().getDrawable(android.R.drawable.ic_media_play);
        Drawable_ic_media_pause = getResources().getDrawable(android.R.drawable.ic_media_pause);
        Drawable_ic_delete = getResources().getDrawable(android.R.drawable.ic_delete);
        Drawable_ic_input_add = getResources().getDrawable(android.R.drawable.ic_input_add);
        Log.d(TAG, "已获取系统资源包括图标");
        //获取设备屏幕尺寸信息
        DisplayMetrics displayMetrics = new DisplayMetrics();// 创建DisplayMetrics对象以获取屏幕尺寸信息
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);// 从WindowManager中获取默认Display的屏幕尺寸
        // 获取屏幕宽度和高度（以像素为单位）
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }
    View mFloatingView;
    TextView TextView_Duration;
    ImageButton Button_Pause_Play;
    ImageButton Button_CloseFloatingWindow;
    ImageButton Button_AddRectangleArea;

    View FloatingView_AddRectangleArea;
    DrawRectangleView View_drawRectangleArea;
    Button Button_Sure;
    /**
     * @brief          获取获取页面布局中的组件
     * @author         小企鹅
     * @return         none
     */
    public void getLayoutAssembly(){
        //填充功能栏并获取其中的组件
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null);
        TextView_Duration = (TextView) mFloatingView.findViewById(R.id.TextView_Duration);
        Button_Pause_Play = (ImageButton) mFloatingView.findViewById(R.id.Button_Pause_Play);
        Button_CloseFloatingWindow = (ImageButton) mFloatingView.findViewById(R.id.Button_CloseFloatingWindow);
        Button_AddRectangleArea = (ImageButton) mFloatingView.findViewById(R.id.Button_AddRectangleArea);
        //填充矩形框绘制栏并获取其中的组件
        FloatingView_AddRectangleArea = LayoutInflater.from(this).inflate(R.layout.layout_floating_add_rectangle_area, null);
        View_drawRectangleArea = (DrawRectangleView) FloatingView_AddRectangleArea.findViewById(R.id.View_drawRectangleArea);
        Button_Sure = (Button) FloatingView_AddRectangleArea.findViewById(R.id.Button_Sure);
        Log.d(TAG, "已获取页面布局中的组件");
    }


    @Override
    protected void onServiceConnected() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //创建视窗管理器
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //获取基础信息
        getLayoutAssembly();
        getAndroidResources();

        //功能栏悬浮窗的布局属性设置
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
        //添加工具栏的窗口内容并显示出来
        windowManager.addView(mFloatingView,layoutParams);
        mFloatingView.setVisibility(View.INVISIBLE);
        //获取悬浮窗的高度和宽度
        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

        //框选矩形区域属性设置
        WindowManager.LayoutParams Params_FloatingView_AddRectangleArea = new WindowManager.LayoutParams(
                screenWidth,
                screenHeight,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        //添加框选页面并不显示
        windowManager.addView(FloatingView_AddRectangleArea,Params_FloatingView_AddRectangleArea);
        FloatingView_AddRectangleArea.setVisibility(View.INVISIBLE);

        //注册广播接收器
        ShowFloatingWindowBroadcastReceiver receiver = new ShowFloatingWindowBroadcastReceiver();
        IntentFilter filter = new IntentFilter("SHOW_FLOATING_WINDOW");
        registerReceiver(receiver, filter);

        //设置相关组件属性
        Configure_Button_CloseFloatingWindow();
        Configure_Button_Pause_Play();
        Configure_Button_AddRectangleArea();
        Configure_TextView_Duration(layoutParams);
        Configure_Button_Sure();
    }
    int SelectedAreaStartX=0;
    int SelectedAreaStartY=0;
    int SelectedAreaWidth=0;
    int SelectedAreaHeight=0;
    /**
     * @brief          设置框选区域的悬浮窗中'确定'按钮的相关属性
     * @author         小企鹅
     * @return         none
     */
    private void Configure_Button_Sure() {
        Button_Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingView_AddRectangleArea.setVisibility(View.INVISIBLE);//关闭框选区域的页面
                //更新框选的区域
                SelectedAreaStartX = View_drawRectangleArea.getRectangleStartX();
                SelectedAreaStartY = View_drawRectangleArea.getRectangleStartY();
                SelectedAreaWidth = View_drawRectangleArea.getRectangleWidth();
                SelectedAreaHeight = View_drawRectangleArea.getRectangleHeight();

//                Log.d(TAG,"点击末端Y："+(SelectedAreaHeight+SelectedAreaStartY));
//                Log.d(TAG, "绘制时的末端Y位置:"+View_drawRectangleArea.getRectangleEndY());
            }
        });
    }
    /**
     * @brief          设置悬浮窗中Button_AddRectangleArea（框选矩形区域按钮）的相关属性
     * @author         小企鹅
     * @return         none
     */
    private void Configure_Button_AddRectangleArea() {
        Button_AddRectangleArea.setImageDrawable(Drawable_ic_input_add); // 替换系统内置的媒体删除图标资源
        Button_AddRectangleArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingView_AddRectangleArea.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * @brief          设置悬浮窗中Button_CloseFloatingWindow（关闭悬浮窗按钮）的相关属性
     * @author         小企鹅
     * @return         none
     */
    private void Configure_Button_CloseFloatingWindow() {
        //设置关闭按钮图片
        Button_CloseFloatingWindow.setImageDrawable(Drawable_ic_delete); // 替换系统内置的媒体删除图标资源
        //点击关闭按钮后停止自动点击，状态归0，关闭悬浮窗
        Button_CloseFloatingWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);//清除点击任务
                AutoClickState = IS_NOT_CLICKING;//状态归0
                Button_Pause_Play.setImageDrawable(Drawable_ic_media_play); // 替换系统内置的媒体播放图标资源
                // 点击关闭按钮后，关闭悬浮窗
                closeFloatingWindowAndReturnToMain();
            }
        });
    }
    /**
     * @brief          设置悬浮窗中Button_Pause_Play（开始与停止按钮）的相关属性
     * @author         小企鹅
     * @return         none
     */
    private void Configure_Button_Pause_Play(){
        //设置开始按钮图片
        Button_Pause_Play.setImageDrawable(Drawable_ic_media_play); // 替换系统内置的媒体播放图标资源
        Button_Pause_Play.setOnClickListener(new View.OnClickListener() {
            int click_x;
            int click_y;
            int BUTTON_SURE = 0;
            int BUTTON_CONTINUE = 1;
            int ClickButtonState = BUTTON_SURE;
            @Override
            public void onClick(View view) {
                if (AutoClickState == IS_CLICKING){//切换到停止状态
                    AutoClickState = IS_NOT_CLICKING;
                    Button_Pause_Play.setImageDrawable(Drawable_ic_media_play);
                    Log.d(TAG, "停止自动点击");
                    handler.removeCallbacksAndMessages(null);

                } else if (AutoClickState == IS_NOT_CLICKING) {//切换到自动点击状态
                    AutoClickState = IS_CLICKING;
                    Button_Pause_Play.setImageDrawable(Drawable_ic_media_pause);
                    Log.d(TAG, "正在自动点击");
                    Log.d(TAG, "end Y :"+(SelectedAreaStartY+SelectedAreaHeight));
                    //初始化点击位置
                    click_x = SelectedAreaStartX+SelectedAreaWidth/2;
                    click_y = StatusBarHeight + SelectedAreaStartY + (int) Math.round(SelectedAreaHeight*0.82);
                    //在指定区域（框选的矩形区域）内自动点击
                    //目前的方案是直接点击'确定'按钮和'继续听讲'按钮
                    //创建2个点击状态，分别点击确定和继续听讲
                    handler.postDelayed(new Runnable() {
                        int deltaY = (int) (50*(SelectedAreaHeight/1550.0));
//                        int deltaY = 10;
                        @Override
                        public void run() {
                            if (click_y>StatusBarHeight+SelectedAreaStartY+SelectedAreaHeight-15){
                                ClickButtonState = (ClickButtonState+1)%2;//目前就只点2个按钮
                                click_y = (int) Math.round(SelectedAreaStartY+SelectedAreaHeight*0.90);
                            }

                            if (ClickButtonState == BUTTON_SURE){
                                click_x = SelectedAreaStartX+SelectedAreaWidth/2;
                            }else if (ClickButtonState == BUTTON_CONTINUE){
                                click_x = SelectedAreaStartX+SelectedAreaWidth-30;
                            }
                            performClick(click_x,click_y);
                            click_y += deltaY;
//                            Log.d(TAG,"click_y:"+click_y);
                            handler.postDelayed(this,500);
                        }
                    },1);

                }
            }
        });
    }
    /**
     * @brief          设置悬浮窗中TextView_Duration的相关属性
     * @author         小企鹅
     * @return         none
     */
    private void Configure_TextView_Duration(WindowManager.LayoutParams layoutParams){
        //拖动文本框时改变悬浮窗位置
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
                            // 输出屏幕尺寸信息
                            Log.d("ScreenSize", "屏幕宽度：" + screenWidth + "px，屏幕高度：" + screenHeight + "px");
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
    }
    /**
     * @brief          关闭悬浮窗
     * @author         小企鹅
     * @return         none
     */
    private void closeFloatingWindowAndReturnToMain() {
        // 设置悬浮窗视图为不可见
        mFloatingView.setVisibility(View.INVISIBLE);
        FloatingView_AddRectangleArea.setVisibility(View.INVISIBLE);
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
//            Log.d(TAG, "auto click x:" + x + "  y:" + y);
            Path path = new Path();
            path.moveTo(x, y);

            GestureDescription gestureDescription = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 50L, 50L))//设置点击的参数
                    .build();

//            dispatchGesture(gestureDescription, gestureCallback, null);
            dispatchGesture(gestureDescription, null, null);
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
            Log.d(TAG, "点击失败");
        }
    };

    int StatusBarHeight = 0;
    public class ShowFloatingWindowBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if (intent.getAction().equals("SHOW_FLOATING_WINDOW")){
                StatusBarHeight = intent.getIntExtra("StatusBarHeight",0);//获取顶部状态栏的高度来补偿点击纵坐标的偏差
                mFloatingView.setVisibility(View.VISIBLE);
                Log.d(TAG, "接收到显示悬浮窗的广播");
                Log.d(TAG, "顶部状态栏高度为："+StatusBarHeight);
            }
        }
    }
}