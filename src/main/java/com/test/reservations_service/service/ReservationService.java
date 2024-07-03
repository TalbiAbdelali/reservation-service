package com.test.reservations_service.service;

import com.test.reservations_service.dto.TableAvailabilityDTO;
import com.test.reservations_service.model.Reservation;
import com.test.reservations_service.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Value("${restaurant.service.url}")
    private String restaurantServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Reservation createUpdateReservation(Reservation reservation, String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearerToken);

        String url = restaurantServiceUrl + "/restaurant/" + reservation.getRestaurantId() + "/availability?date=" + reservation.getReservationTime();
        //ResponseEntity<TableAvailabilityDTO> response = restTemplate.getForEntity(url, TableAvailabilityDTO.class);
        ResponseEntity<TableAvailabilityDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET, new HttpEntity<>(headers), TableAvailabilityDTO.class);
        TableAvailabilityDTO availability = response.getBody();

        if (availability != null && availability.getAvailableTables() > 0) {
            // Update availability in the Restaurant Service if needed
            availability.setAvailableTables(availability.getAvailableTables() - 1);

            //restTemplate.put(url, new HttpEntity<>(availability, headers), TableAvailabilityDTO.class);

            String url2 = restaurantServiceUrl + "/restaurant/"+ availability.getRestaurant().getId() +"/availability";

            restTemplate.exchange(
                    url2,
                    HttpMethod.POST, new HttpEntity<>(availability, headers), TableAvailabilityDTO.class);

            return reservationRepository.save(reservation);
        } else {
            throw new ResourceAccessException("No available tables for this date");
        }

    }

    public List<Reservation> getAllReservations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reservationRepository.findAll(pageable).getContent();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ResourceAccessException("Reservation not found"));
    }

    public Reservation cancelReservation(Reservation reservation) {
        reservation.setStatus("CANCELED");
        return reservationRepository.save(reservation);
    }
}
