package com.tpbackend.ms_catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tpbackend.ms_catalogo", "com.tpbackend"})
public class MsCatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCatalogoApplication.class, args);
	}

}
