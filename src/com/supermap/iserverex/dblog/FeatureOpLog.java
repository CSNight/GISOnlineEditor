package com.supermap.iserverex.dblog;

import java.util.Date;

public class FeatureOpLog {
    private String ID;

    private Date OPERATIONTIME;

    private String OPROLE;

    private String OPSERVER;

    private String OPWORKSPACE;

    private String OPDATASOURCE;

    private String OPDATASET;

    private String OPFEATUREID;

    private String OP_TYPE;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID == null ? null : ID.trim();
    }

    public Date getOPERATIONTIME() {
        return OPERATIONTIME;
    }

    public void setOPERATIONTIME(Date OPERATIONTIME) {
        this.OPERATIONTIME = OPERATIONTIME;
    }

    public String getOPROLE() {
        return OPROLE;
    }

    public void setOPROLE(String OPROLE) {
        this.OPROLE = OPROLE == null ? null : OPROLE.trim();
    }

    public String getOPSERVER() {
        return OPSERVER;
    }

    public void setOPSERVER(String OPSERVER) {
        this.OPSERVER = OPSERVER == null ? null : OPSERVER.trim();
    }

    public String getOPWORKSPACE() {
        return OPWORKSPACE;
    }

    public void setOPWORKSPACE(String OPWORKSPACE) {
        this.OPWORKSPACE = OPWORKSPACE == null ? null : OPWORKSPACE.trim();
    }

    public String getOPDATASOURCE() {
        return OPDATASOURCE;
    }

    public void setOPDATASOURCE(String OPDATASOURCE) {
        this.OPDATASOURCE = OPDATASOURCE == null ? null : OPDATASOURCE.trim();
    }

    public String getOPDATASET() {
        return OPDATASET;
    }

    public void setOPDATASET(String OPDATASET) {
        this.OPDATASET = OPDATASET == null ? null : OPDATASET.trim();
    }

    public String getOPFEATUREID() {
        return OPFEATUREID;
    }

    public void setOPFEATUREID(String OPFEATUREID) {
        this.OPFEATUREID = OPFEATUREID == null ? null : OPFEATUREID.trim();
    }

    public String getOP_TYPE() {
        return OP_TYPE;
    }

    public void setOP_TYPE(String OP_TYPE) {
        this.OP_TYPE = OP_TYPE == null ? null : OP_TYPE.trim();
    }
}