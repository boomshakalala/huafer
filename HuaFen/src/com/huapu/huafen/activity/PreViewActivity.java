package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.loopviewpager.Item;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/14.
 */

public class PreViewActivity extends BaseActivity {

    @BindView(R.id.myImage)
    ImageView myImage;
    @BindView(R.id.personPic)
    SimpleDraweeView personPic;
    @BindView(R.id.personName)
    TextView personName;
    @BindView(R.id.earnMoney)
    TextView earnMoney;
    @BindView(R.id.inputContent)
    TextView inputContent;
    @BindView(R.id.specialImage)
    ImageView specialImage;
    private View linearLayout;

    private int shareEarned;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_preview);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        linearLayout = findViewById(R.id.linearLayout);
        getTitleBar().
                setTitle("预览").
                setRightText("保存", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CommonUtils.saveImage(linearLayout, PreViewActivity.this, "temp.png");
                        toast("已保存到相册");
                    }
                });

        myImage.setImageResource(item.getImg());
//        personName.setText(item.getName());
        CommonTitleView commonTitleView = (CommonTitleView) findViewById(R.id.ctvName);
        TextView textView = (TextView) commonTitleView.findViewById(R.id.tvName);
        textView.setTextSize(14f);
        commonTitleView.setData(CommonPreference.getUserInfo());
        ImageLoader.resizeSmall(personPic, item.getUserPic(), 1);

        earnMoney.setText("我在花粉儿共赚了" + item.getShareEarned() + "元");
        if (TextUtils.isEmpty(item.getInputContent())) {
            inputContent.setVisibility(View.GONE);
        } else {
            inputContent.setText(item.getInputContent());
        }

//        switch (item.getUserLevel()) {
//            case 2:
//                specialImage.setVisibility(View.VISIBLE);
//                specialImage.setImageResource(R.drawable.icon_vip);
//                break;
//            case 3:
//                specialImage.setVisibility(View.VISIBLE);
//                specialImage.setImageResource(R.drawable.icon_xing);
//                break;
//            default:
//                specialImage.setVisibility(View.GONE);
//                break;
//        }

    }

    private void initData() {
        shareEarned = getIntent().getIntExtra("shareEarned", shareEarned);
        item = (Item) getIntent().getSerializableExtra("item");
    }

    private void saveImage(View view) {
        view.setDrawingCacheEnabled(true);
        try {
            String sdCardDir = this.getCacheDir().toString();
            File file = new File(sdCardDir, "temp.png");
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            view.getDrawingCache()
                    .compress(Bitmap.CompressFormat.PNG, 80, bos);
            bos.flush();
            bos.close();
            toast("已保存到相册");
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), "temp.png", "description");
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            this.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.setDrawingCacheEnabled(false);
    }


}
