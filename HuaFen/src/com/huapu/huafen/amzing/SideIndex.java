package com.huapu.huafen.amzing;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.Pair;

import java.util.List;

/**
 * Created by lalo.zhang on 2015/8/28.
 */
public class SideIndex extends LinearLayout {

    private int sideHeight;
    private float heightPerItem;
    private TextView centerLetter;

    public SideIndex(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
    }

    public SideIndex(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setCenterLetterText(TextView text){
        this.centerLetter=text;
    }

    public void genIndexBar(Context context,final AmazingListView amazingListView,int letterColor,int textSize) {
        sideHeight = this.getHeight();
        this.removeAllViews();
        final List<Pair<String, List<Object>>> data = amazingListView.getAdapter().getData();
        if(!ArrayUtil.isEmpty(data)){
            for (int i = 0; i < data.size(); i++) {
                TextView tmpTV = new TextView(context);
                String tmpLetter = data.get(i).first;
                tmpTV.setText(tmpLetter);
                tmpTV.setGravity(Gravity.CENTER);
                tmpTV.setTextColor(letterColor);
                tmpTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                LayoutParams params = new LayoutParams(-1, LayoutParams.WRAP_CONTENT, 1);
                tmpTV.setLayoutParams(params);
                this.addView(tmpTV);
            }

            if (amazingListView.getAdapter().getData().size() > 0) {
                heightPerItem = sideHeight / this.getChildCount();
                this.setTouchDelegate(new TouchDelegate(new Rect(), this) {

                    @Override
                    public boolean onTouchEvent(MotionEvent event) {
                        int index = (int) (event.getY() / heightPerItem);
                        int position = amazingListView.getAdapter().getPositionForSection(index);
                        if (index < 0) {
                            index = 0;
                        }
                        if (index >= data.size()) {
                            index = data.size() - 1;
                        }
                        amazingListView.setSelection(position);
                        if(centerLetter!=null){
                            switch (event.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:
                                    centerLetter.setVisibility(View.VISIBLE);
                                    centerLetter.setText(data.get(index).first);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    centerLetter.setVisibility(View.GONE);
                                    break;
                                default:
                                    break;
                            }
                        }

                        return true;
                    }
                });
            }
        }

    }

    public void genIndexBar(Context context, final AmazingListView amazingListView, int letterColor, int textSize, final int headerCount) {
        sideHeight = this.getHeight();
        this.removeAllViews();
        final List<Pair<String, List<Object>>> data = amazingListView.getAdapter().getData();
        if(!ArrayUtil.isEmpty(data)){
            for (int i = 0; i < data.size(); i++) {
                TextView tmpTV = new TextView(context);
                String tmpLetter = data.get(i).first;
                tmpTV.setText(tmpLetter);
                tmpTV.setGravity(Gravity.CENTER);
                tmpTV.setTextColor(letterColor);
                tmpTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                LayoutParams params = new LayoutParams(-1, LayoutParams.WRAP_CONTENT, 1);
                tmpTV.setLayoutParams(params);
                this.addView(tmpTV);
            }

            if (amazingListView.getAdapter().getData().size() > 0) {
                heightPerItem = sideHeight / this.getChildCount();
                this.setTouchDelegate(new TouchDelegate(new Rect(), this) {

                    @Override
                    public boolean onTouchEvent(MotionEvent event) {
                        int index = (int) (event.getY() / heightPerItem);
                        int position = amazingListView.getAdapter().getPositionForSection(index)+headerCount;
                        if (index < 0) {
                            index = 0;
                        }
                        if (index >= data.size()) {
                            index = data.size() - 1;
                        }
                        amazingListView.setSelection(position);
                        if(centerLetter!=null){
                            switch (event.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:
                                    centerLetter.setVisibility(View.VISIBLE);
                                    centerLetter.setText(data.get(index).first);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    centerLetter.setVisibility(View.GONE);
                                    break;
                                default:
                                    break;
                            }
                        }

                        return true;
                    }
                });
            }
        }

    }
}
