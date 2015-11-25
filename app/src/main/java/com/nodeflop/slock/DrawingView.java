package com.nodeflop.slock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;

public class DrawingView extends View {

    private HashSet<Point> points_set;
    private ArrayList<Point> points;

    private Paint drawPaint;
    private Path path = new Path();
    private boolean touch_up;
    private boolean show_line;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);

        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(15);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setColor(Color.WHITE);
        drawPaint.setStyle(Paint.Style.STROKE);

        points = new ArrayList<Point>();
        points_set = new HashSet<Point>();
        touch_up = false;
        show_line = true;
    }

    public void showLine(boolean t){
        show_line = t;
    }

    public boolean isTouch_up(){
        return touch_up;
    }

    public ArrayList<Point> getGesture(){
        return points;
    }

    public void reset(){
        touch_up = false;
        points_set = new HashSet<Point>();
        points = new ArrayList<Point>();
    }

    public boolean onTouch(MotionEvent event){
        int x = (int)event.getX();
        int y = (int)event.getY();
        if(points_set.add(new Point(x, y))){
            points.add(new Point(x, y));
        }

        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                path = new Path();
                touch_up = true;
                break;
            default:
                return false;
        }
        // Force a view to draw again
        postInvalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(show_line){
            drawPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, drawPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouch(event);
    }
}
