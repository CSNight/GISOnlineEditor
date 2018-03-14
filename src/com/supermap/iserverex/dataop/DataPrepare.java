package com.supermap.iserverex.dataop;

import com.supermap.data.*;
import com.supermap.iserverex.utils.JSONUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPrepare {

    public Map<String, String> DataDispatch(String JSON) {
        JSONObject data = JSONObject.fromObject(JSON);
        Map<String, String> meta = new HashMap<>();
        meta.put("FeatureType", data.get("FeatureType").toString());
        meta.put("EditRole", data.get("EditRole").toString());
        meta.put("BelongsOrg", data.get("BelongsOrg").toString());
        //接口参数增加上级电子围栏数据集名称
        meta.put("TopClassDatasetName", data.get("TopClassDatasetName").toString());
        meta.put("Features", data.get("Features").toString());
        return meta;
    }

    public  List<Map<Object, Map<String, Object>>> datainsertBuild(
            String jsonElements, String geoType) {
        List<Map<Object, Map<String, Object>>> info = new ArrayList<>();
        try {
            JSONArray jsarray = JSONArray.fromObject(jsonElements);
            for (int i = 0; i < jsarray.size(); i++) {
                JSONArray jsgeo = jsarray.getJSONObject(i).getJSONArray(
                        "Geometry");
                JSONObject jsfield = jsarray.getJSONObject(i).getJSONObject(
                        "Fields");
                Map<Object, Map<String, Object>> featureinfo = new HashMap<>();
                Object geo = GeometryBuilder(jsgeo, geoType);
                Map<String, Object> fields = fieldparse(JSONUtil
                        .json2Map(jsfield.toString()));
                featureinfo.put(geo, fields);
                info.add(featureinfo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return info;
    }

    public Map<String, Map<Object, Map<String, Object>>> dataupdateBuild(
            String jsonElements, String geoType) {
        Map<String, Map<Object, Map<String, Object>>> info = new HashMap<>();
        JSONArray jsarray = JSONArray.fromObject(jsonElements);
        for (int i = 0; i < jsarray.size(); i++) {
            JSONArray jsgeo = JSONArray.fromObject(jsarray.getJSONObject(i)
                    .get("Geometry"));
            JSONObject jsfield = jsarray.getJSONObject(i).getJSONObject(
                    "Fields");
            Map<Object, Map<String, Object>> featureinfo = new HashMap<>();
            Object geo = GeometryBuilder(jsgeo, geoType);
            Map<String, Object> fields = fieldparse(JSONUtil.json2Map(jsfield
                    .toString()));
            featureinfo.put(geo, fields);
            info.put(jsarray.getJSONObject(i).get("FeatureID").toString(),
                    (featureinfo));
        }
        return info;
    }

    public List<String> datadeleteBuild(String jsonElements) {
        List<String> ids = new ArrayList<>();
        JSONArray jsarray = JSONArray.fromObject(jsonElements);
        for (int i = 0; i < jsarray.size(); i++) {
            ids.add(jsarray.getJSONObject(i).get("FeatureID").toString());
        }
        return ids;
    }

    private Object GeometryBuilder(JSONArray jsgeo, String geoType) {
        try {
            List<String[]> xys = parseCoordinate(jsgeo);
            switch (geoType) {
                case "Point":
                    if (xys.size() == 1) {
                        double x = Double.parseDouble(xys.get(0)[0]);
                        double y = Double.parseDouble(xys.get(0)[1]);
                        return new GeoPoint(x, y);
                    } else {
                        return null;
                    }
                case "Line":
                    if (xys.size() >= 2) {
                        Point2Ds p2s = new Point2Ds();
                        PointsArrayPush(xys, p2s);
                        return new GeoLine(p2s);
                    } else {
                        return null;
                    }
                default:
                    if (xys.size() >= 3) {
                        Point2Ds p2s = new Point2Ds();
                        PointsArrayPush(xys, p2s);
                        return new GeoRegion(p2s);
                    } else {
                        return null;
                    }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void PointsArrayPush(List<String[]> xys, Point2Ds p2s) {
        for (String[] xy : xys) {
            double x = Double.parseDouble(xy[0]);
            double y = Double.parseDouble(xy[1]);
            Point2D p2 = new Point2D(x, y);
            p2s.add(p2);
        }
    }

    private List<String[]> parseCoordinate(JSONArray jsgeo) {
        List<String[]> xys = new ArrayList<>();
        try {
            for (int i = 0; i < jsgeo.size(); i++) {
                JSONObject jsxy = jsgeo.getJSONObject(i);
                String[] xy = new String[]{jsxy.get("x").toString(),
                        jsxy.get("y").toString()};
                xys.add(xy);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return xys;
    }

    private Map<String, Object> fieldparse(Map<String, Object> fields) {
        Map<String, Object> newfield = new HashMap<>();
        for (String key : fields.keySet()) {
            if (key.toUpperCase().startsWith("SM")) {
                continue;
            }
            newfield.put(key, fields.get(key));
        }
        return newfield;
    }
}
