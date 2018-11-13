/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.agilehandy.reservation;

import io.agilehandy.reservation.entities.Reservation;
import io.agilehandy.reservation.entities.ReservationRequest;
import io.agilehandy.reservation.exceptions.ExceptionMessage;
import io.agilehandy.reservation.exceptions.ReservationException;
import io.agilehandy.reservation.flight.Flight;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author Haytham Mohamed
 */

@RestController
@Slf4j
public class ReservationController {

	private final ReservationService reservationService;

	public ReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping
	public Flux<Reservation> allReservations() {
		log.info("retrieve all reservations");
		return reservationService.allReservations();

	}

    @GetMapping("/search")
    public Flux<Flight> searchAllFlights(
    		@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient oauth2Client) {
        log.info("searching all flights");
        return reservationService.searchAllFlights(oauth2Client);

    }

	@GetMapping("/search/{id}")
	public Mono<Flight> searchAflight(@PathVariable String id,
	                                  @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient oauth2Client) {
		log.info("searching a flight by id: " + id);
		return reservationService.findById(id, oauth2Client);

	}

	@GetMapping("/search/{from}/{to}")
	public Flux<Flight> searchFlights(@PathVariable String from,
									  @PathVariable String to,
									  @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient oauth2Client) {
	    log.info("searching flights from " + from + " to " + to);
		return reservationService.searchFlights(from, to, oauth2Client);

	}

	@GetMapping("/search/{from}/{to}/{date}")
	public Flux<Flight> searchDatedFlights(@PathVariable String from,
			@PathVariable String to,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient oauth2Client)
			throws ParseException {
		LocalDate dt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate mindt = dt.minusDays(3);
		LocalDate maxdt = dt.plusDays(3);
		//java.util.Date mindtdt = java.sql.Date.valueOf(mindt);
		//java.util.Date maxdtdt = java.sql.Date.valueOf(maxdt);
		return reservationService.searchDatedFlights(from, to, oauth2Client, mindt, maxdt);
	}

	@PostMapping("/book")
	public Mono<ReservationRequest> book(@RequestBody ReservationRequest reservationRequest,
	                                     @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient oauth2Client) {
		log.info("Booking a flight for " + reservationRequest.getPassengers());
		Mono<String> confirmation = reservationService.book(reservationRequest, oauth2Client);
		return confirmation
				.log()
				.doOnNext(c -> reservationRequest.setConfirmation(c))
				.map(s -> reservationRequest)
				;
		// Mono.just(reservationRequest);
	}

	@GetMapping("/search/origins")
	public Flux<String> origins(@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient oauth2Client) {
		return this.reservationService.getFlightOrigins(oauth2Client);
	}

	@GetMapping("/search/destinations")
	public Flux<String> destinations(@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient oauth2Client) {
		return this.reservationService.getFlightDestinations(oauth2Client);
	}

	@ExceptionHandler(ReservationException.class)
	public ExceptionMessage handleException(ReservationException exception) {
		return new ExceptionMessage(exception.getExceptionMsg(),
				exception.getCause() == null ? "" : exception.getCause().getMessage(),
				Arrays.stream(exception.getStackTrace())
						.map(sequence -> String.valueOf(sequence))
						.collect(Collectors.joining("\n")));
	}

}
