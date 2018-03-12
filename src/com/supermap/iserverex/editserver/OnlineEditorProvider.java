package com.supermap.iserverex.editserver;

public interface OnlineEditorProvider {
    public String insertFeature(String Features, String ServerName,
                                String DatasetName);

    public String updateFeature(String Features, String ServerName,
                                String DatasetName);

    public String deleteFeature(String Features, String ServerName,
                                String DatasetName);

    public String QueryByDataset(String DatasetName);

    public String QueryByIDAndSet(String FeatureID, String DatasetName);
}
