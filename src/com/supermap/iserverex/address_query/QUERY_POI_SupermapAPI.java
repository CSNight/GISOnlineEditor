package com.supermap.iserverex.address_query;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.supermap.iserverex.utils.CustomHttpRequest.sendGet;

public class QUERY_POI_SupermapAPI {
    public static String getDS(String address) {
        String serviceUrl = "http://www.supermapol.com/iserver/services/location-china/rest/locationanalyst/China/geocoding.json";
        String post_content = null;
        try {
            post_content = "address=" + URLEncoder.encode(address, "utf-8") + "&city=''&to=4326&maxResult=10&key=25z57SKSvaJFrVtIBOueScd1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONArray infos = new JSONArray();
        String result = sendGet(serviceUrl, post_content);
        if (!result.equals("")) {
            String res = "{result:" + result + "}";
            JSONObject jsonObject = JSONObject.fromObject(res);
            if (jsonObject.containsKey("result")) {
                JSONArray jo = jsonObject.getJSONArray("result");
                for (int i = 0; i < jo.size(); i++) {
                    JSONObject info = new JSONObject();
                    JSONObject poi = jo.getJSONObject(i);
                    String formatAddress = poi.getString("formatedAddress");
                    String confidence = poi.getString("confidence");
                    JSONObject location = poi.getJSONObject("location");
                    double lng = location.getDouble("x");
                    double lat = location.getDouble("y");
                    info.element("format_address", formatAddress);
                    info.element("confidence", confidence);
                    info.element("x", lng);
                    info.element("y", lat);
                    infos.add(info);
                }
            }
        }
        return infos.toString();
    }
}
