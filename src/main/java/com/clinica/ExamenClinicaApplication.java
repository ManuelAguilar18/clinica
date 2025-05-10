package com.clinica;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@SpringBootApplication
public class ExamenClinicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamenClinicaApplication.class, args);
	}
	@Bean
	public CommandLineRunner testControllerLoad(ApplicationContext ctx) {
		return args -> {
			System.out.println("==== Controladores registrados ====");
			String[] beans = ctx.getBeanNamesForAnnotation(Controller.class);
			for (String name : beans) {
				System.out.println(name);
			}
			System.out.println("===================================");
		};
	}
}
