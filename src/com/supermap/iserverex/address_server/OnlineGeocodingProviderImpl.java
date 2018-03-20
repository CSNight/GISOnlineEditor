package com.supermap.iserverex.address_server;

import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Workspace;
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
    private String ServerName = "data-world";
    private String DatasetName = "Countries";
    private QUERY_POI_SocketServer socketServer = null;
    private Thread socket_server;

    public OnlineGeocodingProviderImpl() {
        socket_server = new Thread(StartPOIServer(ServerName, DatasetName));
        socket_server.start();
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
            socket_server = new Thread(StartServer(ServerName, DatasetName));
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
                socket_server.stop();
            }
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
}
