package com.smartparking.config;

import com.smartparking.model.ParkingSlot;
import com.smartparking.model.Reservation;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.SlotType;
import com.smartparking.model.User;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.repository.ReservationRepository;
import com.smartparking.repository.UserRepository;
import com.smartparking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private ParkingSlotRepository slotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        log.info("⚡ Seeding Smart Parking Garage database...");

        // 1. Seed Users
        User admin = new User("admin", userService.getPasswordEncoder().encode("admin123"),
                "Garage Operator", "admin@smartparking.com", "+1-800-PARK-01", "ROLE_ADMIN");
        User user1 = new User("john.doe", userService.getPasswordEncoder().encode("password123"),
                "John Doe", "john.doe@example.com", "+1-555-0199", "ROLE_USER");
        User user2 = new User("sarah.smith", userService.getPasswordEncoder().encode("password123"),
                "Sarah Smith", "sarah.smith@example.com", "+1-555-0188", "ROLE_USER");

        userRepository.saveAll(List.of(admin, user1, user2));

        // 2. Seed 30 Parking Slots across 3 Floors
        List<ParkingSlot> slots = new ArrayList<>();

        // Floor 1 (Level A: Premium & EV & Standard)
        slots.add(new ParkingSlot("A-01", 1, SlotType.STANDARD, 20.0, false, true));
        slots.add(new ParkingSlot("A-02", 1, SlotType.STANDARD, 20.0, false, false));
        slots.add(new ParkingSlot("A-03", 1, SlotType.STANDARD, 20.0, false, false));
        slots.add(new ParkingSlot("A-04", 1, SlotType.EV, 35.0, true, false));
        slots.add(new ParkingSlot("A-05", 1, SlotType.EV, 35.0, true, false));
        slots.add(new ParkingSlot("A-06", 1, SlotType.COMPACT, 15.0, false, false));
        slots.add(new ParkingSlot("A-07", 1, SlotType.COMPACT, 15.0, false, false));
        slots.add(new ParkingSlot("A-08", 1, SlotType.SUV, 30.0, false, false));
        slots.add(new ParkingSlot("A-09", 1, SlotType.SUV, 30.0, false, false));
        slots.add(new ParkingSlot("A-10", 1, SlotType.MOTORCYCLE, 10.0, false, false));

        // Floor 2 (Level B: EV Hub & SUV)
        slots.add(new ParkingSlot("B-01", 2, SlotType.EV, 35.0, true, true));
        slots.add(new ParkingSlot("B-02", 2, SlotType.EV, 35.0, true, false));
        slots.add(new ParkingSlot("B-03", 2, SlotType.EV, 35.0, true, false));
        slots.add(new ParkingSlot("B-04", 2, SlotType.STANDARD, 20.0, false, false));
        slots.add(new ParkingSlot("B-05", 2, SlotType.STANDARD, 20.0, false, false));
        slots.add(new ParkingSlot("B-06", 2, SlotType.SUV, 30.0, false, false));
        slots.add(new ParkingSlot("B-07", 2, SlotType.SUV, 30.0, false, false));
        slots.add(new ParkingSlot("B-08", 2, SlotType.COMPACT, 15.0, false, false));
        slots.add(new ParkingSlot("B-09", 2, SlotType.COMPACT, 15.0, false, false));
        slots.add(new ParkingSlot("B-10", 2, SlotType.MOTORCYCLE, 10.0, false, false));

        // Floor 3 (Level C: Rooftop Standard & Economy)
        slots.add(new ParkingSlot("C-01", 3, SlotType.STANDARD, 20.0, false, true));
        slots.add(new ParkingSlot("C-02", 3, SlotType.STANDARD, 20.0, false, false));
        slots.add(new ParkingSlot("C-03", 3, SlotType.STANDARD, 20.0, false, false));
        slots.add(new ParkingSlot("C-04", 3, SlotType.COMPACT, 15.0, false, false));
        slots.add(new ParkingSlot("C-05", 3, SlotType.COMPACT, 15.0, false, false));
        slots.add(new ParkingSlot("C-06", 3, SlotType.SUV, 30.0, false, false));
        slots.add(new ParkingSlot("C-07", 3, SlotType.SUV, 30.0, false, false));
        slots.add(new ParkingSlot("C-08", 3, SlotType.MOTORCYCLE, 10.0, false, false));
        slots.add(new ParkingSlot("C-09", 3, SlotType.MOTORCYCLE, 10.0, false, false));
        slots.add(new ParkingSlot("C-10", 3, SlotType.MOTORCYCLE, 10.0, false, false));

        slotRepository.saveAll(slots);

        // 3. Pre-seed 2 sample active bookings to showcase live states
        ParkingSlot slotA03 = slotRepository.findBySlotNumber("A-03").orElse(null);
        if (slotA03 != null) {
            Reservation res1 = new Reservation();
            res1.setReservationCode("PK-DEMO001");
            res1.setSlot(slotA03);
            res1.setUser(user1);
            res1.setVehicleNumber("KA-05-MN-5566");
            res1.setVehicleType(SlotType.STANDARD);
            res1.setDriverName("John Doe");
            res1.setDriverPhone("+1-555-0199");
            res1.setStartTime(LocalDateTime.now().minusHours(1).minusMinutes(25));
            res1.setExpectedDurationHours(3);
            res1.setExpectedEndTime(LocalDateTime.now().plusHours(1).plusMinutes(35));
            res1.setHourlyRateSnapshot(slotA03.getHourlyRate());
            res1.setTotalAmount(slotA03.getHourlyRate() * 3);
            res1.setStatus("ACTIVE");
            res1.setPaymentStatus("PENDING");
            Reservation saved1 = reservationRepository.save(res1);

            slotA03.setStatus(SlotStatus.OCCUPIED);
            slotA03.setCurrentVehicleNumber(res1.getVehicleNumber());
            slotA03.setActiveReservationId(saved1.getId());
            slotRepository.save(slotA03);
        }

        ParkingSlot slotB02 = slotRepository.findBySlotNumber("B-02").orElse(null);
        if (slotB02 != null) {
            Reservation res2 = new Reservation();
            res2.setReservationCode("PK-DEMO002");
            res2.setSlot(slotB02);
            res2.setUser(user2);
            res2.setVehicleNumber("DL-01-EV-2024");
            res2.setVehicleType(SlotType.EV);
            res2.setDriverName("Sarah Smith");
            res2.setDriverPhone("+1-555-0188");
            res2.setStartTime(LocalDateTime.now().minusMinutes(40));
            res2.setExpectedDurationHours(2);
            res2.setExpectedEndTime(LocalDateTime.now().plusHours(1).plusMinutes(20));
            res2.setHourlyRateSnapshot(slotB02.getHourlyRate());
            res2.setTotalAmount(slotB02.getHourlyRate() * 2);
            res2.setStatus("ACTIVE");
            res2.setPaymentStatus("PENDING");
            Reservation saved2 = reservationRepository.save(res2);

            slotB02.setStatus(SlotStatus.RESERVED);
            slotB02.setCurrentVehicleNumber(res2.getVehicleNumber());
            slotB02.setActiveReservationId(saved2.getId());
            slotRepository.save(slotB02);
        }

        log.info("✅ Database seeded with 30 slots across 3 floors and 2 active demo bookings!");
    }
}
