package com.bookcloud.smartlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.bookcloud.smartlibrary.config.AppProperties;
import com.bookcloud.smartlibrary.config.CloudinaryProperties;

@SpringBootApplication
@EnableConfigurationProperties({ AppProperties.class, CloudinaryProperties.class })
public class SmartlibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartlibraryApplication.class, args);
	}

}
