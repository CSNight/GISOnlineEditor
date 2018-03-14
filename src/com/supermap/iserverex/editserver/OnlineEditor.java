package com.supermap.iserverex.editserver;

public interface OnlineEditor {
    String InsertFeature(String jsonElements, String paramType);

    String UpdateFeature(String jsonElements, String paramType);

    String DeleteFeature(String jsonElements, String paramType);

    String QueryByDatasetName(String jsonElements, String paramType);

    String QueryByFeatureIDAndDataset(String jsonElements, String paramType);

    String BorderConflictCheck(String jsonElements, String paramType);
}
