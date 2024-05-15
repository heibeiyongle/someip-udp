package com.holomatic.holopilotparking.soa.module.holodto;

/**
 * @author 比才-贾硕哲
 * @time 18/4/2024 19:22
 * @desc
 */
public class HoloDefUserSettings {

    public byte ASLState;
    public byte VoiceStyleState;
    public byte NDAInhibitionState;
    public byte AccAutoState;

    public byte getASLState() {
        return ASLState;
    }

    public void setASLState(byte ASLState) {
        this.ASLState = ASLState;
    }

    public byte getVoiceStyleState() {
        return VoiceStyleState;
    }

    public void setVoiceStyleState(byte voiceStyleState) {
        VoiceStyleState = voiceStyleState;
    }

    public byte getNDAInhibitionState() {
        return NDAInhibitionState;
    }

    public void setNDAInhibitionState(byte NDAInhibitionState) {
        this.NDAInhibitionState = NDAInhibitionState;
    }

    public byte getAccAutoState() {
        return AccAutoState;
    }

    public void setAccAutoState(byte accAutoState) {
        AccAutoState = accAutoState;
    }

    @Override
    public String toString() {
        return "HoloDefUserSettings{" +
                "ASLState=" + ASLState +
                ", VoiceStyleState=" + VoiceStyleState +
                ", NDAInhibitionState=" + NDAInhibitionState +
                ", AccAutoState=" + AccAutoState +
                '}';
    }
}
