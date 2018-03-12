package com.supermap.spscsi.spatialdataop;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Workspace;

public class DatasourceHelper {
    private Workspace m_workspace;

    /**
     * 构造 SampleRun对象
     */
    public DatasourceHelper(Workspace workspace) {
        this.m_workspace = workspace;
    }

    /**
     * 打开udb数据源
     */
    public Datasource openDatasourceUdbPlus(String path) {
        try {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setEngineType(EngineType.UDB);
            info.setServer(path);
            Datasource ds = m_workspace.getDatasources().open(info);
            return ds;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 打开oracle数据源
     */
    public Datasource openDatasourceOracle(String server, String database,
                                           String userName, String password) {
        try {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setEngineType(EngineType.ORACLEPLUS);
            info.setServer(server);
            info.setDatabase(database);
            info.setUser(userName);
            info.setPassword(password);
            Datasource ds = m_workspace.getDatasources().open(info);
            return ds;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 打开SQL server数据源
     */
    public Datasource openDatasourceSqlServer(String server, String database,
                                              String userName, String password) {
        try {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setEngineType(EngineType.SQLPLUS);
            info.setServer(server);
            info.setDatabase(database);
            info.setUser(userName);
            info.setPassword(password);
            info.setDriver("SQL SERVER");
            Datasource ds = m_workspace.getDatasources().open(info);
            return ds;
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * 打开 ISERVERREST 数据源
     */
    public Datasource openDatasourceISERVERREST(String path) {
        try {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setEngineType(EngineType.ISERVERREST);
            info.setServer(path);
            Datasource ds = m_workspace.getDatasources().open(info);
            return ds;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 创建udb数据源
     */
    public Datasource createDatasourceUdbPlus(String path, String database) {
        try {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setEngineType(EngineType.UDB);
            info.setServer(path + "/" + database);
            Datasource ds = m_workspace.getDatasources().create(info);
            return ds;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 创建Oracle数据源
     */
    public Datasource createDatasourceOracle(String server, String database,
                                             String userName, String password) {
        try {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setEngineType(EngineType.ORACLEPLUS);
            info.setServer(server);
            info.setDatabase(database);
            info.setUser(userName);
            info.setPassword(password);
            Datasource ds = m_workspace.getDatasources().create(info);
            return ds;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 创建SQL Server数据源
     */
    public Datasource createDatasourceSqlServer(String server, String database,
                                                String userName, String password) {
        try {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setEngineType(EngineType.SQLPLUS);
            info.setServer(server);
            info.setDatabase(database);
            info.setUser(userName);
            info.setPassword(password);
            info.setDriver("SQL SERVER");
            Datasource ds = m_workspace.getDatasources().create(info);
            return ds;
        } catch (Exception ex) {
            return null;
        }
    }

}
