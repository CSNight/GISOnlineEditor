package com.supermap.iserverex.address_server;

public interface OnlineGeocoding {
    String StartPOIServer(String jsonElements, String paramType);

    String StopPOISever();

    String NewClientSocket();

    String POISearch(String jsonElements, String paramType);

    String POI_Client_Stop(String jsonElements, String paramType);
}
