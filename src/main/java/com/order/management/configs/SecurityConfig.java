package com.order.management.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	// Password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Default admin user (InMemory)
	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		UserDetails user = User.withUsername("admin").password(encoder.encode("admin123")).roles("ADMIN").build();
		return new InMemoryUserDetailsManager(user);
	}

	// AuthenticationManager
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder encoder,
			UserDetailsService uds) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(uds).passwordEncoder(encoder)
				.and().build();
	}

	// Security filter chain (6.1+)
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // disable CSRF untuk Swagger POST
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
						.permitAll() // allow auth & swagger
						.anyRequest().authenticated());
		return http.build();
	}
}
