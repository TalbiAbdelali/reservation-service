package com.test.reservations_service.controller;

import com.test.reservations_service.model.Reservation;
import com.test.reservations_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Reservation> createUpdateReservation(@RequestBody Reservation reservation, @RequestHeader (name="Authorization") String token) {
        Reservation createdReservation = reservationService.createUpdateReservation(reservation, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Reservation> reservations = reservationService.getAllReservations(page, size);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping
    public ResponseEntity<Reservation> cancelReservation(@RequestBody Reservation reservation) {
        Reservation cancelReservation = reservationService.cancelReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(cancelReservation);
    }
}
