package com.supermap.iserverex.edit_server;

import com.supermap.services.components.Component;
import com.supermap.services.components.ComponentContext;
import com.supermap.services.components.ComponentContextAware;
import net.sf.json.JSONObject;

import java.util.List;

@Component(providerTypes = {OnlineEditorProvider.class}, optional = false, type = "")
public class OnlineEditorImpl implements OnlineEditor, ComponentContextAware {

    private OnlineEditorProvider onlineeditorProvider = null;

    @Override
    public String InsertFeature(String jsonElements, String paramType) {
        String temp = null;
        try {
            JSONObject data = JSONObject.fromObject(jsonElements);
            String ServerName = data.getString("DataServerName");
            String DatasetName = data.getString("DataSet");
            temp = onlineeditorProvider.insertFeature(jsonElements, ServerName,
                    DatasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String UpdateFeature(String jsonElements, String paramType) {
        String temp = null;
        try {
            JSONObject data = JSONObject.fromObject(jsonElements);
            String ServerName = data.getString("DataServerName");
            String DatasetName = data.getString("DataSet");
            temp = onlineeditorProvider.updateFeature(jsonElements, ServerName,
                    DatasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String DeleteFeature(String jsonElements, String paramType) {
        String temp = null;
        try {
            JSONObject data = JSONObject.fromObject(jsonElements);
            String ServerName = data.getString("DataServerName");
            String DatasetName = data.getString("DataSet");
            temp = onlineeditorProvider.deleteFeature(jsonElements, ServerName,
                    DatasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public void setComponentContext(ComponentContext context) {
        List<Object> providers = context.getProviders(Object.class);
        if (providers != null) {
            for (Object provider : providers) {
                if (provider instanceof OnlineEditorProvider) {
                    this.onlineeditorProvider = (OnlineEditorProvider) provider;
                    break;
                }
            }
        }
    }

    @Override
    public String QueryByDatasetName(String jsonElements, String paramType) {
        String temp = null;
        try {
            // TODO Auto-generated method stub
            JSONObject data = JSONObject.fromObject(jsonElements);
            String DatasetName = data.getString("DataSet");
            temp = onlineeditorProvider.QueryByDataset(DatasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String QueryByFeatureIDAndDataset(String jsonElements,
                                             String paramType) {
        String temp = null;
        try {
            // TODO Auto-generated method stub
            JSONObject data = JSONObject.fromObject(jsonElements);
            String FeatureID = data.getString("FeatureID");
            String DatasetName = data.getString("DataSet");
            temp = onlineeditorProvider.QueryByIDAndSet(FeatureID, DatasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String BorderConflictCheck(String jsonElements, String paramType) {
        String temp = null;
        try {
            // TODO Auto-generated method stub
            JSONObject data = JSONObject.fromObject(jsonElements);
            String ServerName = data.getString("DataServerName");
            String DatasetName = data.getString("DataSet");
            temp = onlineeditorProvider.BorderConflictCheck(ServerName, DatasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

}
