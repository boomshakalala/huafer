package com.huapu.huafen.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.PushMessage;


/**
 * Created by admin on 2016/10/15.
 */
public class MessageController {

    private final static String TAG = MessageController.class.getSimpleName();
//    private volatile static MessageController mInstance;
//
//    private MessageController(){
//
//    }
//
//    public static MessageController getInstance(){
//        if(mInstance==null){
//            synchronized (MessageController.class){
//                if(mInstance==null){
//                    mInstance = new MessageController();
//                }
//            }
//        }
//        return mInstance;
//    }

    public static void dispatchMessage(Context context,String extraMap){
        try {
            PushMessage msg = JSON.parseObject(extraMap, PushMessage.class);
            if(msg!=null){
                ActionUtil.dispatchAction(context, msg.getAction(), msg.getTarget(), msg.getType());
            }
        }catch (Exception e){
            LogUtil.e(TAG,e.getMessage());
        }
    }


}
