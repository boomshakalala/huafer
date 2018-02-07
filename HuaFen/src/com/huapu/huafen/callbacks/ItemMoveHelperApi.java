package com.huapu.huafen.callbacks;

/**
 * Created by admin on 2017/3/9.
 */

public interface ItemMoveHelperApi {
    /**
     * Item 切换位置
     *
     * @param fromPosition 开始位置
     * @param toPosition   结束位置
     */
    void onItemMoved(int fromPosition, int toPosition);

    /**
     * 开始移动
     */
    void onMoveStart();
    /**
     * 停止移动
     */
    void onMoveEnd();
}
