package com.supermap.iserverex.address_server;

public interface OnlineGeocoding {
    String StartPOIServer(String jsonElements, String paramType);
    String StopPOISever();
}
