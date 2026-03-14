package com.wangwei.utils;

public class GeoUtils {

    private static final double EARTH_RADIUS = 6371000; // 地球半径(米)

    public static double distance(double lon1, double lat1, double lon2, double lat2) {

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        double deltaLat = radLat2 - radLat1;
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(radLat1) * Math.cos(radLat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}