package com.smartparking.controller;

import com.smartparking.dto.ApiResponse;
import com.smartparking.model.ParkingSlot;
import com.smartparking.service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    @Autowired
    private ParkingSlotService slotService;

    // Simulate vehicle arriving or leaving on a specific slot
    @PostMapping("/sensor/toggle")
    public ResponseEntity<ApiResponse<ParkingSlot>> toggleSensor(@RequestParam String slotNumber) {
        try {
            ParkingSlot updated = slotService.toggleSimulationSensor(slotNumber);
            return ResponseEntity.ok(ApiResponse.ok("Sensor triggered for slot " + slotNumber, updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Simulate random traffic movement
    @PostMapping("/random-traffic")
    public ResponseEntity<ApiResponse<String>> triggerRandomTraffic() {
        List<ParkingSlot> all = slotService.getAllSlots();
        if (all.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No slots available"));
        }
        int randomIndex = (int) (Math.random() * all.size());
        ParkingSlot target = all.get(randomIndex);
        slotService.toggleSimulationSensor(target.getSlotNumber());
        return ResponseEntity.ok(ApiResponse.ok("Random traffic simulation event fired on " + target.getSlotNumber()));
    }
}
