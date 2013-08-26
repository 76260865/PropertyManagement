package com.jason.property.model;

/**
 * 用户欠费信息
 * 
 */
public class ArrearInfo {
    private int inputTableId;

    private int objectType;

    private int objectID;

    private float price;

    private float amount;

    private float startDegree;

    private float endDegree;

    private String payStartDate;

    private String payEndDate;

    private int status;

    private int feeType;

    public int getInputTableId() {
        return inputTableId;
    }

    public void setInputTableId(int inputTableId) {
        this.inputTableId = inputTableId;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public int getObjectID() {
        return objectID;
    }

    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getStartDegree() {
        return startDegree;
    }

    public void setStartDegree(float startDegree) {
        this.startDegree = startDegree;
    }

    public float getEndDegree() {
        return endDegree;
    }

    public void setEndDegree(float endDegree) {
        this.endDegree = endDegree;
    }

    public String getPayStartDate() {
        return payStartDate;
    }

    public void setPayStartDate(String payStartDate) {
        this.payStartDate = payStartDate;
    }

    public String getPayEndDate() {
        return payEndDate;
    }

    public void setPayEndDate(String payEndDate) {
        this.payEndDate = payEndDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFeeType() {
        return feeType;
    }

    public void setFeeType(int feeType) {
        this.feeType = feeType;
    }

}
