package com.supermap.iserverex.address_query;


import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.supermap.iserverex.utils.CoorTransform.Gps;
import static com.supermap.iserverex.utils.CoorTransform.bd09_To_Gps84;
import static com.supermap.iserverex.utils.CustomHttpRequest.sendGet;

public class QUERY_POI_BaiduAPI {
    private static String toQueryString(Map<?, ?> data) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey()).append("=");
            try {
                queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    private static String MD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String CalculateAKSN(String sk, Map<String, String> param) {
        try {
            String paramsStr = toQueryString(param);
            String wholeStr = "/geocoder/v2/?" + paramsStr + sk;
            String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
            return MD5(tempStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDS(String Address, boolean isContainGeo, DatasetVector dv, String ak, String sk) {
        String serviceUrl = "http://api.map.baidu.com/geocoder/v2/";
        String aks = ak.equals("") ? "ZeUEfsIRxiXoCKbVCCYyGtG3oATPf6hN" : ak;
        String sks = sk.equals("") ? "ZOVLVeb09KCS4Fnu8T85QVPgtGqOSk3S" : sk;
        Map<String, String> params_map = new LinkedHashMap<>();
        params_map.put("address", Address);
        params_map.put("ak", aks);
        params_map.put("output", "json");
        String sn = CalculateAKSN(sks, params_map);
        String post = toQueryString(params_map) + "&sn=" + sn;
        String result = sendGet(serviceUrl, post);
        if (!result.equals("")) {
            JSONObject jsonObject = JSONObject.fromObject(result);
            if (jsonObject.containsKey("result")) {
                JSONObject results = jsonObject.getJSONObject("result");
                if (results.containsKey("location")) {
                    JSONObject location = results.getJSONObject("location");
                    int confidence = results.getInt("confidence");
                    double lng = location.getDouble("lng");
                    double lat = location.getDouble("lat");
                    Gps GS = bd09_To_Gps84(lat, lng);
                    double new_lat = GS.getWgLat();
                    double new_lng = GS.getWgLon();
                    JSONObject xy = new JSONObject();
                    xy.element("x", new_lng);
                    xy.element("y", new_lat);
                    xy.element("confidence", confidence);
                    xy.element("relation", GetRelatedRegion(dv, isContainGeo, lonlat2mercator(new_lng, new_lat)));
                    return xy.toString();
                }
            }
        }
        return "";
    }

    private static Point2D lonlat2mercator(double x, double y) {
        double lng = x * 20037508.34 / 180;
        double lat = Math.log(Math.tan((90 + y) * Math.PI / 360)) / (Math.PI / 180);
        lat = lat * 20037508.34 / 180;
        return new Point2D(lng, lat);
    }

    private static String GetRelatedRegion(DatasetVector dv, boolean isContainGeo, Point2D poi) {
        try {
            //设置查询参数
            QueryParameter parameter = new QueryParameter();
            parameter.setSpatialQueryObject(poi);
            parameter.setSpatialQueryMode(SpatialQueryMode.WITHIN);
            parameter.setCursorType(CursorType.DYNAMIC);
            Recordset recordset = dv.query(parameter);
            if (recordset.getRecordCount() > 0) {
                return FeatureToJSONArray(recordset, isContainGeo).toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static JSONArray FeatureToJSONArray(Recordset rs, boolean isContainGeo) {
        JSONArray joFeatArray = new JSONArray();
        try {
            while (!rs.isEOF()) {
                if (isContainGeo) {
                    GeoRegion geoRegion = (GeoRegion) rs.getGeometry();
                    JSONObject joFeat = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    DecimalFormat decimalFormat = new DecimalFormat("############.00000000");
                    for (int i = 0; i < geoRegion.getPartCount(); i++) {
                        Point2Ds grPart = geoRegion.getPart(i);
                        for (int j = 0; j < grPart.getCount(); j++) {
                            jsonArray.add(new String[]{decimalFormat.format(grPart.getItem(j).getX()), decimalFormat.format(grPart.getItem(j).getY())});
                        }
                    }
                    joFeat.element("Geometry", jsonArray);
                    JSONObject joFields = new JSONObject();
                    Object[] field_values = rs.getValues();
                    FieldInfos field_names = rs.getFieldInfos();
                    for (int i = 0; i < field_names.getCount(); i++) {
                        joFields.element(field_names.get(i).getName(), field_values[i]);
                    }
                    joFeat.element("Fields", joFields);
                    joFeatArray.add(joFeat);
                } else {
                    JSONObject joFeat = new JSONObject();
                    JSONObject joFields = new JSONObject();
                    Object[] field_values = rs.getValues();
                    FieldInfos field_names = rs.getFieldInfos();
                    for (int i = 0; i < field_names.getCount(); i++) {
                        joFields.element(field_names.get(i).getName(), field_values[i]);
                    }
                    joFeat.element("Fields", joFields);
                    joFeatArray.add(joFeat);
                }
                rs.moveNext();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        rs.close();
        rs.dispose();
        return joFeatArray;
    }

    public static void RegionDecompose(List<GeoRegion> geoRegions, GeoRegion geoRegion) {
        GeoRegion[] geoRs = geoRegion.protectedDecompose();
        for (GeoRegion gr : geoRs) {
            if (gr.getPartCount() == 1) {
                geoRegions.add(gr);
            } else {
                RegionDecompose(geoRegions, gr);
            }
        }
    }
    public static void main(String [] args){
        Point2D SS=lonlat2mercator(104.08105,30.601048);
    }
}
