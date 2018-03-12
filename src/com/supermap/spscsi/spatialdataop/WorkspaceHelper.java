package com.supermap.spscsi.spatialdataop;

import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.data.WorkspaceVersion;

public class WorkspaceHelper {
    private Workspace m_workspace;

    /**
     * 根据workspace和map构造 SampleRun对象
     */
    public WorkspaceHelper(Workspace workspace) {
        this.m_workspace = workspace;
    }

    public WorkspaceHelper() {
    }

    public Workspace getWorksapce() {
        return m_workspace;
    }

    /**
     * 打开文件型的工作空间
     */
    @SuppressWarnings("deprecation")
    public boolean openWorkspaceSXW(String filePath, String password) {
        m_workspace = new Workspace();

        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();
        connectionInfo.setServer(filePath);
        connectionInfo.setPassword(password);
        if (filePath.endsWith(".sxw")) {
            connectionInfo.setType(WorkspaceType.SXW);
        } else if (filePath.endsWith(".smw")) {
            connectionInfo.setType(WorkspaceType.SMW);
        } else if (filePath.endsWith(".sxwu")) {
            connectionInfo.setType(WorkspaceType.SXWU);
        } else if (filePath.endsWith(".smwu")) {
            connectionInfo.setType(WorkspaceType.SMWU);
        }
        return m_workspace.open(connectionInfo);
    }

    /**
     * 打开数据库型的工作空间
     */
    public boolean openWorkspaceSQL(String Server, String Database,
                                    String user, String password, String WorkspaceName, int type) {
        m_workspace.dispose();
        m_workspace = new Workspace();

        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();
        if (type == 1) {
            connectionInfo.setType(WorkspaceType.ORACLE);
        } else {
            connectionInfo.setDriver("SQL SERVER");
            connectionInfo.setType(WorkspaceType.SQL);
        }

        connectionInfo.setServer(Server);
        connectionInfo.setDatabase(Database);
        connectionInfo.setUser(user);
        connectionInfo.setPassword(password);
        connectionInfo.setName(WorkspaceName);
        return m_workspace.open(connectionInfo);
    }

    /**
     * 创建文件型的工作空间
     */
    @SuppressWarnings("deprecation")
    public boolean createWorkspaceSXW(String filePath, String WorkspaceName,
                                      String Password, int Version, int workType) {

        m_workspace.dispose();
        m_workspace = new Workspace();
        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();

        String server = filePath + WorkspaceName;

        if (workType == 0) {
            server += ".sxw";
            connectionInfo.setType(WorkspaceType.SXW);
        }
        if (workType == 1) {
            server += ".smw";
            connectionInfo.setType(WorkspaceType.SMW);
        }
        if (workType == 2) {
            server += ".sxwu";
            connectionInfo.setType(WorkspaceType.SXWU);
        }
        if (workType == 3) {
            server += ".smwu";
            connectionInfo.setType(WorkspaceType.SMWU);
        }

        if (Version == 0) {
            connectionInfo.setVersion(WorkspaceVersion.UGC60);
        } else if (Version == 1) {
            connectionInfo.setVersion(WorkspaceVersion.UGC20);
        } else {
            connectionInfo.setVersion(WorkspaceVersion.SFC60);
        }

        connectionInfo.setServer(server);
        connectionInfo.setPassword(Password);

        return m_workspace.create(connectionInfo);

    }

    /**
     * 创建数据库型的工作空间
     */
    public boolean createWorkspaceSQL(String Server, String Database,
                                      String UserName, String Password, String WorkspaceName,
                                      int Version, int type) {
        m_workspace.dispose();
        m_workspace = new Workspace();
        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();

        if (type == 1) {
            connectionInfo.setType(WorkspaceType.ORACLE);
        } else {
            connectionInfo.setDriver("SQL SERVER");
            connectionInfo.setType(WorkspaceType.SQL);
        }

        connectionInfo.setServer(Server);
        connectionInfo.setDatabase(Database);
        connectionInfo.setUser(UserName);
        connectionInfo.setPassword(Password);
        connectionInfo.setName(WorkspaceName);

        if (Version == 0) {
            connectionInfo.setVersion(WorkspaceVersion.UGC60);
        } else if (Version == 1) {
            connectionInfo.setVersion(WorkspaceVersion.UGC20);
        } else {
            connectionInfo.setVersion(WorkspaceVersion.SFC60);
        }

        return m_workspace.create(connectionInfo);
    }

    /**
     * 删除文件型的工作空间
     */
    @SuppressWarnings("deprecation")
    public boolean deleteWorkspaceSXW(String Server, String Password,
                                      int workType) {

        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();
        String filePath = Server;

        if (workType == 0) {
            connectionInfo.setType(WorkspaceType.SXW);
        }
        if (workType == 1) {
            connectionInfo.setType(WorkspaceType.SMW);
        }
        if (workType == 2) {
            connectionInfo.setType(WorkspaceType.SXWU);
        }
        if (workType == 3) {
            connectionInfo.setType(WorkspaceType.SMWU);
        }

        connectionInfo.setServer(filePath);
        connectionInfo.setPassword(Password);

        return Workspace.deleteWorkspace(connectionInfo);

    }

    /**
     * 删除数据库型的工作空间
     */
    public boolean deleteWorkspaceSQL(String Server, String Database,
                                      String UserName, String Password, String WorkspaceName, int type) {
        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();

        if (type == 1) {
            connectionInfo.setType(WorkspaceType.ORACLE);
        } else {
            connectionInfo.setDriver("SQL SERVER");
            connectionInfo.setType(WorkspaceType.SQL);
        }

        connectionInfo.setServer(Server);
        connectionInfo.setDatabase(Database);
        connectionInfo.setUser(UserName);
        connectionInfo.setPassword(Password);
        connectionInfo.setName(WorkspaceName);

        return Workspace.deleteWorkspace(connectionInfo);

    }

}
