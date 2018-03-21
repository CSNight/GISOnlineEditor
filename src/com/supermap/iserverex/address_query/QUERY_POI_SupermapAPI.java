package com.supermap.iserverex.address_query;

import com.supermap.data.*;
import com.supermap.iserverex.operation.DATA_OP_BorderCheck;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.supermap.iserverex.utils.CustomHttpRequest.sendGet;

public class QUERY_POI_SupermapAPI {
    public static String getDS(String address, Workspace ws, DatasetVector dv) {
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
            String res = null;
            try {
                res = "{result:" + new String(result.getBytes("gbk"), "utf-8") + "}";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
                    info.element("relation", GetRelatedRegion(ws, dv, new Point2D(lng, lat)));
                    infos.add(info);
                }
            }
        }
        return infos.toString();
    }

    private static String GetRelatedRegion(Workspace ws, DatasetVector dv, Point2D poi) {
        try {
            //设置查询参数
            QueryParameter parameter = new QueryParameter();
            parameter.setSpatialQueryObject(poi);
            parameter.setSpatialQueryMode(SpatialQueryMode.WITHIN);
            parameter.setCursorType(CursorType.DYNAMIC);
            Recordset recordset = dv.query(parameter);
            if (recordset.getRecordCount() > 0) {
                return FeatureToJSONArray(recordset, dv).toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static JSONArray FeatureToJSONArray(Recordset rs, DatasetVector dv) {
        JSONArray joFeatArray = new JSONArray();
        try {
            while (!rs.isEOF()) {
                GeoRegion geoRegion = (GeoRegion) rs.getGeometry();
                List<GeoRegion> geoRegionList = new ArrayList<>();
                DATA_OP_BorderCheck.RegionDecompose(geoRegionList, geoRegion);
                for (GeoRegion gr : geoRegionList) {
                    JSONObject joFeat = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < gr.getPartCount(); i++) {
                        Point2Ds grPart = gr.getPart(i);
                        for (int j = 0; j < grPart.getCount(); j++) {
                            jsonArray.add(new double[]{grPart.getItem(j).getX(), grPart.getItem(j).y});
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
                }
                rs.moveNext();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return joFeatArray;
    }
}
