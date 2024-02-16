package org.zighinetto.poc;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.time.Duration;
import java.util.Objects;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class PocApplicationTest {

    private final JmsTemplate jmsTemplate;

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "hello,HELLO",
            "wOrlD,WORLD"
    })
    void testStrings(String message, String expected) {
        jmsTemplate.send("entry", mc -> mc.createTextMessage(message));

        var actual = await().atMost(Duration.ofSeconds(10)).until(() -> jmsTemplate.receive("exit"), Objects::nonNull).getBody(String.class);
        assertEquals(expected, actual);
    }
}