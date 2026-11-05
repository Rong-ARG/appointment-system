package com.ronogar.appointment_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class AppointmentSystemApplication {

	private static final String DEFAULT_TIMEZONE = "America/Argentina/Buenos_Aires";

	public static void main(String[] args) {

		String zone = System.getenv().getOrDefault("APP_TIMEZONE", DEFAULT_TIMEZONE);
		TimeZone.setDefault(TimeZone.getTimeZone(zone));

		SpringApplication.run(AppointmentSystemApplication.class, args);
	}

}