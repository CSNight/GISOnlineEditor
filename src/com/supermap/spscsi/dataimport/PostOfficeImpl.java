package com.supermap.spscsi.dataimport;

import com.supermap.services.components.Component;
import com.supermap.services.components.ComponentContext;
import com.supermap.services.components.ComponentContextAware;
import com.supermap.services.components.commontypes.MapParameter;
import com.supermap.services.components.spi.MapProvider;

import java.util.List;

@SuppressWarnings("unused")
@Component(providerTypes = {MapProvider.class, PostOfficeProvider.class}, optional = false, type = "")
public class PostOfficeImpl implements PostOffice, ComponentContextAware {

    private MapProvider mapProvider = null;
    private PostOfficeProvider postofficeProvider = null;
    private MapParameter defaultMapParam = null;

    public PostOfficeImpl() {

    }

    @Override
    public String importBoundryData(String filepath, String datasetName,
                                    String GeometryField) {
        String temp = null;
        try {
            temp = postofficeProvider.importOrgBoundryData(filepath,
                    datasetName, GeometryField);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String importPOIData(String filepath, String datasetName) {
        String temp = null;
        try {
            temp = postofficeProvider.importPoiData(filepath, datasetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public void setComponentContext(ComponentContext context) {
        PostOfficeParam param = context.getConfig(PostOfficeParam.class);
        if (param == null) {
            throw new IllegalArgumentException("参数 TemperatureParam 不能为空");
        }
        List<Object> providers = context.getProviders(Object.class);
        if (providers != null) {
            for (Object provider : providers) {
                if (provider instanceof PostOfficeProvider) {
                    this.postofficeProvider = (PostOfficeProvider) provider;
                    break;
                }
            }
            for (Object provider : providers) {
                if (provider instanceof MapProvider) {
                    this.mapProvider = (MapProvider) provider;
                }
            }
        }
    }

    @Override
    public String CreateWorkspace(String Path, String Name) {
        String temp = null;
        try {
            temp = postofficeProvider.CreateWorkspace(Path, Name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

}
