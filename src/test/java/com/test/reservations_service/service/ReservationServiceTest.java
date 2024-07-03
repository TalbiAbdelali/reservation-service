package com.test.reservations_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.test.reservations_service.config.AppConfig;
import com.test.reservations_service.dto.TableAvailabilityDTO;
import com.test.reservations_service.model.Reservation;
import com.test.reservations_service.model.Restaurant;
import com.test.reservations_service.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {AppConfig.class})
public class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReservationService reservationService;

    @Value("${restaurant.service.url}")
    private String restaurantServiceUrl;

    private Reservation reservation;
    private TableAvailabilityDTO availability;
    private Restaurant restaurant;
    private String bearerToken;

    @BeforeEach
    public void setUp() {
        reservation = new Reservation();
        reservation.setRestaurantId(1L);
        reservation.setReservationTime("2024-07-02T19:00:00");

        restaurant = new Restaurant();
        restaurant.setId(1L);

        availability = new TableAvailabilityDTO();
        availability.setAvailableTables(5);
        availability.setRestaurant(restaurant);

        bearerToken = "Bearer token";
    }

    @Test
    public void testCreateUpdateReservation_Success() {
        // Mock the RestTemplate exchange method for GET request
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(TableAvailabilityDTO.class)))
                .thenReturn(new ResponseEntity<>(availability, HttpStatus.OK));

        // Mock the RestTemplate exchange method for POST request
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(TableAvailabilityDTO.class)))
                .thenReturn(new ResponseEntity<>(availability, HttpStatus.OK));

        // Mock the save method of the repository
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Call the service method
        Reservation result = reservationService.createUpdateReservation(reservation, bearerToken);

        // Verify the result
        assertNotNull(result);
        verify(reservationRepository, times(1)).save(reservation);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(TableAvailabilityDTO.class));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(TableAvailabilityDTO.class));
    }

    @Test
    public void testCreateUpdateReservation_NoAvailableTables() {
        // Set available tables to 0 to simulate no availability
        availability.setAvailableTables(0);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(TableAvailabilityDTO.class)))
                .thenReturn(new ResponseEntity<>(availability, HttpStatus.OK));

        ResourceAccessException exception = assertThrows(ResourceAccessException.class, () -> {
            reservationService.createUpdateReservation(reservation, bearerToken);
        });

        assertEquals("No available tables for this date", exception.getMessage());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    public void testGetAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        Page<Reservation> page = new PageImpl<>(reservations);
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<Reservation> result = reservationService.getAllReservations(0, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testGetReservationById_Success() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.getReservationById(1L);

        assertNotNull(result);
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetReservationById_NotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceAccessException exception = assertThrows(ResourceAccessException.class, () -> {
            reservationService.getReservationById(1L);
        });

        assertEquals("Reservation not found", exception.getMessage());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    public void testCancelReservation() {
        reservation.setStatus("ACTIVE");

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result = reservationService.cancelReservation(reservation);

        assertNotNull(result);
        assertEquals("CANCELED", result.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }
}