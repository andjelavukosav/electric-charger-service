package com.example.sensor.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectricCharger implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deviceId;
    private String type;
    private Double maxPowerKw;
    private Instant startChargingTime;
    private Instant endChargingTime;
    private ChargingStatus status;
    private double currentA;
    private double voltageV;
    private List<String> connectorTypes;


    public ElectricCharger() {}

    public ElectricCharger(String deviceId, Instant startChargingTime, Instant endChargingTime, double currentA, ChargingStatus status, double voltageV, double maxPowerKw, String type, List<String> connectorTypes) {
        this.deviceId = deviceId;
        this.startChargingTime = startChargingTime;
        this.endChargingTime = endChargingTime;
        this.currentA = currentA;
        this.status = status;
        this.voltageV = voltageV;
        this.type = type;
        this.maxPowerKw = maxPowerKw;
        this.connectorTypes = connectorTypes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getMaxPowerKw() {
        return maxPowerKw;
    }

    public void setMaxPowerKw(Double maxPowerKw) {
        this.maxPowerKw = maxPowerKw;
    }

    public List<String> getConnectorTypes() {
        return connectorTypes;
    }

    public void setConnectorTypes(List<String> connectorTypes) {
        this.connectorTypes = connectorTypes;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public ChargingStatus getStatus() {
        return status;
    }

    public double getCurrentA() {
        return currentA;
    }

    public double getVoltageV() {
        return voltageV;
    }

    public Instant getEndChargingTime() {
        return endChargingTime;
    }

    public void setEndChargingTime(Instant endChargingTime) {
        this.endChargingTime = endChargingTime;
    }

    public Instant getStartChargingTime() {
        return startChargingTime;
    }

    public void setStartChargingTime(Instant startChargingTime) {
        this.startChargingTime = startChargingTime;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setStatus(ChargingStatus status) {
        this.status = status;
    }

    public void setCurrentA(double currentA) {
        this.currentA = currentA;
    }

    public void setVoltageV(double voltageV) {
        this.voltageV = voltageV;
    }

    @Override
    public String toString() {
        return "ElectricCharger{" +
                "deviceId='" + deviceId + '\'' +
                ", type='" + type + '\'' +
                ", maxPowerKw=" + maxPowerKw +
                ", startChargingTime=" + startChargingTime +
                ", endChargingTime=" + endChargingTime +
                ", status=" + status +
                ", currentA=" + currentA +
                ", voltageV=" + voltageV +
                ", connectorTypes=" + connectorTypes +
                '}';
    }
}
