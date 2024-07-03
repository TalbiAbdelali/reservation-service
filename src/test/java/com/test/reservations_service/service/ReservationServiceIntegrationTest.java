package com.test.reservations_service.service;

import com.test.reservations_service.config.AppConfig;
import com.test.reservations_service.model.Reservation;
import com.test.reservations_service.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.mockserver.integration.ClientAndServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class ReservationServiceIntegrationTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Value("${restaurant.service.url}")
    private String restaurantServiceUrl;

    private ClientAndServer mockServer;

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        mockServer = ClientAndServer.startClientAndServer(1080);
        restTemplate = new RestTemplateBuilder().rootUri("http://localhost:" + port).build();
        reservationRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        mockServer.stop();
        reservationRepository.deleteAll();
    }




    @Test
    public void testGetAllReservations() {
        Reservation reservation = new Reservation();
        reservation.setRestaurantId(1L);
        reservation.setReservationTime("2024-07-02T19:00:00");
        reservationRepository.save(reservation);

        List<Reservation> result = reservationService.getAllReservations(0, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetReservationById_Success() {
        Reservation reservation = new Reservation();
        reservation.setRestaurantId(1L);
        reservation.setReservationTime("2024-07-02T19:00:00");
        reservation = reservationRepository.save(reservation);

        Reservation result = reservationService.getReservationById(reservation.getId());

        assertNotNull(result);
        assertEquals(reservation.getId(), result.getId());
    }

    @Test
    public void testGetReservationById_NotFound() {
        ResourceAccessException exception = assertThrows(ResourceAccessException.class, () -> {
            reservationService.getReservationById(1L);
        });

        assertEquals("Reservation not found", exception.getMessage());
    }

    @Test
    public void testCancelReservation() {
        Reservation reservation = new Reservation();
        reservation.setRestaurantId(1L);
        reservation.setReservationTime("2024-07-02T19:00:00");
        reservation.setStatus("ACTIVE");
        reservation = reservationRepository.save(reservation);

        Reservation result = reservationService.cancelReservation(reservation);

        assertNotNull(result);
        assertEquals("CANCELED", result.getStatus());
    }
}
