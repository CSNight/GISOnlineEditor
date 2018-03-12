package com.supermap.spscsi.dataimport;

public interface PostOffice {
    public String importBoundryData(String filepath, String datasetName,
                                    String GeometryField);

    public String importPOIData(String filepath, String datasetName);

    public String CreateWorkspace(String Path, String Name);
}
