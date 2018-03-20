package com.supermap.iserverex.edit_server;

import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Workspace;
import com.supermap.iserverex.logmanage.DB_LOG_LogQuery;
import com.supermap.iserverex.operation.DATA_OP_BorderCheck;
import com.supermap.iserverex.operation.DATA_OP_DataModify;
import com.supermap.iserverex.operation.DATA_OP_DataPrepare;
import com.supermap.iserverex.utils.ConfigReader;
import com.supermap.iserverex.utils.RestfulAPIRequest;
import com.supermap.services.components.spi.ProviderContext;
import com.supermap.services.components.spi.ProviderContextAware;
import com.supermap.services.providers.WorkspaceConnectionInfo;
import com.supermap.services.providers.WorkspaceContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineEditorProviderImpl implements OnlineEditorProvider,
        ProviderContextAware {
    public String Info = null;

    public OnlineEditorProviderImpl() {

    }

    @Override
    public void setProviderContext(ProviderContext context) {
    }

    @Override
    public String insertFeature(String Features, String ServerName,
                                String DatasetName) {
        // TODO Auto-generated method stub
        try {
            Workspace workspace = getWorkspace(ServerName);
            DatasetVector dv = getDatasetVector(workspace, DatasetName);
            DATA_OP_DataPrepare dp = new DATA_OP_DataPrepare();
            Map<String, String> meta = dp.DataDispatch(Features);
            List<Map<Object, Map<String, Object>>> info = dp.datainsertBuild(
                    meta.get("Features"), meta.get("FeatureType"));
            DATA_OP_DataModify dsh = new DATA_OP_DataModify();
            String result = dsh.addFeature(dv, meta, info, ServerName,
                    DatasetName);
            workspace.save();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String updateFeature(String Features, String ServerName,
                                String DatasetName) {
        // TODO Auto-generated method stub
        try {
            Workspace workspace = getWorkspace(ServerName);
            DatasetVector dv = getDatasetVector(workspace, DatasetName);
            DATA_OP_DataPrepare dp = new DATA_OP_DataPrepare();
            Map<String, String> meta = dp.DataDispatch(Features);
            Map<String, Map<Object, Map<String, Object>>> info = dp
                    .dataupdateBuild(meta.get("Features"),
                            meta.get("FeatureType"));
            DATA_OP_DataModify dsh = new DATA_OP_DataModify();
            String result = dsh.updateFeature(dv, meta, info, ServerName,
                    DatasetName);
            workspace.save();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String deleteFeature(String Features, String ServerName,
                                String DatasetName) {
        // TODO Auto-generated method stub
        try {
            Workspace workspace = getWorkspace(ServerName);
            DatasetVector dv = getDatasetVector(workspace, DatasetName);
            DATA_OP_DataPrepare dp = new DATA_OP_DataPrepare();
            Map<String, String> meta = dp.DataDispatch(Features);
            List<String> info = dp.datadeleteBuild(meta.get("Features"));
            DATA_OP_DataModify dsh = new DATA_OP_DataModify();
            String result = dsh.deleteFeature(dv, meta, info, ServerName,
                    DatasetName);
            workspace.save();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
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
                Map<String, String> workspaceinfo = new HashMap<>();
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
            Map<String, String> workspaceinfo = new HashMap<>();
            workspaceinfo.put("workspace", ServerName);
            workspaceinfo.put("info", Info);
            ConfigReader.XmlWorksapceInfoSaver(workspaceinfo);
            return workspace;
        }
    }

    @Override
    public String QueryByDataset(String DatasetName) {
        // TODO Auto-generated method stub
        DB_LOG_LogQuery dblog = new DB_LOG_LogQuery();
        return dblog.QueryBySet(DatasetName);

    }

    @Override
    public String QueryByIDAndSet(String FeatureID, String DatasetName) {
        // TODO Auto-generated method stub
        DB_LOG_LogQuery dblog = new DB_LOG_LogQuery();
        return dblog.QueryByID(FeatureID, DatasetName);
    }

    @Override
    public String BorderConflictCheck(String ServerName, String DatasetName) {
        Workspace workspace = getWorkspace(ServerName);
        DatasetVector dv = getDatasetVector(workspace, DatasetName);
        return DATA_OP_BorderCheck.BorderConflictDetect(dv);
    }

    @Override
    public String BorderTopClassConflictCheck(String ServerName, String DatasetName, String TopsetName) {
        Workspace workspace = getWorkspace(ServerName);
        DatasetVector dv = getDatasetVector(workspace, DatasetName);
        DatasetVector dvtop = getDatasetVector(workspace, TopsetName);
        return DATA_OP_BorderCheck.BorderTopClassConflictDetect(dv, dvtop);
    }
}
