package co.dzone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
        "spring.profiles.active=local"
})
class OneApplicationTest {

    @Test
    void contextLoads() {
        // Spring Context 로딩 확인
    }
}
