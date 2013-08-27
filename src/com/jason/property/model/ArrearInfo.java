package com.jason.property.model;

/**
 * 用户欠费信息
 * 
 */
public class ArrearInfo {
    private int inputTableId;

    private int objectType;

    private int objectID;

    private double price;

    private double amount;

    private double startDegree;

    private double endDegree;

    private String payStartDate;

    private String payEndDate;

    private int status;

    private int feeType;

    private String name;

    // 默认数量为1
    private int count = 1;

    private int feeStandardID;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getStartDegree() {
        return startDegree;
    }

    public void setStartDegree(double startDegree) {
        this.startDegree = startDegree;
    }

    public double getEndDegree() {
        return endDegree;
    }

    public void setEndDegree(double endDegree) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFeeStandardID() {
        return feeStandardID;
    }

    public void setFeeStandardID(int feeStandardID) {
        this.feeStandardID = feeStandardID;
    }
}
