package coms309.workout;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LiftApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("Testing");
	}

}
