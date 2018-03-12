package com.supermap.iserverex.dataop;

import com.supermap.analyst.spatialanalyst.OverlayAnalyst;
import com.supermap.analyst.spatialanalyst.OverlayAnalystParameter;
import com.supermap.data.*;

import java.util.HashMap;
import java.util.Map;

public class BorderAutoModify {
    public static Map<String, Map<Object, Map<String, Object>>> BorderConfilctDetect(
            Map<String, Map<Object, Map<String, Object>>> infos, DatasetVector dverase) {
        Workspace ws = dverase.getDatasource().getWorkspace();
        Datasource ds = dverase.getDatasource();
        DatasetVector dvtmp = BuildTempDataset(infos, ws, ds);
        String resultDatasetName = ds.getDatasets().getAvailableDatasetName("resultDataset");
        DatasetVectorInfo datasetvectorInfo = new DatasetVectorInfo();
        datasetvectorInfo.setType(DatasetType.REGION);
        datasetvectorInfo.setName(resultDatasetName);
        DatasetVector resultDataset = ds.getDatasets().create(datasetvectorInfo);
        // 设置叠加分析参数
        OverlayAnalystParameter overlayAnalystParamErase = new OverlayAnalystParameter();
        overlayAnalystParamErase.setTolerance(0.0122055608);
        // 调用裁剪叠加分析方法实现裁剪分析
        OverlayAnalyst.erase(dvtmp, dverase, resultDataset, overlayAnalystParamErase);
        ws.save();
        Map<String, Map<Object, Map<String, Object>>> new_infos = new HashMap<>();
        Recordset rs = resultDataset.getRecordset(false, CursorType.DYNAMIC);
        while (rs.isEOF()) {
            String id = rs.getFieldValue("FeatGuid").toString();
            for (String key : infos.keySet()) {
                if (id.equals(key)) {
                    Map<Object, Map<String, Object>> new_feat = new HashMap<>();
                    Object old_g = infos.get(key).keySet().toArray()[0];
                    Map<String, Object> fields = infos.get(key).get(old_g);
                    new_feat.put(rs.getGeometry(), fields);
                    new_infos.put(id, new_feat);
                }
            }
            rs.moveNext();
        }
        return new_infos;
    }

    private static DatasetVector BuildTempDataset(Map<String, Map<Object, Map<String, Object>>> infos, Workspace ws, Datasource ds) {
        Datasets datasets = ds.getDatasets();
        DatasetVectorInfo datasetvectorInfoIdentity = new DatasetVectorInfo();
        datasetvectorInfoIdentity.setType(DatasetType.REGION);
        datasetvectorInfoIdentity.setName("TempErase");
        if (!datasets.isAvailableDatasetName("TempErase")) {
            datasets.delete("TempErase");
        }
        DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
        datasetVectorInfo.setType(DatasetType.REGION);
        datasetVectorInfo.setName("TempErase");
        DatasetVector dv = datasets.create(datasetVectorInfo);
        FieldInfos fieldInfos = dv.getFieldInfos();
        // 新建字段，添加到字段信息集合
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setName("FeatGuid");
        fieldInfo.setCaption("FeatGuid");
        fieldInfo.setType(FieldType.WTEXT);
        fieldInfo.setRequired(false);
        fieldInfos.add(fieldInfo);
        Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
        for (String key : infos.keySet()) {
            Map<Object, Map<String, Object>> feature = infos.get(key);
            Object gr = feature.keySet().toArray()[0];
            Map<String, Object> fie = new HashMap<>();
            fie.put("FeatGuid", key);
            rs.addNew((GeoRegion) gr, fie);
            rs.update();
        }
        ws.save();
        return (DatasetVector) ds.getDatasets().get("TempErase");
    }
}
