package com.srknzl.PiRobot;

public class FoundDeviceModel
{
    String name;
    String address;

    //constructor
    public FoundDeviceModel(String name, String address) {
        this.name = name;
        this.address = address;
    }
    //getters

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }
}
