package com.example.sellercabinet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.sellercabinet.utils.ConsoleHttpClient;

import java.io.Console;
import java.util.Scanner;
import com.example.sellercabinet.dto.SellerResponse;
import tools.jackson.databind.ObjectMapper;

@SpringBootApplication
public class SellercabinetApplication {

	public static void main(String[] args) {
		ObjectMapper objectMapper = new ObjectMapper();
		SpringApplication.run(SellercabinetApplication.class, args);
	}

}
