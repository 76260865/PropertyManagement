package com.jason.property.model;

import java.util.ArrayList;

public class RoomInfo {
    private int roomId;

    private String roomCode;

    private double buildArea;

    private double useArea;

    private String ownerName;
    
    private String receiveDate;

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    private ArrayList<Equipment> equipments = new ArrayList<Equipment>();

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public double getBuildArea() {
        return buildArea;
    }

    public void setBuildArea(double buildArea) {
        this.buildArea = buildArea;
    }

    public double getUseArea() {
        return useArea;
    }

    public void setUseArea(double useArea) {
        this.useArea = useArea;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public ArrayList<Equipment> getEquipments() {
        return equipments;
    }
}
