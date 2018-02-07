
package twodimencode.view;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;


public final class ViewfinderResultPointCallback implements ResultPointCallback
{
    /**
     * 相机依赖的view
     */
    private final ViewfinderView viewfinderView;

    public ViewfinderResultPointCallback(ViewfinderView viewfinderView)
    {
        this.viewfinderView = viewfinderView;
    }

    public void foundPossibleResultPoint(ResultPoint point)
    {
        /**
         * 将可能点添加到存放可能点的数组中
         */
        viewfinderView.addPossibleResultPoint(point);
    }

}
