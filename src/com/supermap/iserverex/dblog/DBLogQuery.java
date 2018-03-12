package com.supermap.iserverex.dblog;

import com.supermap.iserverex.utils.JSONUtil;
import com.supermap.iserverex.utils.ResultMsg;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DBLogQuery {
    public String QueryBySet(String set) {
        ResultMsg res = new ResultMsg();
        try {
            OpLogHelper op = new OpLogHelper();
            String sql = "SELECT * FROM FEATUREOPLOG WHERE OPDATASET='" + set
                    + "' Order by OPERATIONTIME DESC";
            List<FeatureOpLogWithBLOBs> result = op.query(sql);
            JSONObject jolog = new JSONObject();
            JSONArray jologs = new JSONArray();
            for (int i = 0; i < result.size(); i++) {
                JSONObject jf = new JSONObject();
                jf.element("FeatureID", result.get(i).getOPFEATUREID());
                jf.element("OpRole", result.get(i).getOPROLE());
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(result.get(i).getOPERATIONTIME()
                        .getTime());
                jf.element("OpTime", time);
                jf.element("OpType", result.get(i).getOP_TYPE());
                if (result.get(i).getOLD_OPINFO() != null) {
                    byte[] old = (byte[]) result.get(i).getOLD_OPINFO();
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
                if (result.get(i).getNEW_OPINFO() != null) {
                    byte[] ne = (byte[]) result.get(i).getNEW_OPINFO();
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
            OpLogHelper op = new OpLogHelper();
            String sql = "SELECT * FROM FEATUREOPLOG WHERE OPDATASET='" + set
                    + "' AND OPFEATUREID='" + id
                    + "' Order by OPERATIONTIME DESC";
            List<FeatureOpLogWithBLOBs> result = op.query(sql);
            JSONObject jolog = new JSONObject();
            JSONArray jologs = new JSONArray();
            for (int i = 0; i < result.size(); i++) {
                JSONObject jf = new JSONObject();
                jf.element("FeatureID", result.get(i).getOPFEATUREID());
                jf.element("OpRole", result.get(i).getOPROLE());
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(result.get(i).getOPERATIONTIME()
                        .getTime());
                jf.element("OpTime", time);
                jf.element("OpType", result.get(i).getOP_TYPE());
                if (result.get(i).getOLD_OPINFO() != null) {
                    byte[] old = (byte[]) result.get(i).getOLD_OPINFO();
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
                if (result.get(i).getNEW_OPINFO() != null) {
                    byte[] ne = (byte[]) result.get(i).getNEW_OPINFO();
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
