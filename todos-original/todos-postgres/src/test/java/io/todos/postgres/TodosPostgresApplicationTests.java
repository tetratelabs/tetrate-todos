package io.todos.postgres;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
class TodosPostgresApplicationTests {

    @Test
    void contextLoads() {
    }

}
