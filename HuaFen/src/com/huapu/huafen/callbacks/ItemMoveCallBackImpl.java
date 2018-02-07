package com.huapu.huafen.callbacks;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by admin on 2017/3/9.
 */

public class ItemMoveCallBackImpl extends ItemTouchHelper.Callback{


    private ItemMoveHelperApi mHelperApi;
    private int mDragStartPosition; //能够拖拽的开始位置
    private int mDragEndPosition; //能够拖拽的结束位置
    private boolean mDragEndPositionFlag; //是否设置了拖拽结束位置

    public ItemMoveCallBackImpl(ItemMoveHelperApi helperApi) {
        this.mHelperApi = helperApi;
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.e("ItemMoveCallBackImpl","getMovementFlags");
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.e("ItemMoveCallBackImpl","onMove:"+viewHolder.getAdapterPosition()+","+target.getAdapterPosition());
        if (mHelperApi != null) {
            mHelperApi.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e("ItemMoveCallBackImpl","onSwiped:"+direction);

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.e("ItemMoveCallBackImpl","onSelectedChanged:"+actionState);
        if (mHelperApi == null) {
            super.onSelectedChanged(viewHolder, actionState);
            return;
        }
        if (viewHolder == null) {
            mHelperApi.onMoveEnd();
        } else {
            mHelperApi.onMoveStart();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

//    @Override
//    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        dY = getLimitedDy(recyclerView, viewHolder, dY);
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//    }
    /**
     * 根据方向和条件获取限制在RecyclerView内部的DY值
     *
     * @param recyclerView 列表
     * @param viewHolder   drag的ViewHolder
     * @param dY           限制前的DY值
     * @return 限制后的DY值
     */
    private float getLimitedDy(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dY) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)
                || ((LinearLayoutManager) layoutManager).getOrientation() != OrientationHelper.VERTICAL) {
            return dY;
        }
        int position = viewHolder.getLayoutPosition();
        mDragEndPosition = mDragEndPositionFlag ?
                mDragStartPosition : recyclerView.getAdapter().getItemCount() - 1;
        if (position == mDragStartPosition) {
            return dY < 0 ? 0 : dY;
        } else if (position == mDragEndPosition) {
            return dY > 0 ? 0 : dY;
        }
        return dY;
    }

    /**
     * 设置拖拽开始位置
     *
     * @param dragStartPosition 开始位置
     */
    public void setDragStartPosition(int dragStartPosition) {
        mDragStartPosition = dragStartPosition;
    }

    /**
     * 设置拖拽结束的位置
     *
     * @param dragEndPosition 结束位置
     */
    public void setDragEndPosition(int dragEndPosition) {
        mDragEndPositionFlag = true;
        mDragEndPosition = dragEndPosition;
    }
}
