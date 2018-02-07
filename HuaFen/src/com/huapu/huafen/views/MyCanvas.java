package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by Administrator on 2016/11/8.
 */
public class MyCanvas extends View{

    Paint  paint;  //绘图

    public MyCanvas(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
    }

    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
    }

    public MyCanvas(Context context, AttributeSet attrs, Paint paint) {
        super(context, attrs);
        this.paint = paint;
    }

    /**
     * 绘制网格线
     */
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.BLUE);
        //canvas.drawCircle(100, 100, 90, paint);
        final int width = 480;  //hdpi 480x800
        final int height = 800;
        final int edgeWidth = 10;
        final int space = 30;   //长宽间隔
        int vertz = 0;
        int hortz = 0;
        for(int i=0;i<100;i++){
            canvas.drawLine(0,  vertz,  width, vertz, paint);
            canvas.drawLine(hortz, 0, hortz, height, paint);
            vertz+=space;
            hortz+=space;
        }
    }
}
