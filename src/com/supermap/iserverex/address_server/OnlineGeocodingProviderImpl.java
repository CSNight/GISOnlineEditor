package com.supermap.iserverex.address_server;

import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Workspace;
import com.supermap.iserverex.address_query.QUERY_POI_BaiduAPI;
import com.supermap.iserverex.address_query.QUERY_POI_Client;
import com.supermap.iserverex.address_query.QUERY_POI_SocketServer;
import com.supermap.iserverex.utils.ConfigReader;
import com.supermap.iserverex.utils.JSONUtil;
import com.supermap.iserverex.utils.RestfulAPIRequest;
import com.supermap.iserverex.utils.ResultMsg;
import com.supermap.services.components.spi.ProviderContext;
import com.supermap.services.components.spi.ProviderContextAware;
import com.supermap.services.providers.WorkspaceConnectionInfo;
import com.supermap.services.providers.WorkspaceContainer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OnlineGeocodingProviderImpl implements OnlineGeocodingProvider,
        ProviderContextAware {
    public String Info = null;
    private String ServerName = "data-WS_JSJT";
    private String DatasetName = "cdtdjgwl";
    private QUERY_POI_SocketServer socketServer = null;
    private Thread socket_server;
    private Map<String, QUERY_POI_Client> clients = new HashMap<>();

    public OnlineGeocodingProviderImpl() {
    }

    @Override
    public void setProviderContext(ProviderContext providerContext) {
    }

    private DatasetVector getDatasetVector(Workspace workspace,
                                           String DatasetName) {
        DatasetVector dataset = null;
        if (workspace != null) {
            for (int i = 0; i < workspace.getDatasources().getCount(); i++) {
                Datasource ds = workspace.getDatasources().get(i);
                if (ds.getDatasets().contains(DatasetName)) {
                    dataset = (DatasetVector) ds.getDatasets().get(DatasetName);
                    break;
                }
            }
            return dataset;
        } else {
            return null;
        }
    }

    private Workspace getWorkspace(String ServerName) {
        RestfulAPIRequest rareq = new RestfulAPIRequest();
        Workspace workspace = null;
        try {
            Info = rareq.CheckDataServerProvider(ServerName);
            workspace = WorkspaceContainer.get(
                    WorkspaceConnectionInfo.parse(Info), this);
            if (workspace == null) {
                Info = rareq.GetDataServerProvider(ServerName);
                workspace = WorkspaceContainer.get(
                        WorkspaceConnectionInfo.parse(Info), this);
                Map<String, String> workspaceinfo = new HashMap<String, String>();
                workspaceinfo.put("workspace", ServerName);
                workspaceinfo.put("info", Info);
                ConfigReader.XmlWorksapceInfoSaver(workspaceinfo);
            }
            return workspace;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("服务工作空间变更或服务名有误");
            Info = rareq.GetDataServerProvider(ServerName);
            workspace = WorkspaceContainer.get(
                    WorkspaceConnectionInfo.parse(Info), this);
            Map<String, String> workspaceinfo = new HashMap<String, String>();
            workspaceinfo.put("workspace", ServerName);
            workspaceinfo.put("info", Info);
            ConfigReader.XmlWorksapceInfoSaver(workspaceinfo);
            return workspace;
        }
    }

    @Override
    public String StartPOIServer(String ServerName, String DatasetName) {
        ResultMsg res = new ResultMsg();
        try {
            socket_server = new Thread(() -> StartServer(ServerName, DatasetName));
            socket_server.start();
            res.setStatus(200);
            res.setNs_type("Stop");
            res.setMessage("success");
            return JSONUtil.ConvertToString("json", res);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            res.setStatus(400);
            res.setNs_type("Stop");
            res.setMessage("failed");
            return JSONUtil.ConvertToString("json", res);
        }

    }

    public String StartServer(String ServerName, String DatasetName) {
        try {
            Workspace workspace = getWorkspace(ServerName);
            DatasetVector dv = getDatasetVector(workspace, DatasetName);
            if (socketServer != null) {
                StopPOISever();
                if (!ServerName.equals(this.ServerName) || !DatasetName.equals(this.DatasetName)) {
                    this.ServerName = ServerName;
                    this.DatasetName = DatasetName;
                    socketServer = new QUERY_POI_SocketServer(workspace, dv);
                } else if (socketServer.getServerSocket().isClosed()) {
                    socketServer = new QUERY_POI_SocketServer(workspace, dv);
                }
            } else {
                socketServer = new QUERY_POI_SocketServer(workspace, dv);
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    @Override
    public String StopPOISever() {
        ResultMsg res = new ResultMsg();
        try {
            if (socketServer != null) {
                if (!socketServer.getServerSocket().isClosed()) {
                    socketServer.getServerSocket().close();
                }
            }
            socketServer = null;
            if (socket_server != null) {
                socket_server.interrupt();
            }
            clients.clear();
            res.setStatus(200);
            res.setNs_type("Stop");
            res.setMessage("success");
            return JSONUtil.ConvertToString("json", res);
        } catch (IOException e) {
            e.printStackTrace();
            res.setStatus(400);
            res.setNs_type("Stop");
            res.setMessage("failed");
            return JSONUtil.ConvertToString("json", res);
        }

    }

    @Override
    public String NewClientSocket() {
        QUERY_POI_Client poi_client = new QUERY_POI_Client();
        String id = poi_client.SendMessage("").trim().replaceAll("/\u0000/", "").substring(0, 36);
        clients.put(id, poi_client);
        return id;
    }

    @Override
    public String POISearch(String ServerName, String DatasetName, String address, boolean isContainGeo, String ak, String sk) {
        Workspace workspace = getWorkspace(ServerName);
        DatasetVector dv = getDatasetVector(workspace, DatasetName);

        return QUERY_POI_BaiduAPI.getDS(address, isContainGeo, dv, ak, sk);
//        QUERY_POI_Client poi_client = clients.get(ID);
//        if (poi_client != null) {
//            return poi_client.SendMessage(address);
//        }
    }

    @Override
    public String POI_Client_Stop(String ID) {
        QUERY_POI_Client poi_client = clients.get(ID);
        if (poi_client != null) {
            poi_client.Stop();
            clients.remove(ID);
            return "success";
        }
        return "failed";
    }
}
