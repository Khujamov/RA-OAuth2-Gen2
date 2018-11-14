/*
 * Copyright 2017 the original author or authors.
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


package io.agilehandy.ui.client;

import io.agilehandy.ui.model.Flight;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * @author Haytham Mohamed
 **/
@Component
public class FlightsClient {

	private final String GATEWAY_URL = "http://localhost:8080/api/flights";

	private final WebClient webClient;

	public FlightsClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<String> pingFlightsService() {
		String uri = GATEWAY_URL + "/ping";
		return webClient
				.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(String.class)
				;
	}

	public Flux<Flight> findDatedFlights(String origin, String destination,
	                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate mindate,
	                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate maxdate) {
		String uri = GATEWAY_URL +
				"/search/datedlegs?origin={p1}&destination={p2}&minDate={p3}&maxDate={p4}";
		return webClient
				.get()
				.uri(uri, origin, destination, mindate, maxdate)
				.retrieve()
				.bodyToFlux(Flight.class)
				;
	}

	public Flux<Flight> findFlights(String origin, String destination) {
		String uri = GATEWAY_URL + "/search/legs?origin={p1}&destination={p2}";
		return webClient
				.get()
				.uri(uri, origin, destination)
				.retrieve()
				.bodyToFlux(Flight.class)
				;
	}

	public Flux<Flight> findAllFlights() {
		String uri = GATEWAY_URL;
		return webClient
				.get()
				.uri(uri)
				.retrieve()
				.bodyToFlux(Flight.class)
				;
	}

	public Mono<Flight> findById(String id) {
		String uri = GATEWAY_URL + "/{id}";
		return webClient
				.get()
				.uri(uri, id)
				.retrieve()
				.bodyToMono(Flight.class)
				;
	}

	public Mono<Void> update(Flight flight) {
		String uri = GATEWAY_URL;
		return webClient
				.post()
				.uri(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(flight)
				.retrieve()
				.bodyToMono(Void.class)
				;
	}

	public Flux<String> allOrigins() {
		String uri = GATEWAY_URL + "/origins";
		return webClient
				.get()
				.uri(uri)
				.retrieve()
				.bodyToFlux(String.class)
				;
	}

	public Flux<String> allDestinations() {
		String uri = GATEWAY_URL + "/destinations";
		return webClient
				.get()
				.uri(uri)
				.retrieve()
				.bodyToFlux(String.class)
				;
	}
}
