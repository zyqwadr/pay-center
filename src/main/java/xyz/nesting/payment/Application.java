package xyz.nesting.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		/*
		 * 指定环境启动
		 *  mvn spring-boot:run -Drun.arguments=--spring.profiles.active=prod
		 */
		SpringApplication.run(Application.class, args);
	}
}
