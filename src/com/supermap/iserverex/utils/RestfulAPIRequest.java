package com.supermap.iserverex.utils;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RestfulAPIRequest {
    @SuppressWarnings("rawtypes")
    private Map iserverinfo = null;
    private String Server = "";
    private String Port = "";
    private String User = "";
    private String Password = "";

    public RestfulAPIRequest() {
        iserverinfo = ConfigReader.readerXml();
        Server = iserverinfo.get("server").toString();
        Port = iserverinfo.get("port").toString();
        User = iserverinfo.get("user").toString();
        Password = iserverinfo.get("password").toString();
    }

    public String CheckDataServerProvider(String ServerName) {
        String info = ConfigReader.WorkspaceInfoGet(ServerName);
        if (!info.equals("")) {
            return info;
        } else {
            String map = GetDataServerProvider(ServerName);
            Map<String, String> workspaceinfo = new HashMap<String, String>();
            workspaceinfo.put("workspace", ServerName);
            workspaceinfo.put("info", map);
            ConfigReader.XmlWorksapceInfoSaver(workspaceinfo);
            return map;
        }
    }

    public String GetDataServerProvider(String ServerName) {
        String ComponentName = GetDataServerInstanceInfo(ServerName);
        String ProviderName = GetDataServerComponentInfo(ComponentName);
        String url = String.format(
                "http://%s:%s/iserver/manager/providers/%s.json", Server, Port,
                ProviderName);
        String ResponseString = CustomHttpRequest.sendGet(url,
                String.format("token=%s", GenerateASToken()));
        JSONObject js = JSONObject.fromObject(ResponseString);
        js.getJSONObject("config").get("workspacePath");
        return js.getJSONObject("config").get("workspacePath").toString();
    }

    private String GetDataServerComponentInfo(String ComponentName) {
        String url = String.format(
                "http://%s:%s/iserver/manager/components/%s.json", Server,
                Port, ComponentName);
        String ResponseString = CustomHttpRequest.sendGet(url,
                String.format("token=%s", GenerateASToken()));
        Map<String, Object> info = JSONUtil.json2Map(ResponseString);
        return info.get("providers").toString();
    }

    private String GetDataServerInstanceInfo(String ServerName) {
        String url = String.format(
                "http://%s:%s/iserver/manager/instances/%s/rest.json", Server,
                Port, ServerName);
        String ResponseString = CustomHttpRequest.sendGet(url,
                String.format("token=%s", GenerateASToken()));
        Map<String, Object> info = JSONUtil.json2Map(ResponseString);
        return info.get("componentName").toString();
    }

    private String GenerateASToken() {
        String url = String.format(
                "http://%s:%s/iserver/services/security/tokens.json", Server,
                Port);
        String credential = "{\"userName\":\"" + User + "\",\"password\":\""
                + Password
                + "\",\"clientType\":\"RequestIP\",\"expiration\":60}";
        String token = CustomHttpRequest.sendPost(url, credential);
        return token;
    }
}
