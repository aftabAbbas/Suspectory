package com.aftab.suspectory.Model;

public class DeviceInfo {

    private String deviceId, deviceName, sim1Display, sim2Display, sim1Num, sim2Num;

    public DeviceInfo() {
    }

    public DeviceInfo(String deviceId, String deviceName, String sim1Display, String sim2Display, String sim1Num, String sim2Num) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.sim1Display = sim1Display;
        this.sim2Display = sim2Display;
        this.sim1Num = sim1Num;
        this.sim2Num = sim2Num;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSim1Display() {
        return sim1Display;
    }

    public void setSim1Display(String sim1Display) {
        this.sim1Display = sim1Display;
    }

    public String getSim2Display() {
        return sim2Display;
    }

    public void setSim2Display(String sim2Display) {
        this.sim2Display = sim2Display;
    }

    public String getSim1Num() {
        return sim1Num;
    }

    public void setSim1Num(String sim1Num) {
        this.sim1Num = sim1Num;
    }

    public String getSim2Num() {
        return sim2Num;
    }

    public void setSim2Num(String sim2Num) {
        this.sim2Num = sim2Num;
    }
}
