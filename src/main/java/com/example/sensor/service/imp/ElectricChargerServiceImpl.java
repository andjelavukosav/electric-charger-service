package com.example.sensor.service.imp;

import com.example.sensor.model.ChargingStatus;
import com.example.sensor.model.ElectricCharger;
import com.example.sensor.service.ChargingDataSender;
import com.example.sensor.service.ElectricChargerService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class ElectricChargerServiceImpl implements ElectricChargerService {

    private final Random random = new Random();
    private final List<ElectricCharger> chargers = new ArrayList<>();

    @Autowired
    private ChargingDataSender chargingDataSender;

    @Override
    public ElectricCharger generateACCharger(String chargerId) {
        // Definisanje mogućih snaga za AC punjače u Evropi
        Double[] acPowers = {3.7, 7.4, 11.0, 22.0};
        Double maxPowerKw = acPowers[random.nextInt(acPowers.length)];

        Integer voltageV;
        Integer currentA;
        List<String> connectorTypes = new ArrayList<>(); // Inicijalizujemo kao praznu listu

        if (maxPowerKw == 3.7) {
            voltageV = 230;
            currentA = 16; // Oko 16A
        } else if (maxPowerKw == 7.4) {
            voltageV = 230;
            currentA = 32; // Oko 32A
        } else if (maxPowerKw == 11.0) { // Trofazni
            voltageV = 400;
            currentA = 16; // Oko 16A po fazi
        } else { // 22.0 kW, Trofazni
            voltageV = 400;
            currentA = 32; // Oko 32A po fazi
        }
        // Većina punjača je Type 2
        connectorTypes.add("Type 2");

        // Ako je snaga 7.4 kW ili manja (što je podržano od Type 1)
        // I ako nasumično odlučimo (npr. 20% šanse) da dodamo i Type 1 konektor
        if (maxPowerKw <= 7.4 && random.nextDouble() < 0.20) { // 20% šanse da ima i Type 1
            connectorTypes.add("Type 1"); // Dodajemo Type 1 uz Type 2
        }

        // Kreiranje AC punjača
        return new ElectricCharger(
                chargerId,
                null,
                null,
                currentA,
                ChargingStatus.IDLE,
                voltageV,
                maxPowerKw,
                "AC",
                connectorTypes
        );
    }

    @Override
    public ElectricCharger generateDCCharger(String chargerId) {
        // Definisanje mogućih snaga za DC punjače
        Double[] dcPowers = {50.0, 75.0, 100.0, 150.0, 250.0, 350.0};
        Double maxPowerKw = dcPowers[random.nextInt(dcPowers.length)];

        Integer voltageV;
        Integer currentA;
        List<String> connectorTypes = new ArrayList<>();

        // Svi DC punjači u Evropi trebali bi podržavati CCS Combo 2
        connectorTypes.add("CCS Combo 2");

        if (random.nextBoolean()) { // 50% šanse da punjač ima i CHAdeMO
            connectorTypes.add("CHAdeMO");
        }

        // Određivanje napona i struje na osnovu snage
        // Simuliramo širi opseg napona koji punjač može podržati (od 400V do 900V)
        // Manji punjači su često bliže 400V, veći podržavaju i 800V sisteme
        if (maxPowerKw <= 100.0) {
            voltageV = random.nextInt(201) + 400; // Opseg 400V - 600V
        } else {
            voltageV = random.nextInt(501) + 400; // Opseg 400V - 900V
        }

        // Izračunavanje struje: I = P / U. Ograničavamo na realne maks. vrednosti.
        currentA = (int) Math.round(maxPowerKw * 1000 / voltageV);
        // Realna gornja granica za struju (kablovi, sigurnost)
        if (currentA > 500) currentA = 500; // Maksimalna struja za ultrabrze punjače je oko 500A

        // Kreiranje DC punjača
        return new ElectricCharger(
                chargerId,
                null,
                null,
                currentA,
                ChargingStatus.IDLE,
                voltageV,
                maxPowerKw,
                "DC",
                connectorTypes
        );
    }

    @PostConstruct
    public void init() {
        // Generisaćemo tačno 10 punjača
        int numberOfChargers = 10;

        for (int i = 0; i < numberOfChargers; i++) {
            String chargerId = "CH-" + (i + 1);
            ElectricCharger charger;

            // Nasumično odlučujemo da li će punjač biti AC ili DC
            if (random.nextBoolean()) { // True za AC, False za DC (ili obrnuto, po želji)
                charger = generateACCharger(chargerId);
            } else {
                charger = generateDCCharger(chargerId);
            }
            chargers.add(charger);
            System.out.println("Inicijalizovan: " + charger);
            chargingDataSender.sendChargingData(charger);
        }

        System.out.println("\nUkupno inicijalizovano " + chargers.size() + " punjača.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Aplikacija se gasi. Postavljam sve punjače na ERROR status...");
            for (ElectricCharger charger : chargers) {
                charger.setStatus(ChargingStatus.ERROR);
                chargingDataSender.sendChargingData(charger);
            }
        }));
    }


    @Scheduled(fixedRate = 300000) // 300.000 ms = 5 minuta
    public void simulateCharging() {
        if (chargers.isEmpty()) return;

        int index = random.nextInt(chargers.size());
        ElectricCharger charger = chargers.get(index);

        Instant now = Instant.now();

        switch (charger.getStatus()) {
            case IDLE -> {
                charger.setStatus(ChargingStatus.CHARGING);
                charger.setStartChargingTime(now);

                System.out.println("Punjač " + charger.getDeviceId() + " je prešao iz IDLE u CHARGING: " + charger);
                chargingDataSender.sendChargingData(charger);
            }
            case CHARGING -> {
                charger.setStatus(ChargingStatus.COMPLETED);
                charger.setEndChargingTime(now);
                System.out.println("Punjač " + charger.getDeviceId() + " je prešao iz CHARGING u COMPLETED: " + charger);
                chargingDataSender.sendChargingData(charger);
            }
            case COMPLETED -> {
                // prelazak u IDLE
                charger.setStatus(ChargingStatus.IDLE);
                charger.setStartChargingTime(null);
                charger.setEndChargingTime(null);

                System.out.println("Punjač " + charger.getDeviceId() + " je prešao iz COMPLETED u IDLE: " + charger);
                chargingDataSender.sendChargingData(charger);
            }
        }
    }


}



