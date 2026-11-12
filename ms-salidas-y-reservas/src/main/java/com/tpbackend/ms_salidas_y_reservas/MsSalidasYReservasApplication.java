package com.tpbackend.ms_salidas_y_reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tpbackend.ms_catalogo", "com.tpbackend"})
public class MsSalidasYReservasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSalidasYReservasApplication.class, args);
	}

}
