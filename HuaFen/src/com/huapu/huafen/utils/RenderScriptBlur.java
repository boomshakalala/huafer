package com.huapu.huafen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Type;

/**
 * Created by admin on 2017/2/20.
 */

public class RenderScriptBlur {

    private RenderScript renderScript;

    public RenderScriptBlur(Context context) {
        this.renderScript = RenderScript.create(context);
    }

    /**
     * sync
     * 将图片高斯模糊化
     * @param radius 模糊半径，由于性能限制，这个值的取值区间为(0,25f]
     * @param bitmapOriginal 源Bitmap
     */
    public Bitmap blur(float radius, Bitmap bitmapOriginal) {
        if(radius<1f||radius>25f){
            throw new IllegalArgumentException(
                    "the param radius can not = "+radius+".The range is only (0,25f]");
        }
        Bitmap bmp = Bitmap.createBitmap(bitmapOriginal);
        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        final Allocation input = Allocation.createFromBitmap(renderScript, bmp);
        //Type: “一个Type描述了一个Allocation或者并行操作的Element和dimensions ”
        Type type = input.getType();
        final Allocation output = Allocation.createTyped(renderScript, type);
        //创建一个模糊效果的RenderScript的工具对象
        //第二个参数Element相当于一种像素处理的算法，高斯模糊的话用这个就好
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //设置渲染的模糊程度, 25f是最大模糊度
        script.setRadius(radius);
        // 设置blurScript对象的输入内存
        script.setInput(input);
        // 将输出数据保存到输出刚刚创建的输出内存中
        script.forEach(output);
        // 将数据填充到bitmap中
        output.copyTo(bmp);

        //销毁它们释放内存
        input.destroy();
        output.destroy();
        script.destroy();
        type.destroy();
        return bmp;
    }

    public void destory(){
        this.renderScript.destroy();
    }
}
