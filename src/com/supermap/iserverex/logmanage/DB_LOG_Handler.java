package com.supermap.iserverex.logmanage;


import com.supermap.iserverex.utils.DBUtil;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DB_LOG_Handler {
    private Connection conn = null;

    public DB_LOG_Handler() {
        conn = DBUtil.getConn();
    }

    public void close() {
        DBUtil.close(conn);
    }

    public int[] BatchInsert(List<DB_LOG_FeatureLogWithBLOBs> oplogs) {
        int[] results = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO FEATUREOPLOG";
        sql += " (ID,OPERATIONTIME,OPROLE,OPSERVER,OPWORKSPACE,OPDATASOURCE,OPDATASET,OPFEATUREID,OLD_OPINFO,NEW_OPINFO,OP_TYPE)";
        sql += " values(?,?,?,?,?,?,?,?,?,?,?)";
        try {
            pstmt = conn.prepareStatement(sql);
            for (DB_LOG_FeatureLogWithBLOBs oplog : oplogs) {
                pstmt.setString(1, oplog.getID());
                pstmt.setTimestamp(2, new Timestamp(oplog.getOPERATIONTIME()
                        .getTime()));
                pstmt.setString(3, oplog.getOPROLE());
                pstmt.setString(4, oplog.getOPSERVER());
                pstmt.setString(5, oplog.getOPWORKSPACE());
                pstmt.setString(6, oplog.getOPDATASOURCE());
                pstmt.setString(7, oplog.getOPDATASET());
                pstmt.setString(8, oplog.getOPFEATUREID());
                try {
                    pstmt.setBlob(9,
                            new ByteArrayInputStream(oplog.getOLD_OPINFO()));
                    pstmt.setBlob(10,
                            new ByteArrayInputStream(oplog.getNEW_OPINFO()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                pstmt.setString(11, oplog.getOP_TYPE());
                pstmt.addBatch();
            }
            results = pstmt.executeBatch();
            DBUtil.commit(conn);
        } catch (Exception e) {
            e.printStackTrace();
            DBUtil.rollback(conn);

        } finally {
            DBUtil.close(pstmt);
        }
        return results;
    }

    public void delete(String id) {
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM FEATUREOPLOG WHERE id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            DBUtil.commit(conn);
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt);
        }
    }

    public int[] BatchUpdate(List<DB_LOG_FeatureLogWithBLOBs> oplogs) {
        int[] results = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE FEATUREOPLOG ";
        sql += "SET ID=?,SET OPERATIONTIME=?,SET OPROLE=?,SET OPSERVER=?,SET OPWORKSPACE=?,SET OPDATASOURCE=?,";
        sql += "SET OPDATASET=?,SET OPFEATUREID=?,SET OLD_OPINFO=?,SET NEW_OPINFO=?,SET OP_TYPE=?)";
        try {
            pstmt = conn.prepareStatement(sql);
            for (DB_LOG_FeatureLogWithBLOBs oplog : oplogs) {
                pstmt.setString(1, oplog.getID());
                pstmt.setTimestamp(2, new Timestamp(oplog.getOPERATIONTIME()
                        .getTime()));
                pstmt.setString(3, oplog.getOPROLE());
                pstmt.setString(4, oplog.getOPSERVER());
                pstmt.setString(5, oplog.getOPWORKSPACE());
                pstmt.setString(6, oplog.getOPDATASOURCE());
                pstmt.setString(7, oplog.getOPDATASET());
                pstmt.setString(8, oplog.getOPFEATUREID());
                pstmt.setBlob(9,
                        new ByteArrayInputStream(oplog.getOLD_OPINFO()));
                pstmt.setBlob(10,
                        new ByteArrayInputStream(oplog.getNEW_OPINFO()));
                pstmt.setString(11, oplog.getOP_TYPE());
                pstmt.addBatch();
            }
            results = pstmt.executeBatch();
            DBUtil.commit(conn);
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt);
        }
        return results;
    }

    public List<DB_LOG_FeatureLogWithBLOBs> queryall() {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        String sql = "SELECT * FROM FEATUREOPLOG ";
        List<DB_LOG_FeatureLogWithBLOBs> oplogList = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);

            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                DB_LOG_FeatureLogWithBLOBs oplog = new DB_LOG_FeatureLogWithBLOBs();
                oplog.setID(resultSet.getString("ID"));
                oplog.setOPERATIONTIME(resultSet.getTimestamp("OPERATIONTIME"));
                oplog.setOPROLE(resultSet.getString("OPROLE"));
                oplog.setOPSERVER(resultSet.getString("OPSERVER"));
                oplog.setOPWORKSPACE(resultSet.getString("OPWORKSPACE"));
                oplog.setOPDATASOURCE(resultSet.getString("OPDATASOURCE"));
                oplog.setOPDATASET(resultSet.getString("OPDATASET"));
                oplog.setOPFEATUREID(resultSet.getString("OPFEATUREID"));
                Blob ob = resultSet.getBlob("OLD_OPINFO");
                Blob nb = resultSet.getBlob("NEW_OPINFO");
                if (ob != null) {
                    oplog.setOLD_OPINFO(ob.getBytes(1, (int) ob.length()));
                }
                if (nb != null) {
                    oplog.setNEW_OPINFO(nb.getBytes(1, (int) nb.length()));
                }
                oplog.setOLD_OPINFO(ob.getBytes(1, (int) Objects.requireNonNull(ob).length()));
                oplog.setNEW_OPINFO(nb.getBytes(1, (int) Objects.requireNonNull(nb).length()));
                oplog.setOP_TYPE(resultSet.getString("OP_TYPE"));
                oplogList.add(oplog);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(resultSet);
            DBUtil.close(pstmt);
        }
        return oplogList;
    }

    public List<DB_LOG_FeatureLogWithBLOBs> query(String sql) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        List<DB_LOG_FeatureLogWithBLOBs> oplogList = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                DB_LOG_FeatureLogWithBLOBs oplog = new DB_LOG_FeatureLogWithBLOBs();
                oplog.setID(resultSet.getString("ID"));
                oplog.setOPERATIONTIME(resultSet.getTimestamp("OPERATIONTIME"));
                oplog.setOPROLE(resultSet.getString("OPROLE"));
                oplog.setOPSERVER(resultSet.getString("OPSERVER"));
                oplog.setOPWORKSPACE(resultSet.getString("OPWORKSPACE"));
                oplog.setOPDATASOURCE(resultSet.getString("OPDATASOURCE"));
                oplog.setOPDATASET(resultSet.getString("OPDATASET"));
                oplog.setOPFEATUREID(resultSet.getString("OPFEATUREID"));
                Blob ob = resultSet.getBlob("OLD_OPINFO");
                Blob nb = resultSet.getBlob("NEW_OPINFO");
                if (ob != null) {
                    oplog.setOLD_OPINFO(ob.getBytes(1, (int) ob.length()));
                }
                if (nb != null) {
                    oplog.setNEW_OPINFO(nb.getBytes(1, (int) nb.length()));
                }
                oplog.setOP_TYPE(resultSet.getString("OP_TYPE"));
                oplogList.add(oplog);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(resultSet);
            DBUtil.close(pstmt);
        }
        return oplogList;
    }
}
