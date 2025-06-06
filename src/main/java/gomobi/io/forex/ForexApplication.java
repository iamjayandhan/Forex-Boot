package gomobi.io.forex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ForexApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForexApplication.class, args);
	}
}
