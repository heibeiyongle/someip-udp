package com.holomatic.holopilotparking.soa.module.holodto;

/**
 * @author 比才-贾硕哲
 * @time 18/4/2024 19:33
 * @desc
 */
public class HoloDefPilotCommand {

    public byte cmdType;
    public long cmdValue;

    public byte getCmdType() {
        return cmdType;
    }

    public long getCmdValue() {
        return cmdValue;
    }

    public void setCmdType(byte cmdType) {
        this.cmdType = cmdType;
    }

    public void setCmdValue(long cmdValue) {
        this.cmdValue = cmdValue;
    }
}
