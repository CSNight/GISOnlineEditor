package com.supermap.iserverex.operation;

import com.supermap.data.*;
import com.supermap.iserverex.logmanage.DB_LOG_FeatureLogWithBLOBs;
import com.supermap.iserverex.logmanage.DB_LOG_Handler;
import com.supermap.iserverex.utils.GUID;
import com.supermap.iserverex.utils.JSONUtil;
import com.supermap.iserverex.utils.ResultMsg;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DATA_OP_DataModify {

    public String addFeature(DatasetVector dv, Map<String, String> meta,
                             List<Map<Object, Map<String, Object>>> infos, String ServerName,
                             String DatasetName) {
        int successcount = 0;
        int misscount = 0;
        ResultMsg res = new ResultMsg();
        try {
            meta.put("server", ServerName);
            WorkspaceConnectionInfo wc = dv.getDatasource().getWorkspace().getConnectionInfo();
            DatasourceConnectionInfo dc = dv.getDatasource().getConnectionInfo();
            meta.put("workspace", wc.getName());
            meta.put("datasource", dc.getServer());
            meta.put("dataset", DatasetName);
            meta.put("optype", "add");
            JSONObject jselements = new JSONObject();
            JSONArray jsastatus = new JSONArray();
            String TopClassDatasetName = meta.get("TopClassDatasetName");
            Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
            for (int i = 0; i < infos.size(); i++) {
                if (meta.get("FeatureType").equals("Region")) {
                    Map<Object, Map<String, Object>> feature = infos.get(i);
                    Object geo = feature.keySet().toArray()[0];
                    GeoRegion new_geo = DATA_OP_BorderAutoBuild.TopLayerBorderOverlapFix((GeoRegion) geo, dv, TopClassDatasetName);
                    boolean addres = false;
                    if (new_geo != null) {
                        addres = rs.addNew(new_geo, feature.get(geo));
                    } else {
                        addres = rs.addNew((GeoRegion) geo, feature.get(geo));
                    }
                    rs.update();
                    rs.edit();
                    rs.setGeometry(DATA_OP_BorderAutoBuild.BorderFix((GeoRegion) geo, dv));
                    rs.update();
                    meta.put("featureid", rs.getID() + "");
                    byte[] new_f = featureTobyte(rs.getFeature(), feature.get(geo).keySet().toArray(), rs.getID());
                    oplogbuild(meta, new byte[]{}, new_f);
                    if (addres) {
                        successcount++;
                    } else {
                        misscount++;
                    }
                    jsastatus.add(FeatureOpstatus(addres, rs.getID()));
                } else {
                    misscount++;
                    jsastatus.add(FeatureOpstatus(false, rs.getID()));
                }
            }
            jselements.element("opStatus", jsastatus);
            res.setStatus(200);
            res.setNs_type("addFeature");
            res.setMessage("success");
            res.setTotalCount(misscount + successcount);
            res.setElement(jselements.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            res.setStatus(400);
            res.setNs_type("addFeature");
            res.setMessage(ex.getMessage());
            res.setTotalCount(0);
            res.setElement("failed");
        }
        return JSONUtil.ConvertToString("json", res);
    }

    public String updateFeature(DatasetVector dv, Map<String, String> meta,
                                Map<String, Map<Object, Map<String, Object>>> new_info,
                                String ServerName, String DatasetName) {
        int successcount = 0;
        int misscount = 0;
        ResultMsg res = new ResultMsg();
        try {
            meta.put("server", ServerName);
            WorkspaceConnectionInfo wc = dv.getDatasource().getWorkspace()
                    .getConnectionInfo();
            DatasourceConnectionInfo dc = dv.getDatasource()
                    .getConnectionInfo();
            meta.put("workspace", wc.getName());
            meta.put("datasource", dc.getServer());
            meta.put("dataset", DatasetName);
            meta.put("optype", "update");
            JSONObject jselements = new JSONObject();
            JSONArray jsastatus = new JSONArray();
            String TopClassDatasetName = meta.get("TopClassDatasetName");
            Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
            for (String key : new_info.keySet()) {
                if (meta.get("FeatureType").equals("Region")) {
                    Map<Object, Map<String, Object>> feature = new_info.get(key);
                    Object geo = feature.keySet().toArray()[0];
                    int FeatID = Integer.parseInt(key);
                    if (rs.seekID(FeatID)) {
                        byte[] old_f = featureTobyte(rs.getFeature(), feature.get(geo).keySet().toArray(), FeatID);
                        rs.edit();
                        GeoRegion new_geo = DATA_OP_BorderAutoBuild.TopLayerBorderOverlapFix((GeoRegion) geo, dv, TopClassDatasetName);
                        if (new_geo != null) {
                            rs.setGeometry(new_geo);
                        } else {
                            rs.setGeometry((GeoRegion) geo);
                        }
                        rs.setValues(feature.get(geo));
                        rs.update();
                        rs.edit();
                        rs.setGeometry(DATA_OP_BorderAutoBuild.BorderFix((GeoRegion) geo, dv));
                        boolean updateres = rs.update();
                        meta.put("featureid", FeatID + "");
                        byte[] new_f = featureTobyte(rs.getFeature(), feature.get(geo).keySet().toArray(), FeatID);
                        oplogbuild(meta, old_f, new_f);
                        if (updateres) {

                            successcount++;
                        } else {
                            misscount++;
                        }
                        jsastatus.add(FeatureOpstatus(updateres, FeatID));
                    } else {
                        misscount++;
                        jsastatus.add(FeatureOpstatus(false, FeatID));
                    }
                }
            }
            jselements.element("opStatus", jsastatus);
            res.setStatus(200);
            res.setNs_type("updateFeature");
            res.setMessage("success");
            res.setTotalCount(misscount + successcount);
            res.setElement(jselements.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            res.setStatus(400);
            res.setNs_type("updateFeature");
            res.setMessage(ex.getMessage());
            res.setTotalCount(0);
            res.setElement("failed");
        }
        return JSONUtil.ConvertToString("json", res);
    }

    public String deleteFeature(DatasetVector dv, Map<String, String> meta,
                                List<String> info, String ServerName, String DatasetName) {
        int successcount = 0;
        int misscount = 0;
        ResultMsg res = new ResultMsg();
        try {
            meta.put("server", ServerName);
            WorkspaceConnectionInfo wc = dv.getDatasource().getWorkspace()
                    .getConnectionInfo();
            DatasourceConnectionInfo dc = dv.getDatasource()
                    .getConnectionInfo();
            meta.put("workspace", wc.getName());
            meta.put("datasource", dc.getServer());
            meta.put("dataset", DatasetName);
            meta.put("optype", "delete");
            JSONObject jselements = new JSONObject();
            JSONArray jsastatus = new JSONArray();
            Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
            Object[] fields = new Object[rs.getFieldInfos().getCount()];
            for (int i = 0; i < rs.getFieldInfos().getCount(); i++) {
                fields[i] = rs.getFieldInfos().get(i).getName();
            }
            for (int i = 0; i < info.size(); i++) {
                int FeatID = Integer.parseInt(info.get(i));
                if (rs.seekID(FeatID)) {
                    byte[] old_f = featureTobyte(rs.getFeature(), fields,
                            FeatID);
                    boolean updateres = rs.delete();
                    meta.put("featureid", FeatID + "");
                    oplogbuild(meta, old_f, new byte[]{});
                    if (updateres) {
                        successcount++;
                    } else {
                        misscount++;
                    }
                    jsastatus.add(FeatureOpstatus(updateres, FeatID));
                } else {
                    misscount++;
                    jsastatus.add(FeatureOpstatus(false, FeatID));
                }
            }
            jselements.element("opStatus", jsastatus);
            res.setStatus(200);
            res.setNs_type("delFeature");
            res.setMessage("success");
            res.setTotalCount(misscount + successcount);
            res.setElement(jselements.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            res.setStatus(400);
            res.setNs_type("delFeature");
            res.setMessage(ex.getMessage());
            res.setTotalCount(0);
            res.setElement("failed");
        }
        return JSONUtil.ConvertToString("json", res);
    }

    private void oplogbuild(Map<String, String> meta, byte[] old_f, byte[] new_f) {
        DB_LOG_Handler op = new DB_LOG_Handler();
        try {
            List<DB_LOG_FeatureLogWithBLOBs> oplogs = new ArrayList<>();
            DB_LOG_FeatureLogWithBLOBs oplog = new DB_LOG_FeatureLogWithBLOBs();
            oplog.setID(GUID.getUUID());
            oplog.setOPERATIONTIME(new Date());
            oplog.setOPROLE(meta.get("EditRole"));
            oplog.setOPSERVER(meta.get("server"));
            oplog.setOPWORKSPACE(meta.get("workspace"));
            oplog.setOPDATASOURCE(meta.get("datasource"));
            oplog.setOPDATASET(meta.get("dataset"));
            oplog.setOPFEATUREID(meta.get("featureid"));
            oplog.setOLD_OPINFO(old_f);
            oplog.setNEW_OPINFO(new_f);
            oplog.setOP_TYPE(meta.get("optype"));
            oplogs.add(oplog);
            op.BatchInsert(oplogs);
            op.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            op.close();
        }
    }

    private byte[] featureTobyte(Feature feat, Object[] fields, int ID) {
        try {
            JSONObject jso = new JSONObject();
            GeoRegion gr = (GeoRegion) feat.getGeometry();
            JSONArray jsageo = new JSONArray();
            JSONObject jsf = new JSONObject();
            for (int i = 0; i < gr.getPartCount(); i++) {
                for (int j = 0; j < gr.getPart(i).getCount(); j++) {
                    JSONObject jsp = new JSONObject();
                    jsp.element("x", gr.getPart(i).getItem(j).x);
                    jsp.element("y", gr.getPart(i).getItem(j).y);
                    jsageo.element(jsp);
                }
            }
            for (Object field : fields) {
                jsf.element(field.toString(),
                        feat.getValue(field.toString()));
            }
            jso.element("FeatureID", ID);
            jso.element("Geometry", jsageo);
            jso.element("Fields", jsf);
            try {
                return jso.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private JSONObject FeatureOpstatus(boolean status, int ID) {
        JSONObject jostatus = new JSONObject();
        jostatus.element("FeatureID", ID);
        if (status) {
            jostatus.element("Status", "success");
        } else {
            jostatus.element("Status", "failed");
        }
        return jostatus;
    }
}
