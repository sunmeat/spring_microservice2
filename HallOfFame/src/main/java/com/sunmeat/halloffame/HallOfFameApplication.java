package com.sunmeat.halloffame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // без цієї анотації планувальник не працюватиме
@SpringBootApplication
public class HallOfFameApplication {
	public static void main(String[] args) {
		SpringApplication.run(HallOfFameApplication.class, args);
	}
}

@Component
class BrowserLauncher {
	@EventListener(ApplicationReadyEvent.class)
	public void launchBrowser() {
		System.setProperty("java.awt.headless", "false"); 
		var desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI("http://localhost:8081/reviews"));
		} catch (Exception e) {
			// нічого не робимо у випадку помилки
		}
	}
}
