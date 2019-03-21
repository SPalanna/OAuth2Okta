package com.sp.auth.OktaOAuth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ResourceServerApplication {

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
		public Map<String, Object> messages() {

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
}
