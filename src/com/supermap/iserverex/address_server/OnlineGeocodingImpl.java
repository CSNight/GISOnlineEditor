package com.supermap.iserverex.address_server;

import com.supermap.services.components.Component;
import com.supermap.services.components.ComponentContext;
import com.supermap.services.components.ComponentContextAware;

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
}
