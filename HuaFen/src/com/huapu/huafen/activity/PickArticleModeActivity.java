package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.SendSuccessEvent;
import com.huapu.huafen.utils.CommonPreference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 花语发布
 * Created by admin on 2017/4/27.
 */
public class PickArticleModeActivity extends BaseActivity {

    @BindView(R2.id.avatarBig)
    SimpleDraweeView avatarBig;
    @BindView(R2.id.mode1)
    SimpleDraweeView mode1;
    @BindView(R2.id.mode2)
    SimpleDraweeView mode2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_article_mode);
        EventBus.getDefault().register(this);
        avatarBig.setImageURI(CommonPreference.getUserInfo().getUserIcon());
        mode1.setOnClickListener(this);
        mode2.setOnClickListener(this);
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("花语发布").setLeftText("取消", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onSendSuccessEvent(SendSuccessEvent event) {
        if (null != event && event.isSuccess) {
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.mode1) {
            Intent intent = new Intent(this, ArticleCoverActivity.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.mode2) {
            Intent intent = new Intent(this, EasyArticleActivity.class);
            intent.putExtra("FROM_HOME", "FROM_HOME");
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
