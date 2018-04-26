package com.supermap.iserverex.address_server;

import com.supermap.services.components.Component;
import com.supermap.services.components.ComponentContext;
import com.supermap.services.components.ComponentContextAware;
import net.sf.json.JSONObject;

import java.util.List;

@Component(providerTypes = {OnlineGeocodingProvider.class}, optional = false, type = "")
public class OnlineGeocodingImpl implements OnlineGeocoding, ComponentContextAware {

    private OnlineGeocodingProvider onlinegeocodingProvider = null;

    @Override
    public void setComponentContext(ComponentContext context) {
        List<Object> providers = context.getProviders(Object.class);
        if (providers != null) {
            for (Object provider : providers) {
                if (provider instanceof OnlineGeocodingProvider) {
                    this.onlinegeocodingProvider = (OnlineGeocodingProvider) provider;
                    break;
                }
            }
        }
    }

    @Override
    public String StartPOIServer(String jsonElements, String paramType) {
        String temp = null;
        try {
            // TODO Auto-generated method stub
            JSONObject data = JSONObject.fromObject(jsonElements);
            String ServerName = data.getString("DataServerName");
            String DatasetName = data.getString("DataSet");
            temp = onlinegeocodingProvider.StartPOIServer(ServerName, DatasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String StopPOISever() {
        return onlinegeocodingProvider.StopPOISever();
    }

    @Override
    public String NewClientSocket() {
        return onlinegeocodingProvider.NewClientSocket();
    }

    @Override
    public String POISearch(String jsonElements, String paramType) {
        String temp = null;
        try {
            // TODO Auto-generated method stub
            JSONObject data = JSONObject.fromObject(jsonElements);
            String SERVERNAME = data.getString("SERVERNAME");
            String DATASETNAME = data.getString("DATASETNAME");
            String ADDRESS = data.getString("ADDRESS");
            boolean iscontain = data.getBoolean("ISCONTAINGEO");
            String AK = data.containsKey("AK") ? data.getString("AK") : "";
            String SK = data.containsKey("SK") ? data.getString("SK") : "";
            temp = onlinegeocodingProvider.POISearch(SERVERNAME, DATASETNAME, ADDRESS, iscontain, AK, SK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String POI_Client_Stop(String jsonElements, String paramType) {
        String temp = null;
        try {
            // TODO Auto-generated method stub
            JSONObject data = JSONObject.fromObject(jsonElements);
            String ID = data.getString("ID");
            temp = onlinegeocodingProvider.POI_Client_Stop(ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }
}
