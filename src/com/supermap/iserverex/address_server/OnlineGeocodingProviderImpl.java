package com.supermap.iserverex.address_server;

import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Workspace;
import com.supermap.iserverex.utils.ConfigReader;
import com.supermap.iserverex.utils.RestfulAPIRequest;
import com.supermap.services.components.spi.ProviderContext;
import com.supermap.services.components.spi.ProviderContextAware;
import com.supermap.services.providers.WorkspaceConnectionInfo;
import com.supermap.services.providers.WorkspaceContainer;

import java.util.HashMap;
import java.util.Map;

public class OnlineGeocodingProviderImpl implements OnlineGeocodingProvider,
        ProviderContextAware {
    public String Info = null;

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
}
