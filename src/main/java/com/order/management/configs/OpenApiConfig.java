package com.order.management.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		Server productionServer = new Server();
		productionServer.setUrl("https://order-management-service-production.up.railway.app");
		productionServer.setDescription("Production Server");

		Server localServer = new Server();
		localServer.setUrl("http://localhost:8080");
		localServer.setDescription("Local Development");

		return new OpenAPI()
				.info(new Info().title("Order Management Service API")
						.description("Authentication & Order Management API").version("1.0.0"))
				.servers(List.of(productionServer, localServer)) // Masukkan list server di sini
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("BearerAuth",
						new SecurityScheme().name("Authorization").type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")));
	}
}