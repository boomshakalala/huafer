package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.PoiAddress;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;

import butterknife.BindView;
import butterknife.OnClick;


public class TagActivity extends BaseActivity {

    public final static int ARTICLE_TAG_GOODS = 1;
    public final static int ARTICLE_TAG_BRAND = ARTICLE_TAG_GOODS + 1;
    public final static int ARTICLE_TAG_LOCATION = ARTICLE_TAG_BRAND + 1;
    public final static int ARTICLE_TAG_OTHER = ARTICLE_TAG_LOCATION + 1;
    @BindView(R2.id.tv_pop_cargo)
    TextView tvPopCargo;
    @BindView(R2.id.rl_pop_cargo)
    RelativeLayout rlPopCargo;
    @BindView(R2.id.tv_pop_brand)
    TextView tvPopBrand;
    @BindView(R2.id.tv_pop_site)
    TextView tvPopSite;
    @BindView(R2.id.tv_pop_else)
    TextView tvPopElse;
    @BindView(R2.id.iv_close)
    ImageView ivClose;
    @BindView(R2.id.pop_layout)
    LinearLayout popLayout;

    private static final int REQUEST_CODE_FOR_BRANDS = 12;  //品牌页
    private static final int REQUEST_CODE_FOR_POI = 13; //地理位置
    private static final int REQUEST_CODE_FOR_ELSE = 14; //其他
    private static final int REQUEST_CODE_FOR_CARGO = 15; //商品
    private int[] location;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Transparent);
        setContentView(R.layout.tag_item_popupwindows);
        location = mIntent.getIntArrayExtra(MyConstants.LOCATION_X_Y);
        position = mIntent.getIntExtra(MyConstants.EXTRA_IMAGE_INDEX,-1);
    }

    @OnClick({R.id.rl_pop_cargo, R.id.tv_pop_brand, R.id.tv_pop_site, R.id.tv_pop_else, R.id.iv_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_pop_cargo:
                Intent intent = new Intent(TagActivity.this, GoodsListActivity.class);
                startActivityForResult(intent,REQUEST_CODE_FOR_CARGO);
                break;
            case R.id.tv_pop_brand:
                intent = new Intent(TagActivity.this, BrandListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_BRANDS);
                break;
            case R.id.tv_pop_site:
                intent = new Intent(TagActivity.this, PoiListActivity.class);
                startActivityForResult(intent,REQUEST_CODE_FOR_POI);
                break;
            case R.id.tv_pop_else:
                intent = new Intent(TagActivity.this, OtherTagActivity.class);
                startActivityForResult(intent,REQUEST_CODE_FOR_ELSE);
                break;
            case R.id.iv_close:
                finish();
                this.overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FOR_BRANDS:
                    if(data!=null){
                        Brand goodsBrand = (Brand) data.getSerializableExtra(MyConstants.BRAND_RESULT);
                        if (goodsBrand != null) {
                            String brand = goodsBrand.brandName;
                            FloridData.Tag tag = new FloridData.Tag();
                            tag.txt = brand;
                            String title ;
                            if(!TextUtils.isEmpty(brand) && brand.length() > 15){
                                title = brand.substring(0,15);
                            }else{
                                title = brand;
                            }
                            tag.title = title;
                            tag.type = ARTICLE_TAG_BRAND;
                            tvPopBrand.setText(brand);

                            Intent intent = new Intent();
                            intent.putExtra(MyConstants.ARTICLE_TAG,tag);
                            intent.putExtra(MyConstants.LOCATION_X_Y,location);
                            intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX,position);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    break;
                case REQUEST_CODE_FOR_POI:
                    if(data!=null){
                        PoiAddress bean = (PoiAddress) data.getSerializableExtra(MyConstants.POI_RESULT);
                        if(bean!=null){
                            Intent intent = new Intent();
                            FloridData.Tag tag = new FloridData.Tag();
                            tag.txt = bean.title;
                            String title ;
                            if(!TextUtils.isEmpty(bean.title) && bean.title.length() > 15){
                                title = bean.title.substring(0,15);
                            }else{
                                title = bean.title;
                            }
                            tag.title = title;
                            tag.type = ARTICLE_TAG_LOCATION;
                            tag.target = bean.poiId;
                            intent.putExtra(MyConstants.ARTICLE_TAG,tag);
                            intent.putExtra(MyConstants.LOCATION_X_Y,location);
                            intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX,position);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    break;
                case REQUEST_CODE_FOR_ELSE:
                    if(data!=null){
                        String content = data.getStringExtra(MyConstants.TAG_ELSE);
                        if(!TextUtils.isEmpty(content)){
                            Intent intent = new Intent();
                            FloridData.Tag tag = new FloridData.Tag();
                            tag.title = content;
                            tag.txt = tag.title;
                            tag.type = ARTICLE_TAG_OTHER;
                            intent.putExtra(MyConstants.ARTICLE_TAG,tag);
                            intent.putExtra(MyConstants.LOCATION_X_Y,location);
                            intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX,position);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    break;
                case REQUEST_CODE_FOR_CARGO:
                    if(data!=null){
                        GoodsData goodsData = (GoodsData) data.getSerializableExtra(MyConstants.EXTRA_GOODS_DATA);
                        if(goodsData!=null){

                            Intent intent = new Intent();
                            FloridData.Tag tag = new FloridData.Tag();
                            tag.title = goodsData.getName();
                            tag.txt = tag.title;
                            tag.type = ARTICLE_TAG_GOODS ;
                            tag.action = ActionConstants.OPEN_GOODS_DETAIL;
                            tag.target = String.valueOf(goodsData.getGoodsId());
                            intent.putExtra(MyConstants.ARTICLE_TAG,tag);
                            intent.putExtra(MyConstants.LOCATION_X_Y,location);
                            intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX,position);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
