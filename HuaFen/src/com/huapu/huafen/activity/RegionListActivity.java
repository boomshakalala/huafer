package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.FilterRegionView;
import java.util.List;
import butterknife.BindView;

/**
 * Created by mac on 17/8/16.
 */

public class RegionListActivity extends BaseActivity {

    private final static int COVERT_REGIONS = 0x4321;
    @BindView(R.id.filterRegionView)
    FilterRegionView filterRegionView;


    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == COVERT_REGIONS) {
                List<FilterAreaData> data = (List<FilterAreaData>) msg.obj;
                filterRegionView.setData(data);
            }
            return true;
        }
    });

    @Override
    public void initTitleBar() {
        setTitleString("城市地区");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_list);
        //地区
        filterRegionView.setOnItemDataSelect1(new FilterRegionView.OnItemDataSelect1() {

            @Override
            public void onDataSelected(int[] result, String text) {
                LogUtil.e("onDataSelected", "(" + result[0] + "," + result[1] + "," + result[2] + ")");
                Intent intent = new Intent();
                intent.putExtra(MyConstants.EXTRA_SELECT_CITY_ID,result[1]);
                intent.putExtra(MyConstants.EXTRA_SELECT_DISTRICT_ID,result[2]);
                intent.putExtra(MyConstants.EXTRA_CHOOSE_CITYNAME,text);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        filterRegionView.needSameCity(false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<FilterAreaData> data = CommonUtils.covertRegionsWithoutQuan();
                Message msg = Message.obtain();
                msg.what = COVERT_REGIONS;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
