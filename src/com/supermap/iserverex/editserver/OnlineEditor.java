package com.supermap.iserverex.editserver;

public interface OnlineEditor {
    public String InsertFeature(String jsonElements, String paramType);

    public String UpdateFeature(String jsonElements, String paramType);

    public String DeleteFeature(String jsonElements, String paramType);

    public String QueryByDatasetName(String jsonElements, String paramType);

    public String QueryByFeatureIDAndDataset(String jsonElements,
                                             String paramType);
}
