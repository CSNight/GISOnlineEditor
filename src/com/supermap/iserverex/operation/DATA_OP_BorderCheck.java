package com.supermap.iserverex.operation;

import com.supermap.data.*;
import com.supermap.data.topology.TopologyValidator;
import com.supermap.iserverex.utils.JSONUtil;
import com.supermap.iserverex.utils.ResultMsg;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DATA_OP_BorderCheck {
    public static String BorderConfilctDetect(DatasetVector dv) {
        Workspace ws = dv.getDatasource().getWorkspace();
        Datasource ds = dv.getDatasource();
        DatasetVector topology_res_over = TopologyValidator.validate(dv, null, TopologyRule.REGION_NO_OVERLAP, 0.0001, null, ds, "topology_res_over");
        DatasetVector topology_res_gaps = TopologyValidator.validate(dv, null, TopologyRule.REGION_NO_GAPS, 0.0001, null, ds, "topology_res_gaps");
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setName("BelongsFeatID");
        fieldInfo.setCaption("BelongsFeatID");
        fieldInfo.setType(FieldType.WTEXT);
        fieldInfo.setMaxLength(200);
        fieldInfo.setDefaultValue("");
        topology_res_over.getFieldInfos().add(fieldInfo);
        topology_res_gaps.getFieldInfos().add(fieldInfo);
        ResultMsg res = new ResultMsg();
        Recordset rs_over = topology_res_over.getRecordset(false, CursorType.DYNAMIC);
        Recordset rs_gaps = topology_res_gaps.getRecordset(false, CursorType.DYNAMIC);
        JSONArray jsonArray_over = FeatureToJSONArray(rs_over);
        JSONArray jsonArray_gaps = FeatureToJSONArray(rs_gaps);
        jsonArray_over.addAll(jsonArray_gaps);
        rs_over.close();
        rs_over.dispose();
        rs_gaps.close();
        rs_gaps.dispose();
        res.setStatus(200);
        res.setNs_type("CheckConflicts");
        res.setMessage("success");
        res.setTotalCount(jsonArray_over.size());
        res.setElement(jsonArray_over.toString());
        ds.getDatasets().delete("topology_res_over");
        ds.getDatasets().delete("topology_res_gaps");
        ws.save();
        return JSONUtil.ConvertToString("json", res);
    }
    public static String BorderTopClassConflictDetect(DatasetVector dv,DatasetVector dvtop){
        Workspace ws = dv.getDatasource().getWorkspace();
        Datasource ds = dv.getDatasource();
        DatasetVector topology_res_over = TopologyValidator.validate(dv, dvtop, TopologyRule.REGION_NO_OVERLAP, 0.0001, null, ds, "topology_res_over");
        DatasetVector topology_res_gaps = TopologyValidator.validate(dv, dvtop, TopologyRule.REGION_NO_GAPS, 0.0001, null, ds, "topology_res_gaps");
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setName("BelongsFeatID");
        fieldInfo.setCaption("BelongsFeatID");
        fieldInfo.setType(FieldType.WTEXT);
        fieldInfo.setMaxLength(200);
        fieldInfo.setDefaultValue("");
        topology_res_over.getFieldInfos().add(fieldInfo);
        topology_res_gaps.getFieldInfos().add(fieldInfo);
        ResultMsg res = new ResultMsg();
        Recordset rs_over = topology_res_over.getRecordset(false, CursorType.DYNAMIC);
        Recordset rs_gaps = topology_res_gaps.getRecordset(false, CursorType.DYNAMIC);
        JSONArray jsonArray_over = FeatureToJSONArray(rs_over);
        JSONArray jsonArray_gaps = FeatureToJSONArray(rs_gaps);
        jsonArray_over.addAll(jsonArray_gaps);
        rs_over.close();
        rs_over.dispose();
        rs_gaps.close();
        rs_gaps.dispose();
        res.setStatus(200);
        res.setNs_type("CheckConflicts");
        res.setMessage("success");
        res.setTotalCount(jsonArray_over.size());
        res.setElement(jsonArray_over.toString());
        ds.getDatasets().delete("topology_res_over");
        ds.getDatasets().delete("topology_res_gaps");
        ws.save();
        return JSONUtil.ConvertToString("json", res);
    }
    private static JSONArray FeatureToJSONArray(Recordset rs) {
        JSONArray joFeatArray = new JSONArray();
        while (rs.isEOF()) {
            GeoRegion geoRegion = (GeoRegion) rs.getGeometry();
            List<GeoRegion> geoRegionList = new ArrayList<>();
            RegionDecompose(geoRegionList, geoRegion);
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
                joFields.element("BelongsA", rs.getFieldValue("ErrorObjectID1"));
                joFields.element("BelongsB", rs.getFieldValue("ErrorObjectID2"));
                joFields.element("BelongsFeatID", "");
                joFeat.element("Fields", joFields);
                joFeatArray.add(joFeat);
            }
            rs.moveNext();
        }
        return joFeatArray;
    }

    private static void RegionDecompose(List<GeoRegion> geoRegions, GeoRegion geoRegion) {
        GeoRegion[] geoRs = geoRegion.protectedDecompose();
        for (GeoRegion gr : geoRs) {
            if (gr.getPartCount() == 1) {
                geoRegions.add(gr);
            } else {
                RegionDecompose(geoRegions, gr);
            }
        }
    }
}
