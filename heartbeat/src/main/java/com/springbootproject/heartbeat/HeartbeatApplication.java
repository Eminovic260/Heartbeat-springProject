package com.springbootproject.heartbeat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Heartbeat Game API",
        version = "1.0",
        description = "API documentation for managing game creation and gameplay actions."
    )
)
@SpringBootApplication
public class HeartbeatApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeartbeatApplication.class, args);
	}

}
