package com.example.AutomaticDurationControl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawRectangleView extends View {
    private Paint paint;
    private Rect rect;
    private float startX, startY, endX, endY;
    private boolean isDrawing = false;

    // 无参构造函数
    public DrawRectangleView(Context context) {
        super(context);
        init();
    }
    // 有参构造函数
    public DrawRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawing) {
            canvas.drawRect(rect, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                isDrawing = true;
                break;
            case MotionEvent.ACTION_MOVE:
                endX = x;
                endY = y;
                rect.set((int) startX, (int) startY, (int) endX, (int) endY);
                invalidate(); // Trigger onDraw to redraw the rectangle
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                break;
        }
        return true;
    }
}
