package com.jason.property.model;

import java.util.ArrayList;

public class EmployeeFeeStandards {
    private String employeeId;

    private int areaId;

    private ArrayList<StandardFee> standardFees = new ArrayList<StandardFee>();

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public ArrayList<StandardFee> getStandardFees() {
        return standardFees;
    }

}
