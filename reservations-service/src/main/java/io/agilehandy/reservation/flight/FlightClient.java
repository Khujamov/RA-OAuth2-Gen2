package io.agilehandy.reservation.flight;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

/**
 * @author Haytham Mohamed
 */

@Component
public class FlightClient {

    private final WebClient webClient;

    public FlightClient(WebClient webClient) {
        this.webClient = webClient;
    }

	public Flux<Flight> findDatedFlights(String origin, String destination,
	                                     OAuth2AuthorizedClient oauth2Client,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate mindate,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate maxdate)
    {
        String uri = "http://FLIGHTS-SERVICE/search/datedlegs?origin={p1}&destination={p2}&minDate={p3}&maxDate={p4}";
		return webClient
                .get()
                .uri(uri, origin, destination, mindate, maxdate)
				.attributes(oauth2AuthorizedClient(oauth2Client))
                .retrieve()
                .bodyToFlux(Flight.class)
                ;
	}

	public Flux<Flight> findFlights(String origin, String destination,
	                                OAuth2AuthorizedClient oauth2Client) {
        String uri = "http://flights-service/search/legs?origin={p1}&destination={p2}";
        return webClient
                .get()
                .uri(uri, origin, destination)
		        .attributes(oauth2AuthorizedClient(oauth2Client))
                .retrieve()
                .bodyToFlux(Flight.class)
                ;
	}

    public Flux<Flight> findAllFlights(OAuth2AuthorizedClient oauth2Client) {
        String uri = "http://flights-service";
        return webClient
                .get()
                .uri(uri)
		        .attributes(oauth2AuthorizedClient(oauth2Client))
                .retrieve()
                .bodyToFlux(Flight.class)
                ;
    }

	public Mono<Flight> findById(String id, OAuth2AuthorizedClient oauth2Client) {
        String uri = "http://FLIGHTS-SERVICE/{id}";
        return webClient
                .get()
                .uri(uri, id)
		        .attributes(oauth2AuthorizedClient(oauth2Client))
                .retrieve()
                .bodyToMono(Flight.class)
                ;
	}

	public Mono<Void> update(Flight flight, OAuth2AuthorizedClient oauth2Client) {
        String uri = "http://FLIGHTS-SERVICE";
        return webClient
                .post()
                .uri(uri)
		        .attributes(oauth2AuthorizedClient(oauth2Client))
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(flight)
                .retrieve()
                .bodyToMono(Void.class)
                ;
	}

	public Flux<String> origins(OAuth2AuthorizedClient oauth2Client) {
        String uri = "http://FLIGHTS-SERVICE/origins";
        return webClient
                .get()
                .uri(uri)
		        .attributes(oauth2AuthorizedClient(oauth2Client))
                .retrieve()
                .bodyToFlux(String.class)
                ;
	}

	public Flux<String> destinations(OAuth2AuthorizedClient oauth2Client) {
        String uri = "http://FLIGHTS-SERVICE/destinations";
        return webClient
                .get()
                .uri(uri)
		        .attributes(oauth2AuthorizedClient(oauth2Client))
                .retrieve()
                .bodyToFlux(String.class)
                ;
	}

}
