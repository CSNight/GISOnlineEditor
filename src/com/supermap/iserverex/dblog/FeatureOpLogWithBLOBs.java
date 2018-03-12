package com.supermap.iserverex.dblog;

public class FeatureOpLogWithBLOBs extends FeatureOpLog {
    private byte[] OLD_OPINFO;

    private byte[] NEW_OPINFO;

    public byte[] getOLD_OPINFO() {
        return OLD_OPINFO;
    }

    public void setOLD_OPINFO(byte[] OLD_OPINFO) {
        this.OLD_OPINFO = OLD_OPINFO;
    }

    public byte[] getNEW_OPINFO() {
        return NEW_OPINFO;
    }

    public void setNEW_OPINFO(byte[] NEW_OPINFO) {
        this.NEW_OPINFO = NEW_OPINFO;
    }
}