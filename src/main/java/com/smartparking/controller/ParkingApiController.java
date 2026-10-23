package com.smartparking.controller;

import com.smartparking.dto.*;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.Reservation;
import com.smartparking.model.User;
import com.smartparking.service.ParkingSlotService;
import com.smartparking.service.PaymentService;
import com.smartparking.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ParkingApiController {

    @Autowired
    private ParkingSlotService slotService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentService paymentService;

    // 2. View Parking Slots
    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<List<ParkingSlot>>> getSlots(
            @RequestParam(required = false) Integer floor) {
        List<ParkingSlot> slots = (floor != null && floor > 0)
                ? slotService.getSlotsByFloor(floor)
                : slotService.getAllSlots();
        return ResponseEntity.ok(ApiResponse.ok("Parking slots retrieved", slots));
    }

    @GetMapping("/slots/{slotNumber}")
    public ResponseEntity<ApiResponse<ParkingSlot>> getSlotDetail(@PathVariable String slotNumber) {
        return slotService.getSlotByNumber(slotNumber.toUpperCase())
                .map(slot -> ResponseEntity.ok(ApiResponse.ok("Slot found", slot)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Parking Garage Overview & Statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ParkingStatsDto>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok("Garage statistics", slotService.getOverallStats()));
    }

    // 3. Book a Slot & 4. Update Database
    @PostMapping("/bookings/book")
    public ResponseEntity<ApiResponse<BookingResponse>> bookSlot(
            @Valid @RequestBody BookingRequest req, HttpSession session) {
        // Automatically inject logged in user's username if available
        if ((req.getUsername() == null || req.getUsername().isBlank()) && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            req.setUsername(user.getUsername());
            if (req.getDriverName() == null || req.getDriverName().isBlank()) {
                req.setDriverName(user.getFullName());
            }
        }

        try {
            BookingResponse response = reservationService.bookSlot(req);
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Booking error: " + ex.getMessage()));
        }
    }

    // Active & Past Bookings
    @GetMapping("/bookings/active")
    public ResponseEntity<ApiResponse<List<Reservation>>> getActiveBookings() {
        return ResponseEntity.ok(ApiResponse.ok("Active bookings", reservationService.getActiveReservations()));
    }

    @GetMapping("/bookings/history")
    public ResponseEntity<ApiResponse<List<Reservation>>> getBookingHistory() {
        return ResponseEntity.ok(ApiResponse.ok("Booking history", reservationService.getAllReservations()));
    }

    // 6. Vehicle Exit: Calculate Bill
    @GetMapping("/checkout/calculate")
    public ResponseEntity<ApiResponse<BillSummaryResponse>> calculateBill(
            @RequestParam String query) {
        BillSummaryResponse bill = paymentService.calculateExitBill(query);
        if (!bill.isFound()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(bill.getMessage()));
        }
        return ResponseEntity.ok(ApiResponse.ok(bill.getMessage(), bill));
    }

    // 6. Vehicle Exit: Process Payment & Exit Gate Barrier
    @PostMapping("/checkout/pay")
    public ResponseEntity<ApiResponse<PaymentReceiptResponse>> payAndExit(
            @Valid @RequestBody PaymentRequest req) {
        try {
            PaymentReceiptResponse receipt = paymentService.processPayment(req);
            return ResponseEntity.ok(ApiResponse.ok(receipt.getMessage(), receipt));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Payment error: " + ex.getMessage()));
        }
    }
}
