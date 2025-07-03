package com.example.sensor.service;

import com.example.sensor.model.ElectricCharger;

public interface ElectricChargerService {
    void simulateCharging();
    ElectricCharger generateACCharger(String chargerId);
    ElectricCharger generateDCCharger(String chargerId);
}
