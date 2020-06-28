package com.example.archimax;

public class serialinfo {
    public String devicename ,deviceserial;

    public serialinfo(){

    }
    public serialinfo(String devicename, String deviceserial) {
        this.devicename = devicename;
        this.deviceserial = deviceserial;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDeviceserial() {
        return deviceserial;
    }

    public void setDeviceserial(String deviceserial) {
        this.deviceserial = deviceserial;
    }
}
