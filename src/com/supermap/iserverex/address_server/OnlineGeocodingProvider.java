package com.supermap.iserverex.address_server;

public interface OnlineGeocodingProvider {
    String StartPOIServer(String ServerName, String DatasetName);

    String StopPOISever();

    String NewClientSocket();

    String POISearch(String ServerName, String DatasetName, String address, boolean isContainGeo, String ak, String sk);

    String POI_Client_Stop(String ID);
}
