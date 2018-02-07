package com.huapu.huafen.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.dialog.ProgressDialog;

/**
 * Created by lalo on 2016/9/25.
 */
public class LocationHelper {

    private static final AMapLocationClient onceClient = new AMapLocationClient(MyApplication.getApplication().getApplicationContext());

    static {
        AMapLocationClientOption  locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setOnceLocation(true);
        onceClient.setLocationOption(locationClientOption);
    }


    public static void startLocation(final OnLocationListener listener, final Context mContext, final boolean loading){
        onceClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                ProgressDialog.closeProgress();
                if(aMapLocation!=null){
                    if(aMapLocation.getErrorCode() == 0){
                        LocationData locationData = getLocationData(aMapLocation);
                        if(listener!=null){
                            listener.onLocationComplete(locationData);
                        }
                    }else{
                        if(listener!=null){
                            listener.onLocationFailed();
                        }
                    }
                }else{
                    if(listener!=null){
                        listener.onLocationFailed();
                    }
                }
            }
        });
        if(loading) {
            ProgressDialog.showProgress(mContext);
        }
        onceClient.startLocation();
    }

    @NonNull
    private static LocationData getLocationData(AMapLocation aMapLocation) {
        LocationData locationData = new LocationData();
        locationData.country = aMapLocation.getCountry();
        locationData.province = aMapLocation.getProvince();
        locationData.city = aMapLocation.getCity();
        locationData.district = aMapLocation.getDistrict();
        locationData.gLat = aMapLocation.getLatitude();
        locationData.gLng = aMapLocation.getLongitude();
        locationData.cityCode = aMapLocation.getCityCode();
        return locationData;
    }


    public static void startLocation(final OnLocationListener listener){

        onceClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation!=null){
                    if(aMapLocation.getErrorCode() == 0){
                        LocationData locationData = getLocationData(aMapLocation);
                        if(listener!=null){
                            listener.onLocationComplete(locationData);
                        }
                    }else{
                        if(listener!=null){
                            listener.onLocationFailed();
                        }
                    }
                }else{
                    if(listener!=null){
                        listener.onLocationFailed();
                    }
                }
            }
        });

        onceClient.startLocation();
    }

    public interface OnLocationListener{
        void onLocationComplete(LocationData locationData);
        void onLocationFailed();
    }

}
