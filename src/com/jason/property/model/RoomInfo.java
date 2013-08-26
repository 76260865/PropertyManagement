package com.jason.property.model;

import java.util.ArrayList;

public class RoomInfo {
    private int roomId;

    private String roomCode;

    private float buildArea;

    private float useArea;

    private String ownerName;

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

    public float getBuildArea() {
        return buildArea;
    }

    public void setBuildArea(float buildArea) {
        this.buildArea = buildArea;
    }

    public float getUseArea() {
        return useArea;
    }

    public void setUseArea(float useArea) {
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
