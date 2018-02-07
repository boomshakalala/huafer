package com.huapu.huafen.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

/**
 * Created by admin on 2016/12/14.
 */
public class GalleryUrlFragment extends BaseFragment implements View.OnClickListener {


    private GalleryViewPager mViewPager;
    private int position;
//    private Button btnLeft;
    private Button btnRightChecked;
    private int index;
    private List<String> items = new ArrayList<String>();
    private ArrayList<Integer> banners;
    private TextDialog dialog;


    public static GalleryUrlFragment newInstance(Bundle data){
        GalleryUrlFragment galleryUrlFragment = new GalleryUrlFragment();
        galleryUrlFragment.setArguments(data);
        return galleryUrlFragment;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_gallery_file,container,false);
    }

    private boolean isLongPress;
    @Override
    public void onViewCreated(View root) {

        Bundle bundle = getArguments();
        items = (List<String>) bundle.getSerializable("items");

        banners=(ArrayList<Integer>) bundle.getSerializable("banners");

        position = bundle.getInt("position",-1);

        initView(root);
        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(getActivity(), items);
        pagerAdapter.setOnItemChangeListener(new BasePagerAdapter.OnItemChangeListener() {
            @Override
            public void onItemChange(int currentPosition) {
                // Toast.makeText(GalleryUrlActivity.this, "Current item is " +
                // currentPosition, Toast.LENGTH_SHORT).show();
                position = currentPosition;
            }
        });

        pagerAdapter.setOnTouchLongClick(new UrlPagerAdapter.OnTouchLongClick() {

            @Override
            public void onLongClick() {
                isLongPress = true;
                dialog = new TextDialog(getActivity(),true);
                dialog.setContentText("是否要保存图片？");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                        isLongPress = false;

                    }
                });
                dialog.setRightText("确定");
                dialog.setRightCall(new DialogCallback() {
                    @Override
                    public void Click() {
                        dialog.dismiss();
                        btnRightChecked.performClick();
                        isLongPress = false;
                    }
                });
                dialog.show();
            }
        });

        pagerAdapter.setOnViewClick(new UrlPagerAdapter.OnViewClick() {

            @Override
            public void onClick() {
                if(dialog != null) {
                    if(dialog.isShowing()){
                        return;
                    }
                }
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }
        });


        mViewPager = (GalleryViewPager) root.findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        if (position != -1) {
            mViewPager.setCurrentItem(position);
        }
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Integer vPosition = banners.get(position);
                if(onIndicatorChangeListener!=null){
                    onIndicatorChangeListener.onChange(vPosition);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView(View root) {
        btnRightChecked = (Button) root.findViewById(R.id.btnRightChecked);
        btnRightChecked.setBackgroundResource(0);
        btnRightChecked.setText("保存");
        btnRightChecked.setVisibility(View.GONE);
        btnRightChecked.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btnLeft:
//                getActivity().finish();
//                break;
            case R.id.btnRightChecked:
                if(position < 0 || position > (items.size()-1)) {
                    return;
                }
                if(FileUtils.isSDCardExist()) {
                    ProgressDialog.showProgress(getActivity());
                    OkHttpClientManager.downloadAsync(items.get(position), FileUtils.getHDir(), System.currentTimeMillis() + ".jpg", new OkHttpClientManager.StringCallback() {

                        @Override
                        public void onError(Request request, Exception e) {
                            ToastUtil.toast(getActivity(), "图片保存失败");
                            ProgressDialog.closeProgress();
                        }

                        @Override
                        public void onResponse(String response) {
                            ProgressDialog.closeProgress();
                            ContentValues values = new ContentValues(7);
                            values.put(MediaStore.Images.Media.TITLE, response);
                            values.put(MediaStore.Images.Media.DISPLAY_NAME, response);
                            values.put(MediaStore.Images.Media.DATE_TAKEN,
                                    new Date().getTime());
                            values.put(MediaStore.Images.Media.MIME_TYPE,
                                    "image/jpeg");
                            values.put(MediaStore.Images.ImageColumns.BUCKET_ID,
                                    response.hashCode());
                            values.put(
                                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                    response);
                            values.put("_data", response);
                            ContentResolver contentResolver = MyApplication.getApplication().getContentResolver();
                            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            if(getActivity()!=null){
                                ToastUtil.toast(getActivity(), "图片保存成功");
                            }
                        }
                    });
                } else {
                    if(getActivity()!=null){
                        ToastUtil.toast(getActivity(), "sd卡不可用，无法保存");
                    }

                }
                break;
        }
    }



    public interface OnIndicatorChangeListener{
        void onChange(int position);
    }

    public void setOnIndicatorChangeListener(OnIndicatorChangeListener onIndicatorChangeListener){
        this.onIndicatorChangeListener = onIndicatorChangeListener;
    }

    private OnIndicatorChangeListener onIndicatorChangeListener;
}
