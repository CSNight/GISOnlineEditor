package com.supermap.iserverex.address_query;


import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.supermap.iserverex.utils.CoorTransform.Gps;
import static com.supermap.iserverex.utils.CoorTransform.bd09_To_Gps84;
import static com.supermap.iserverex.utils.CustomHttpRequest.sendPost;

public class QUERY_ADD_BaiduAPI {
    private String toQueryString(Map<?, ?> data) {
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

    private String MD5(String md5) {
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

    private String CalculateAKSN(String sk, Map<String, String> param) {
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

    public String getDS(String Address) {
        String serviceUrl = "http://api.map.baidu.com/geocoder/v2/";
        String ak = "ZeUEfsIRxiXoCKbVCCYyGtG3oATPf6hN";
        String sk = "ZOVLVeb09KCS4Fnu8T85QVPgtGqOSk3S";
        Map<String, String> params_map = new LinkedHashMap<>();
        params_map.put("address", Address);
        params_map.put("ak", ak);
        params_map.put("output", "json");
        String sn = CalculateAKSN(sk, params_map);
        String post = toQueryString(params_map) + "&sn=" + sn;
        String result = sendPost(serviceUrl, post);
        if (!result.equals("")) {
            JSONObject jsonObject = JSONObject.fromObject(result);
            if (jsonObject.containsKey("result")) {
                JSONObject results = jsonObject.getJSONObject("result");
                if (results.containsKey("location")) {
                    JSONObject location = results.getJSONObject("location");
                    double lng = location.getDouble("lng");
                    double lat = location.getDouble("lat");
                    Gps GS = bd09_To_Gps84(lat, lng);
                    double new_lat = GS.getWgLat();
                    double new_lng = GS.getWgLon();
                    JSONObject xy = new JSONObject();
                    xy.element("x", new_lng);
                    xy.element("y", new_lat);
                    return xy.toString();
                }
            }
        }
        return "";
    }
}
