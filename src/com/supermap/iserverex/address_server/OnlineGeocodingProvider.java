package com.supermap.iserverex.address_server;

public interface OnlineGeocodingProvider {
    String StartPOIServer(String ServerName, String DatasetName);
    String StopPOISever();
}
