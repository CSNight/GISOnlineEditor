package com.supermap.iserverex.edit_server;

public interface OnlineEditorProvider {
    String insertFeature(String Features, String ServerName, String DatasetName);

    String updateFeature(String Features, String ServerName, String DatasetName);

    String deleteFeature(String Features, String ServerName, String DatasetName);

    String QueryByDataset(String DatasetName);

    String QueryByIDAndSet(String FeatureID, String DatasetName);

    String BorderConflictCheck(String ServerName, String DatasetName);
}
