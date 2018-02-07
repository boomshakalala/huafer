package com.huapu.huafen.utils;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * 经纬度计算距离工具类
 */
public class DistanceUtil {
  /**
   * google maps的脚本里代码
   */
  private static double EARTH_RADIUS = 6378137;

  private static double rad(double d) {
    return d * Math.PI / 180.0;
  }

  /**
   * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
   */
  public static int GetDistance(double lng1, double lat1, double lng2, double lat2) {
    double radLat1 = rad(lat1);
    double radLat2 = rad(lat2);
    double a = radLat1 - radLat2;
    double b = rad(lng1) - rad(lng2);
    double s = 2 * asin(sqrt(pow(sin(a / 2), 2) +
      cos(radLat1) * cos(radLat2) * pow(sin(b / 2), 2)));
    s = s * EARTH_RADIUS;
    s = round(s);
    return Double.valueOf(s).intValue();
  }


}
