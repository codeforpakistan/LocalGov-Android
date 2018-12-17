package com.zeeroapps.wssp.Model;

public class AdminComplaintsModel {

    private String cNumber;
    private String cType;
    private String cDetail;
    private String cAddress;
    private String cStatus;
    private String cImageUrl;
    private String cDateAndTime;

    public String getcNumber() {
        return cNumber;
    }

    public void setcNumber(String cNumber) {
        this.cNumber = cNumber;
    }

    public String getcType() {
        return cType;
    }

    public void setcType(String cType) {
        this.cType = cType;
    }

    public String getcDetail() {
        return cDetail;
    }

    public void setcDetail(String cDetail) {
        this.cDetail = cDetail;
    }

    public String getcAddress() {
        return cAddress;
    }

    public void setcAddress(String cAddress) {
        this.cAddress = cAddress;
    }

    public String getcStatus() {
        return cStatus;
    }

    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    public String getcImageUrl() {
        return cImageUrl;
    }

    public void setcImageUrl(String cImageUrl) {
        this.cImageUrl = cImageUrl;
    }

    public String getcDateAndTime() {
        return cDateAndTime;
    }

    public void setcDateAndTime(String cDateAndTime) {
        this.cDateAndTime = cDateAndTime;
    }
}
