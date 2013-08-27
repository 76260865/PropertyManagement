package com.jason.property.model;

public class StandardFee {

    private int feeStandardID;

    private String name;

    private int feeType;

    private double price;

    private int companyID;

    private int areaID;

    private int relationArea;

    public int getFeeStandardID() {
        return feeStandardID;
    }

    public void setFeeStandardID(int feeStandardID) {
        this.feeStandardID = feeStandardID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFeeType() {
        return feeType;
    }

    public void setFeeType(int feeType) {
        this.feeType = feeType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getAreaID() {
        return areaID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public int getRelationArea() {
        return relationArea;
    }

    public void setRelationArea(int relationArea) {
        this.relationArea = relationArea;
    }

}
