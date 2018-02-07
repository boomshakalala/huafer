package cn.leancloud.chatkit.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;

import cn.leancloud.chatkit.R;
import cn.leancloud.chatkit.utils.LCIMConstants;

/**
 * Created by wli on 16/2/29.
 * 图片详情页，聊天时点击图片则会跳转到此页面
 */
public class LCIMImageActivity extends AppCompatActivity {
  private String TAG = "LCIMImageActivity";
  private SimpleDraweeView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.lcim_chat_image_brower_layout);
    imageView = (SimpleDraweeView) findViewById(R.id.item_image);
    Intent intent = getIntent();
    String path = intent.getStringExtra(LCIMConstants.IMAGE_LOCAL_PATH);
    String url = intent.getStringExtra(LCIMConstants.IMAGE_URL);
    Log.i(TAG, "localFilePath:" + path + "    getFileUrl:" +url);
    if (!TextUtils.isEmpty(path)) {
      Uri uri = Uri.parse("file://" + path);
      imageView.setImageURI(uri);
    } else if (!TextUtils.isEmpty(url)) {
      imageView.setImageURI(url);
    }

  }
}
