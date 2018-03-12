package com.supermap.spscsi.dataimport;

import com.supermap.data.*;
import com.supermap.services.components.spi.ProviderContext;
import com.supermap.services.components.spi.ProviderContextAware;
import com.supermap.spscsi.dataop.DataGetfromXlsx;
import com.supermap.spscsi.dataop.DataInfoBuliding;
import com.supermap.spscsi.spatialdataop.DatasetHelper;
import com.supermap.spscsi.spatialdataop.WorkspaceHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostOfficeProviderImpl implements PostOfficeProvider,
        ProviderContextAware {
    public String filePath;
    public static String workspacepath = "";

    public PostOfficeProviderImpl() {
    }

    private String Createworkspace(String Path, String Name) {
        Workspace ws = new Workspace();
        WorkspaceHelper wsh = new WorkspaceHelper(ws);
        File file = new File(Path + Name + ".udb");
        File filex = new File(Path + Name + ".smwu");
        File filed = new File(Path + Name + ".udd");
        if (file.exists()) {
            file.delete();
        }
        if (filex.exists()) {
            filex.delete();
        }
        if (filed.exists()) {
            filed.delete();
        }
        workspacepath = Path + Name + ".smwu";
        wsh.createWorkspaceSXW(Path, Name, "", 0, 3);
        DatasourceConnectionInfo dsci = new DatasourceConnectionInfo();
        dsci.setEngineType(EngineType.UDB);
        dsci.setServer(Path + Name + ".udb");
        dsci.setAlias(Name);
        wsh.getWorksapce().getDatasources().create(dsci);
        wsh.getWorksapce().save();
        wsh.getWorksapce().close();
        wsh.getWorksapce().dispose();
        return "success:" + workspacepath;
    }

    @Override
    public String importOrgBoundryData(String filepath, String datasetName,
                                       String GeometryField) {
        WorkspaceHelper wsh = new WorkspaceHelper();
        wsh.openWorkspaceSXW(workspacepath, "");
        Datasource datasource = wsh.getWorksapce().getDatasources()
                .get("XYDATA");

        String[] title = null;
        Map<Integer, Map<Integer, Object>> map = null;
        try {
            DataGetfromXlsx excelReader = new DataGetfromXlsx(filepath);
            // 对读取Excel表格标题测试
            title = excelReader.readExcelTitle();
            // 对读取Excel表格内容测试
            map = excelReader.readExcelContent();
        } catch (FileNotFoundException e) {
            System.out.println("未找到指定路径的文件!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataInfoBuliding db = new DataInfoBuliding();
        DatasetHelper dshp = new DatasetHelper();
        DatasetVector dv = dshp.createDataset("面数据集", datasetName, datasource);
        List<Map<String, Object>> mapfield = db.parsefieldinfo(title);
        dshp.addFieldInfo(dv.getFieldInfos(), mapfield);
        List<List<String[]>> list = new ArrayList<>();
        int exceptionindex = -1;
        for (int i = 0; i < title.length; i++) {
            if (title[i].equals(GeometryField)) {
                for (int j = 0; j < map.size(); j++) {
                    List<String[]> s = db.parseCoordinate(i, map.get(j));
                    list.add(s);
                }
                exceptionindex = i;
                break;
            }
        }
        dshp.addRecord(dv, map, title, exceptionindex, "Region", list);
        wsh.getWorksapce().save();
        wsh.getWorksapce().close();
        wsh.getWorksapce().dispose();
        return "successful";
    }

    @Override
    public String importPoiData(String filepath, String datasetName) {
        WorkspaceHelper wsh = new WorkspaceHelper();
        wsh.openWorkspaceSXW(workspacepath, "");
        Datasource datasource = wsh.getWorksapce().getDatasources()
                .get("XYDATA");

        String[] title = null;
        Map<Integer, Map<Integer, Object>> map = null;
        try {
            DataGetfromXlsx excelReader = new DataGetfromXlsx(filepath);
            // 对读取Excel表格标题测试
            title = excelReader.readExcelTitle();
            // 对读取Excel表格内容测试
            map = excelReader.readExcelContent();

            DataInfoBuliding db = new DataInfoBuliding();
            DatasetHelper dshp = new DatasetHelper();
            DatasetVector dv = dshp.createDataset("点数据集", datasetName,
                    datasource);
            List<Map<String, Object>> mapfield = db.parsefieldinfo(title);
            dshp.addFieldInfo(dv.getFieldInfos(), mapfield);
            List<List<String[]>> list = new ArrayList<>();
            int exceptionindex = -1;
            int xindex = -1;
            int yindex = -1;
            for (int i = 0; i < title.length; i++) {
                if (title[i].equals("LONGITUDE")) {
                    xindex = i;
                } else if (title[i].equals("LATITUDE")) {
                    yindex = i;
                }
            }
            for (int i = 0; i < map.size(); i++) {
                list.add(db.parseCoordinatepoi(xindex, yindex, map.get(i)));
            }
            dshp.addRecord(dv, map, title, exceptionindex, "Point", list);
            wsh.getWorksapce().save();
            wsh.getWorksapce().close();
            wsh.getWorksapce().dispose();
            return "successful";
        } catch (FileNotFoundException e) {
            System.out.println("未找到指定路径的文件!");
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public void setProviderContext(ProviderContext context) {
        // TODO Auto-generated method stub
        FileSetting file = context.getConfig(FileSetting.class);
        this.filePath = file.getFilePath();
    }

    @Override
    public String CreateWorkspace(String filepath, String Name) {
        return Createworkspace(filepath, Name);
    }

}
