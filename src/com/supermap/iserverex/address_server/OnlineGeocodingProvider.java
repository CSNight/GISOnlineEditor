package com.supermap.iserverex.address_server;

public interface OnlineGeocodingProvider {
    String StartPOIServer(String ServerName, String DatasetName);

    String StopPOISever();

    String NewClientSocket();

    String POISearch(String ID, String address);

    String POI_Client_Stop(String ID);
}
