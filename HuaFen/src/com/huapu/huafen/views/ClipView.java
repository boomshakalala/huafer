package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

public class ClipView extends View {
    public static final int SX = 5;//显示器X轴起始余量
    public static final int EX = 5;//显示器X轴结束余量
    private int clipW;
    private int clipH;
    private static final boolean CIRCLE_MODE = true;
    private boolean isCircle;
    
    public ClipView(Context context) {
        super(context);
    }
    
    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint();
        paint.setColor(0xaa000000);
        paint.setAntiAlias(true);
        if (getCircle()) {
        	canvas.save();
        	Path path = new Path();
        	path.addCircle(width/2, height/2, clipW/2, Path.Direction.CCW);
        	canvas.clipPath(path, Region.Op.DIFFERENCE);
        	canvas.drawPaint(paint);
        	
        	canvas.restore();
    		paint.setColor(Color.WHITE);
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setStrokeWidth(4);
    		paint.setAntiAlias(true);
    		canvas.drawCircle(width/2, height/2, clipW/2-2,
    				paint);
        } else {
	        //top
	        canvas.drawRect(0, 0, width, (height - clipH)/2, paint);
	        //left
	        canvas.drawRect(0, (height - clipH)/2, (width - clipW)/2, (height + clipH)/2, paint);
	        //right
	        canvas.drawRect((width + clipW)/2, (height - clipH)/2, width , (height + clipH)/2, paint);
	        //bottom
	        canvas.drawRect(0, (height + clipH)/2, width, height, paint);
	        
	        Paint paintLine = new Paint();  paintLine.setColor(0xFFFFFFFF);
	        canvas.drawLine((width - clipW)/2, (height - clipH)/2, (width + clipW)/2, (height - clipH)/2, paintLine);
	        canvas.drawLine((width - clipW)/2, (height - clipH)/2, (width - clipW)/2, (height + clipH)/2, paintLine);
	        canvas.drawLine((width + clipW)/2 - 1, (height - clipH)/2, (width + clipW)/2, (height + clipH)/2, paintLine);
	        canvas.drawLine((width - clipW)/2, (height + clipH)/2 - 1, (width + clipW)/2, (height + clipH)/2 - 1, paintLine);
        }
    }

    public boolean getCircle(){
    	return isCircle;
    }
    
    public void setCircle(boolean isCircle){
    	this.isCircle = isCircle;
    }
    public int getClipW() {
        return clipW;
    }

    public void setClipW(int clipW) {
        this.clipW = clipW;
    }
    
    public int getClipH() {
        return clipH;
    }

    public void setClipH(int clipH) {
        this.clipH = clipH;
    }
    
    public int getClipLeft() {
        return (getWidth() - clipW)/2;
    }
    
    public int getClipTop() {
        return (getHeight() - clipH)/2;
    }
    
    public int getClipRight() {
        return (getWidth() + clipW)/2;
    }
    
    public int getClipBottom() {
        return (getHeight() + clipH)/2;
    }
}
