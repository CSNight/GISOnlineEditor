package com.supermap.iserverex.operation;

import com.supermap.analyst.spatialanalyst.OverlayAnalyst;
import com.supermap.analyst.spatialanalyst.OverlayAnalystParameter;
import com.supermap.data.*;
import com.supermap.data.topology.TopologyValidator;

public class DATA_OP_BorderAutoBuild {
    public static GeoRegion BorderFix(GeoRegion geoRegion, DatasetVector dv) {
        Workspace ws = dv.getDatasource().getWorkspace();
        Datasource ds = dv.getDatasource();
        GeoRegion EraseGeo = BorderOverlapFix(geoRegion, dv, ds);
        ws.save();
        GeoRegion SeparateGeo = BorderSeparateFix(EraseGeo, dv, ds);
        ws.save();
        return SeparateGeo;
    }

    public static GeoRegion TopLayerBorderOverlapFix(GeoRegion geoRegion, DatasetVector dv, String TopClassDatasetName) {
        try {
            Workspace ws = dv.getDatasource().getWorkspace();
            Datasource ds = dv.getDatasource();
            DatasetVector dvtop = (DatasetVector) ds.getDatasets().get(TopClassDatasetName);
            DatasetVector dvtmp = BuildTempDataset(geoRegion, ws, ds);
            String resultDatasetName = ds.getDatasets().getAvailableDatasetName("resultDataset");
            DatasetVectorInfo datasetvectorInfo = new DatasetVectorInfo();
            datasetvectorInfo.setType(DatasetType.REGION);
            datasetvectorInfo.setName(resultDatasetName);
            DatasetVector resultDataset = ds.getDatasets().create(datasetvectorInfo);
            // 设置叠加分析参数
            OverlayAnalystParameter overlayAnalystParamErase = new OverlayAnalystParameter();
            overlayAnalystParamErase.setTolerance(0.0122055608);
            // 调用裁剪叠加分析方法实现裁剪分析
            OverlayAnalyst.erase(dvtmp, dvtop, resultDataset, overlayAnalystParamErase);
            Recordset rs = resultDataset.getRecordset(false, CursorType.DYNAMIC);
            GeoRegion region = (GeoRegion) rs.getGeometry().clone();
            rs.close();
            rs.dispose();
            ds.getDatasets().delete("TempErase");
            ds.getDatasets().delete(resultDatasetName);
            ws.save();
            return region;
        } catch (Exception e) {
            return null;
        }
    }

    private static GeoRegion BorderOverlapFix(GeoRegion geoRegion, DatasetVector dv, Datasource ds) {
        DatasetVector topology_res = TopologyValidator.validate(dv, null, TopologyRule.REGION_NO_OVERLAP, 0.0001, null, ds, "topology_res");
        Recordset rs = topology_res.getRecordset(false, CursorType.DYNAMIC);
        GeoRegion new_geo = geoRegion.clone();
        while (rs.isEOF()) {
            GeoRegion erase_part = (GeoRegion) rs.getGeometry().clone();
            GeoRegion after_erase = (GeoRegion) Geometrist.erase(new_geo, erase_part);
            new_geo.dispose();
            new_geo = after_erase.clone();
            rs.moveNext();
        }
        rs.close();
        rs.dispose();
        ds.getDatasets().delete("topology_res");
        return new_geo;
    }

    private static GeoRegion BorderSeparateFix(GeoRegion geoRegion, DatasetVector dv, Datasource ds) {
        DatasetVector topology_res = TopologyValidator.validate(dv, null, TopologyRule.REGION_NO_GAPS, 0.0001, null, ds, "topology_res");
        Recordset rs = topology_res.getRecordset(false, CursorType.DYNAMIC);
        GeoRegion new_geo = geoRegion.clone();
        while (rs.isEOF()) {
            GeoRegion union_part = (GeoRegion) rs.getGeometry().clone();
            GeoRegion after_union = (GeoRegion) Geometrist.union(new_geo, union_part);
            new_geo.dispose();
            new_geo = after_union.clone();
            rs.moveNext();
        }
        rs.close();
        rs.dispose();
        ds.getDatasets().delete("topology_res");
        return new_geo;
    }

    private static DatasetVector BuildTempDataset(GeoRegion geoRegion, Workspace ws, Datasource ds) {
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
        Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
        rs.addNew(geoRegion);
        rs.update();
        rs.close();
        rs.dispose();
        ws.save();
        return (DatasetVector) ds.getDatasets().get("TempErase");
    }
}
