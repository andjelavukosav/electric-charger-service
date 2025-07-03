package com.example.sensor.service;

import com.example.sensor.model.ElectricCharger;

public interface ChargingDataSender {
    void sendChargingData(ElectricCharger charger);
}
