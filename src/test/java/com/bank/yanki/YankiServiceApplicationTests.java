package com.bank.yanki;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class YankiServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main_shouldStartApplication() {

		try (MockedStatic<SpringApplication> mocked =
					 mockStatic(SpringApplication.class)) {


			YankiServiceApplication.main(new String[]{});


			mocked.verify(() ->
					SpringApplication.run(
							YankiServiceApplication.class,
							new String[]{}
					)
			);
		}
	}
}
