package example;

import example.config.FBInitialize;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class IntellipayApplicationTests {

    @MockBean
    private FBInitialize fbInitialize;

    @Test
    void contextLoads() {
    }

}
