package com.supermap.spscsi.dataimport;

public interface PostOfficeProvider {
    public String importOrgBoundryData(String filepath, String datasetName,
                                       String GeometryField);

    public String importPoiData(String filepath, String datasetName);

    public String CreateWorkspace(String filepath, String Name);
}
