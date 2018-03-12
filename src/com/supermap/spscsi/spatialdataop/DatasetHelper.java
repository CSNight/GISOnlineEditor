package com.supermap.spscsi.spatialdataop;

import com.supermap.data.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.parseDouble;

public class DatasetHelper {
    public DatasetVector createDataset(String datasetType, String name,
                                       Datasource m_datasource) {
        Datasets datasets = m_datasource.getDatasets();
        try {
            if (!datasets.isAvailableDatasetName(name)) {
                return null;
            }

            DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();

            if (datasetType.equals("点数据集")) {
                datasetVectorInfo.setType(DatasetType.POINT);
                datasetVectorInfo.setName(name);
            }
            if (datasetType.equals("线数据集")) {
                datasetVectorInfo.setType(DatasetType.LINE);
                datasetVectorInfo.setName(name);
            }
            if (datasetType.equals("面数据集")) {
                datasetVectorInfo.setType(DatasetType.REGION);
                datasetVectorInfo.setName(name);
            }
            if (datasetType.equals("属性表数据集")) {
                datasetVectorInfo.setType(DatasetType.TABULAR);
                datasetVectorInfo.setName(name);
            }
            DatasetVector dv = datasets.create(datasetVectorInfo);
            return dv;
        } catch (Exception ex) {
            return null;
        }
    }

    public void addFieldInfo(FieldInfos fields,
                             List<Map<String, Object>> fieldinfos) {

        for (int i = 0; i < fieldinfos.size(); i++) {
            try {
                String fieldName = fieldinfos.get(i).get("fieldname")
                        .toString().replace(" ", "");
                if (fields.indexOf(fieldName) != -1) {
                    fields.remove(fieldName);
                }
                // 新建字段，添加到字段信息集合
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setName(fieldName);
                fieldInfo.setCaption(fieldName);
                fieldInfo.setType(FieldType.WTEXT);
                fieldInfo.setRequired(false);
                fields.add(fieldInfo);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }

    }

    public void addRecord(DatasetVector dv,
                          Map<Integer, Map<Integer, Object>> dic, String[] title,
                          int exceptionindex, String geoType, List<List<String[]>> xys) {
        Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
        int i = 0;
        rs.getBatch().setMaxRecordCount(3000);
        rs.getBatch().begin();
        while (i < dic.size()) {
            Map<String, Object> record = new HashMap<>();
            for (int j = 0; j < dic.get(i).size(); j++) {
                if (j == exceptionindex) {
                    continue;
                }
                record.put(title[j], dic.get(i).get(j).toString());
            }
            if (geoType == "Point") {
                if (xys.get(i).size() == 1) {
                    double x = parseDouble(xys.get(i).get(0)[0]);
                    double y = parseDouble(xys.get(i).get(0)[1]);
                    GeoPoint geoPoint = new GeoPoint(x, y);
                    rs.addNew(geoPoint, record);
                }
            } else if (geoType == "Line") {
                if (xys.get(i).size() >= 2) {
                    Point2Ds p2s = new Point2Ds();
                    for (int p = 0; p < xys.get(i).size(); p++) {
                        double x = parseDouble(xys.get(i).get(p)[0]);
                        double y = parseDouble(xys.get(i).get(p)[1]);
                        Point2D p2 = new Point2D(x, y);
                        p2s.add(p2);
                    }
                    GeoLine gl = new GeoLine(p2s);
                    rs.addNew(gl, record);
                }
            } else {
                if (xys.get(i).size() >= 3) {
                    Point2Ds p2s = new Point2Ds();
                    for (int p = 0; p < xys.get(i).size(); p++) {

                        double x = parseDouble(xys.get(i).get(p)[0]);
                        double y = parseDouble(xys.get(i).get(p)[1]);
                        Point2D p2 = new Point2D(x, y);
                        p2s.add(p2);
                    }
                    GeoRegion gr = new GeoRegion(p2s);
                    rs.addNew(gr, record);
                }

            }
            i++;
        }
        rs.getBatch().update();
        rs.dispose();
    }
}
