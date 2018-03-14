package com.supermap.iserverex.logmanage;

import com.supermap.iserverex.utils.JSONUtil;
import com.supermap.iserverex.utils.ResultMsg;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DB_LOG_LogQuery {
    public String QueryBySet(String set) {
        ResultMsg res = new ResultMsg();
        try {
            DB_LOG_Handler op = new DB_LOG_Handler();
            String sql = "SELECT * FROM FEATUREOPLOG WHERE OPDATASET='" + set
                    + "' Order by OPERATIONTIME DESC";
            List<DB_LOG_FeatureLogWithBLOBs> result = op.query(sql);
            JSONObject jolog = new JSONObject();
            JSONArray jologs = new JSONArray();
            for (DB_LOG_FeatureLogWithBLOBs aResult : result) {
                JSONObject jf = new JSONObject();
                jf.element("FeatureID", aResult.getOPFEATUREID());
                jf.element("OpRole", aResult.getOPROLE());
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(aResult.getOPERATIONTIME()
                        .getTime());
                jf.element("OpTime", time);
                jf.element("OpType", aResult.getOP_TYPE());
                if (aResult.getOLD_OPINFO() != null) {
                    byte[] old = aResult.getOLD_OPINFO();
                    String ress = "";
                    try {
                        ress = new String(old, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jf.element("Feature_Old", JSONObject.fromObject(ress));
                } else {
                    jf.element("Feature_Old", "");
                }
                if (aResult.getNEW_OPINFO() != null) {
                    byte[] ne = aResult.getNEW_OPINFO();
                    String ress = "";
                    try {
                        ress = new String(ne, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jf.element("Feature_New", JSONObject.fromObject(ress));
                } else {
                    jf.element("Feature_New", "");
                }
                jologs.add(jf);
            }
            jolog.element("Log", jologs);
            res.setStatus(200);
            res.setNs_type("querybyset");
            res.setMessage("success");
            res.setTotalCount(jologs.size());
            res.setElement(jolog);
        } catch (Exception ex) {
            ex.printStackTrace();
            res.setStatus(400);
            res.setNs_type("querybyset");
            res.setMessage(ex.getMessage());
            res.setTotalCount(0);
            res.setElement("failed");
        }
        return JSONUtil.ConvertToString("json", res);
    }

    public String QueryByID(String id, String set) {
        ResultMsg res = new ResultMsg();
        try {
            DB_LOG_Handler op = new DB_LOG_Handler();
            String sql = "SELECT * FROM FEATUREOPLOG WHERE OPDATASET='" + set
                    + "' AND OPFEATUREID='" + id
                    + "' Order by OPERATIONTIME DESC";
            List<DB_LOG_FeatureLogWithBLOBs> result = op.query(sql);
            JSONObject jolog = new JSONObject();
            JSONArray jologs = new JSONArray();
            for (DB_LOG_FeatureLogWithBLOBs aResult : result) {
                JSONObject jf = new JSONObject();
                jf.element("FeatureID", aResult.getOPFEATUREID());
                jf.element("OpRole", aResult.getOPROLE());
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(aResult.getOPERATIONTIME()
                        .getTime());
                jf.element("OpTime", time);
                jf.element("OpType", aResult.getOP_TYPE());
                if (aResult.getOLD_OPINFO() != null) {
                    byte[] old = aResult.getOLD_OPINFO();
                    String ress = "";
                    try {
                        ress = new String(old, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jf.element("Feature_Old", JSONObject.fromObject(ress));
                } else {
                    jf.element("Feature_Old", "");
                }
                if (aResult.getNEW_OPINFO() != null) {
                    byte[] ne = aResult.getNEW_OPINFO();
                    String ress = "";
                    try {
                        ress = new String(ne, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jf.element("Feature_New", JSONObject.fromObject(ress));
                } else {
                    jf.element("Feature_New", "");
                }
                jologs.add(jf);
            }
            jolog.element("Log", jologs);
            res.setStatus(200);
            res.setNs_type("querybyid");
            res.setMessage("success");
            res.setTotalCount(jologs.size());
            res.setElement(jolog);
        } catch (Exception ex) {
            ex.printStackTrace();
            res.setStatus(400);
            res.setNs_type("querybyset");
            res.setMessage(ex.getMessage());
            res.setTotalCount(0);
            res.setElement("failed");
        }
        return JSONUtil.ConvertToString("json", res);
    }
}
