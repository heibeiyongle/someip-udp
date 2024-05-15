package com.holomatic.holopilotparking.soa.module.holodto;

/**
 * @author 比才-贾硕哲
 * @time 18/4/2024 19:22
 * @desc
 */
public class HoloDefVehicleInfo {

    public byte Vehicle_HighLamp;
    public byte Vehicle_LowLamp;
    public byte Vehicle_ClearanceLamp;
    public byte Vehicle_BrakeLamp;
    public byte Vehicle_TurnLeftLamp;
    public byte Vehicle_TurnRightLamp;
    public byte Vehicle_GearInfo;
    public byte Vehicle_Speed;
    public byte Vehicle_TimeGap;

    public byte getVehicle_HighLamp() {
        return Vehicle_HighLamp;
    }

    public byte getVehicle_LowLamp() {
        return Vehicle_LowLamp;
    }

    public byte getVehicle_ClearanceLamp() {
        return Vehicle_ClearanceLamp;
    }

    public byte getVehicle_BrakeLamp() {
        return Vehicle_BrakeLamp;
    }

    public byte getVehicle_TurnLeftLamp() {
        return Vehicle_TurnLeftLamp;
    }

    public byte getVehicle_TurnRightLamp() {
        return Vehicle_TurnRightLamp;
    }

    public byte getVehicle_GearInfo() {
        return Vehicle_GearInfo;
    }

    public byte getVehicle_Speed() {
        return Vehicle_Speed;
    }

    public byte getVehicle_TimeGap() {
        return Vehicle_TimeGap;
    }

    @Override
    public String toString() {
        return "HoloDefVehicleInfo{" +
                "Vehicle_HighLamp=" + Vehicle_HighLamp +
                ", Vehicle_LowLamp=" + Vehicle_LowLamp +
                ", Vehicle_ClearanceLamp=" + Vehicle_ClearanceLamp +
                ", Vehicle_BrakeLamp=" + Vehicle_BrakeLamp +
                ", Vehicle_TurnLeftLamp=" + Vehicle_TurnLeftLamp +
                ", Vehicle_TurnRightLamp=" + Vehicle_TurnRightLamp +
                ", Vehicle_GearInfo=" + Vehicle_GearInfo +
                ", Vehicle_Speed=" + Vehicle_Speed +
                ", Vehicle_TimeGap=" + Vehicle_TimeGap +
                '}';
    }
}
