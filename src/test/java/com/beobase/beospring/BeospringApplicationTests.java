package com.beobase.beospring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BeospringApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainStartsApplication() {
		BeospringApplication.main(new String[]{"--spring.profiles.active=test"});
	}

}
