package com.jason.property.data;

import java.util.ArrayList;

import com.jason.property.model.ArrearInfo;
import com.jason.property.model.EmployeeFeeStandards;
import com.jason.property.model.RoomInfo;
import com.jason.property.model.UserInfo;

public class PropertyService {
    private static final String TAG = "PropertyService";

    private static class SingletonHolder {
        static final PropertyService INSTANCE = new PropertyService();
    }

    private UserInfo mUserInfo;

    private EmployeeFeeStandards mEmployeeFeeStandards;

    /** 房间信息 */
    private RoomInfo mRoomInfo;

    /** 欠费信息 */
    public ArrayList<ArrearInfo> Arrears = new ArrayList<ArrearInfo>();

    /** 临时费用信息 */
    public ArrayList<ArrearInfo> TempArrears = new ArrayList<ArrearInfo>();

    private PropertyService() {
    }

    public static PropertyService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public EmployeeFeeStandards getEmployeeFeeStandards() {
        return mEmployeeFeeStandards;
    }

    public void setEmployeeFeeStandards(EmployeeFeeStandards employeeFeeStandards) {
        this.mEmployeeFeeStandards = employeeFeeStandards;
    }

    public RoomInfo getRoomInfo() {
        return mRoomInfo;
    }

    public void setRoomInfo(RoomInfo roomInfo) {
        this.mRoomInfo = roomInfo;
    }

    /**
     * clear the current data
     */
    public void clear() {
        mUserInfo.getAreas().clear();
        mUserInfo = null;
        mEmployeeFeeStandards.getStandardFees().clear();
        mEmployeeFeeStandards = null;
    }
}
