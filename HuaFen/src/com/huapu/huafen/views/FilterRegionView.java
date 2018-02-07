package com.huapu.huafen.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.FilterAdapter;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lalo on 2016/11/14.
 */
public class FilterRegionView extends FrameLayout {

    private RecyclerView recyclerViewProvince;
    private RecyclerView recyclerViewCity;
    private RecyclerView recyclerViewArea;

    private FilterAdapter provinceAdapter;
    private FilterAdapter cityAdapter;
    private FilterAdapter areaAdapter;

    private int provinceIndex = -1;
    private int cityIndex = -1;
    private int areaIndex = -1;

    private int screenWidth;
    private boolean isDC;
    private FilterAreaData currentCity;
    private FilterAreaData currentArea;
    private boolean isLocationCity;

    private TextView cityLocation;

    private TextView sameCity;
    private FilterAreaData locationAreaData;
    private int[] locationResult = new int[3];
    private boolean animatingCity;
    private boolean animatingArea;
    private OnDismissListener listener;
    private OnItemDataSelect mOnItemDataSelect;
    private OnItemDataSelect1 mOnItemDataSelect1;
    private RelativeLayout sameCityLayout;

    private String city;
    private String distact;

    public FilterRegionView(Context context) {
        this(context, null);
    }

    public FilterRegionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = CommonUtils.getScreenWidth();
        init();
    }

    public void needSameCity(boolean needSameCity) {
        sameCityLayout.setVisibility(needSameCity ? VISIBLE : GONE);
    }

    private void initLocation() {
        LocationData locationData = CommonPreference.getLocalData();
        if (locationData != null) {
            cityLocation.setText(locationData.city + " " + locationData.district);
        } else {
            sameCityLayout.setVisibility(View.GONE);
        }
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_filter_area, this, true);

        LinearLayoutManager recyclerViewProvinceManager = new LinearLayoutManager(getContext());
        recyclerViewProvinceManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewProvince = (RecyclerView) findViewById(R.id.recyclerViewProvince);
        recyclerViewProvince.setLayoutManager(recyclerViewProvinceManager);

        LinearLayoutManager recyclerViewCityManager = new LinearLayoutManager(getContext());
        recyclerViewCityManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewCity = (RecyclerView) findViewById(R.id.recyclerViewCity);
        recyclerViewCity.setLayoutManager(recyclerViewCityManager);

        LinearLayoutManager recyclerViewAreaCityManager = new LinearLayoutManager(getContext());
        recyclerViewAreaCityManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewArea = (RecyclerView) findViewById(R.id.recyclerViewArea);
        recyclerViewArea.setLayoutManager(recyclerViewAreaCityManager);

        provinceAdapter = new FilterAdapter(getContext());

        cityAdapter = new FilterAdapter(getContext());
        areaAdapter = new FilterAdapter(getContext());
        areaAdapter.isNeedsUnderLine = true;

        final int[] cityColors = new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF")};
        int[] areaColors = new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF")};

        recyclerViewProvince.setAdapter(provinceAdapter);
        recyclerViewCity.setAdapter(cityAdapter);
        recyclerViewArea.setAdapter(areaAdapter);

        cityAdapter.setItemColors(cityColors);
        areaAdapter.setItemColors(areaColors);
        areaAdapter.isNeedSetItemBackground = false;


        FrameLayout.LayoutParams cityParams = (FrameLayout.LayoutParams) recyclerViewCity.getLayoutParams();
        cityParams.width = screenWidth * 2 / 3;
        recyclerViewCity.setLayoutParams(cityParams);

        FrameLayout.LayoutParams areaParam = (FrameLayout.LayoutParams) recyclerViewArea.getLayoutParams();
        areaParam.width = screenWidth / 3;
        recyclerViewArea.setLayoutParams(areaParam);

        provinceAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(FilterAreaData data, int position) {
                provinceAdapter.setCheckItemByPosition(position);
                boolean notNeedsShowAnimation = data.getDid() == 0;
                if (currentCity != null) {
                    currentCity.isCheck = false;
                }

                if (currentArea != null) {
                    currentArea.isCheck = false;
                }
                if (!notNeedsShowAnimation) {
                    isLocationCity = data.isLocationCity;
                    if (isLocationCity) {//如果是定位城市
                        provinceAdapter.filterId = data.provinceDid;
                        cityAdapter.filterId = data.getDid();
                    } else {
                        provinceAdapter.filterId = data.getDid();
                        cityAdapter.filterId = 0;
                    }
                    provinceAdapter.isNeedSetItemBackground = true;
                    provinceAdapter.showTips = true;
                    ArrayList<FilterAreaData> list;
                    if (data.getDc() == 1) {
                        isDC = true;
                        list = data.getDistricts();
                    } else {
                        isDC = false;
                        list = data.getCities();
                    }
                    cityAdapter.setData(list);

                    if (recyclerViewCity.getVisibility() == View.GONE) {
                        startCityAnimation();
                    } else {
                        if (recyclerViewArea.getVisibility() == View.VISIBLE) {
                            startCityAnimation();
                        }
                    }

                    if (isDC) {
                        city = data.getName();
                    }

                } else {//全国
                    isLocationCity = false;
                    provinceIndex = position;
                    cityIndex = -1;
                    areaIndex = -1;

                    provinceAdapter.isNeedSetItemBackground = false;
                    provinceAdapter.showTips = true;
                    provinceAdapter.filterId = 0;
                    cityAdapter.filterId = 0;
                    int[] result = new int[3];
                    if (mOnItemDataSelect != null) {
                        mOnItemDataSelect.onDataSelected(result, data.getName());
                    }

                    if (listener != null) {
                        listener.close();
                    }
                }

            }
        });

        cityAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(FilterAreaData data, int position) {

                if (currentArea != null) {
                    currentArea.isCheck = false;
                }
                if (currentCity != null) {
                    currentCity.isCheck = false;
                }

                currentCity = data;

                cityAdapter.setCheckItemByPosition(position);

                if (!isDC && !isLocationCity && data.getDid() > 0) {
                    cityAdapter.filterId = data.getDid();
                    cityAdapter.isNeedSetItemBackground = true;
                    areaAdapter.setData(data.getDistricts());
                    if (recyclerViewArea.getVisibility() == View.GONE) {
                        startAreaAnimation();
                        city = data.getName();
                    }

                } else {//dc或定位城市
                    provinceIndex = provinceAdapter.currentIndex;
                    cityIndex = position;
                    areaIndex = -1;

                    provinceAdapter.isNeedSetItemBackground = true;
                    cityAdapter.isNeedSetItemBackground = false;

                    int[] result = new int[3];

                    if (isLocationCity) {
                        result[0] = provinceAdapter.filterId;
                        result[1] = cityAdapter.filterId;
                        result[2] = data.getDid();
                    } else {
                        if (isDC) {
                            result[1] = provinceAdapter.filterId;
                            result[2] = data.getDid();
                        } else if (data.getDid() == 0) {//全省
                            result[0] = provinceAdapter.filterId;
                        }
                    }

                    distact = data.getName();

                    if (mOnItemDataSelect != null) {
                        mOnItemDataSelect.onDataSelected(result, data.getName());
                    }

                    if (mOnItemDataSelect1 != null) {
                        mOnItemDataSelect1.onDataSelected(result, city + " " + distact);
                    }


                    if (listener != null) {
                        listener.close();
                    }
                }
            }
        });

        areaAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(FilterAreaData data, int position) {
                if (currentArea != null) {
                    currentArea.isCheck = false;
                }
                currentArea = data;

                provinceIndex = provinceAdapter.currentIndex;
                cityIndex = cityAdapter.currentIndex;
                areaIndex = position;

                provinceAdapter.isNeedSetItemBackground = true;
                cityAdapter.isNeedSetItemBackground = true;
                areaAdapter.isNeedSetItemBackground = false;

                int[] result = new int[3];
                result[0] = provinceAdapter.filterId;
                result[1] = cityAdapter.filterId;
                result[2] = data.getDid();

                distact = data.getName();

                Logger.e("get data:" + provinceAdapter.filterId + "city:" + cityAdapter.filterId + "area:" + data.getDid());
                if (mOnItemDataSelect != null) {
                    mOnItemDataSelect.onDataSelected(result, data.getName());
                }

                if (mOnItemDataSelect1 != null) {
                    mOnItemDataSelect1.onDataSelected(result, city + " " + distact);
                }


                if (listener != null) {
                    listener.close();
                }
            }
        });

        cityLocation = (TextView) findViewById(R.id.cityLocation);

        sameCityLayout = (RelativeLayout) findViewById(R.id.sameCityLayout);

        sameCityLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sameCity = (TextView) findViewById(R.id.sameCity);
        sameCity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemDataSelect != null) {
                    for (int i : locationResult) {
                        Logger.e("get data:" + i);
                    }
                    mOnItemDataSelect.onDataSelected(locationResult, "同城");
                    cityIndex = -1;
                    areaIndex = -1;
                    provinceIndex = -1;
                }
            }
        });
        initLocation();

    }

    public void setData(List<FilterAreaData> filterAreaData) {
        provinceAdapter.setData(filterAreaData);
        locationAreaData = CommonUtils.findLocationCityByFilterAreaData(filterAreaData);
        if (null == locationAreaData) {
            return;
        }
        locationResult[0] = locationAreaData.provinceDid;
        locationResult[1] = locationAreaData.getDid();
        locationResult[2] = 0;

    }

    private void startCityAnimation() {
        if (animatingCity) {
            return;
        }
        recyclerViewCity.setVisibility(GONE);
        recyclerViewArea.setVisibility(GONE);

        recyclerViewCity.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(recyclerViewCity, "translationX", screenWidth, 0);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                animatingCity = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatingCity = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);
        animator.start();
    }

    private void startAreaAnimation() {
        if (animatingArea) {
            return;
        }
        recyclerViewArea.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(recyclerViewArea, "translationX", screenWidth, 0);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                animatingArea = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatingArea = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);
        animator.start();
    }

    public void setDefaultCheck(int... params) {
        if (params == null || params.length != 3) {
            return;
        }
        long firstId = params[0];
        long secondId = params[1];
        long thirdId = params[2];
        int count = provinceAdapter.getItemCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                long id = provinceAdapter.getItemId(i);
                if (id == firstId) {
                    provinceIndex = i;
                    break;
                }
            }
        }

        FilterAreaData provinceData = provinceAdapter.getItem(provinceIndex);
        List<FilterAreaData> list;
        if (provinceData == null) {
            return;
        }
        if (provinceData.getDc() == 1) {
            list = provinceData.getDistricts();
        } else {
            list = provinceData.getCities();
        }
        if (!ArrayUtil.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                FilterAreaData fd = list.get(i);
                if (fd.getDid() == secondId) {
                    cityIndex = i;
                    break;
                }
            }
        }

        FilterAreaData cityData = list.get(cityIndex);
        if (cityData == null) {
            return;
        }

        if (!ArrayUtil.isEmpty(cityData.getDistricts())) {
            ArrayList<FilterAreaData> districts = cityData.getDistricts();
            for (int i = 0; i < districts.size(); i++) {
                FilterAreaData fd = districts.get(i);
                if (fd.getDid() == thirdId) {
                    areaIndex = i;
                    break;
                }
            }
        }


    }

    public void resetState() {
        if (cityIndex == -1) {
            recyclerViewCity.setVisibility(View.GONE);
            recyclerViewArea.setVisibility(View.GONE);
        } else {
            if (areaIndex == -1) {
                recyclerViewCity.setVisibility(View.VISIBLE);
                recyclerViewArea.setVisibility(View.GONE);
            } else {
                recyclerViewCity.setVisibility(View.VISIBLE);
                recyclerViewArea.setVisibility(View.VISIBLE);
            }
        }
        isLocationCity = false;
        provinceAdapter.filterId = 0;
        cityAdapter.filterId = 0;

        provinceAdapter.setCheckItemByPosition(provinceIndex);
        FilterAreaData item = provinceAdapter.getItem(provinceIndex);

        if (item != null) {
            isLocationCity = item.isLocationCity;
            if (item.getDid() == 0) {
                provinceAdapter.isNeedSetItemBackground = false;
            } else {
                provinceAdapter.isNeedSetItemBackground = true;
            }
        }

        isDC = provinceAdapter.isDC(provinceIndex);

        if (isDC) {
            if (item != null) {
                ArrayList<FilterAreaData> data = item.getDistricts();
                cityAdapter.setData(data);
            }
        } else {
            if (item != null) {
                ArrayList<FilterAreaData> data = item.getCities();
                cityAdapter.setData(data);
            }
        }
        cityAdapter.setCheckItemByPosition(cityIndex);
        FilterAreaData data = cityAdapter.getItem(cityIndex);

        if (isLocationCity) {
            provinceAdapter.filterId = item.provinceDid;
            cityAdapter.filterId = item.getDid();
        } else if (isDC) {
            provinceAdapter.filterId = item.getDid();
        } else {
            if (item != null) {
                provinceAdapter.filterId = item.getDid();
            }

            if (item != null && data != null) {
                provinceAdapter.filterId = item.getDid();
                cityAdapter.filterId = data.getDid();
            }
        }

        if (data != null) {
            areaAdapter.setData(data.getDistricts());
            if (data.getDid() == 0 || isDC) {
                cityAdapter.isNeedSetItemBackground = false;
            } else {
                cityAdapter.isNeedSetItemBackground = true;
            }
        }
        areaAdapter.setCheckItemByPosition(areaIndex);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    public void setOnItemDataSelect(OnItemDataSelect onItemDataSelect) {
        this.mOnItemDataSelect = onItemDataSelect;
    }

    public void setOnItemDataSelect1(OnItemDataSelect1 onItemDataSelect1) {
        this.mOnItemDataSelect1 = onItemDataSelect1;
    }

    public interface OnDismissListener {
        void close();
    }

    public interface OnItemDataSelect {
        void onDataSelected(int[] result, String text);
    }

    public interface OnItemDataSelect1 {
        void onDataSelected(int[] result, String text);
    }


}