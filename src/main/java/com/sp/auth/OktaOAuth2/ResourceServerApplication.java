package com.sp.auth.OktaOAuth2;

import com.nimbusds.jwt.JWT;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ResourceServerApplication {
	private OAuth2AuthorizedClientService authorizedClientService;

	public void MainController(OAuth2AuthorizedClientService authorizedClientService) {
		this.authorizedClientService = authorizedClientService;
	}


	public static void main(String[] args) {
		SpringApplication.run(ResourceServerApplication.class, args);
	}

	@Configuration
	static class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.oauth2ResourceServer().jwt();
		}
	}

	@RestController
	@CrossOrigin(origins = "http://localhost:8080")
	public class MessageOfTheDayController {

		@GetMapping("/api/userProfile")
		@PreAuthorize("hasAuthority('SCOPE_profile')")
		public Map<String, Object> getUserDetails(JwtAuthenticationToken authentication) {
			return authentication.getTokenAttributes();
		}

		@GetMapping("/api/messages")
		@PreAuthorize("hasAuthority('SCOPE_email')")
		public Map<String, Object> messages(JwtAuthenticationToken authentication) {

			Map userAttributes = Collections.emptyMap();
			userAttributes = authentication.getTokenAttributes();
			System.out.println("Glaims : " + authentication.getToken().getClaims());
			Jwt token = authentication.getToken();
			System.out.println("JWT Token " + token.getHeaders());

			for (Object item : userAttributes.entrySet())
			{
				System.out.println(item.toString());
			}
			Map<String, Object> result = new HashMap<>();
			result.put("messages", Arrays.asList(
					new Message("I am a robot."),
					new Message("Hello, world!")
			));

			return result;
		}
	}

	class Message {
		public Date date = new Date();
		public String text;

		Message(String text) {
			this.text = text;
		}
	}
	private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
		return this.authorizedClientService.loadAuthorizedClient(
				authentication.getAuthorizedClientRegistrationId(), authentication.getName());
	}

	private ExchangeFilterFunction oauth2Credentials(OAuth2AuthorizedClient authorizedClient) {
		return ExchangeFilterFunction.ofRequestProcessor(
				clientRequest -> {
					ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
							.header(HttpHeaders.AUTHORIZATION,
									"Bearer " + authorizedClient.getAccessToken().getTokenValue())
							.build();
					return Mono.just(authorizedRequest);
				});
	}}
