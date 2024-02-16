package org.zighinetto.poc;

import jakarta.jms.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;

@SpringBootApplication
public class PocApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocApplication.class, args);
    }

    @ConditionalOnBean(ConnectionFactory.class)
    @Bean
    public IntegrationFlow exampleIntegrationFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow.from(Jms.inboundAdapter(connectionFactory)
                        .destination("entry")
                )
                .transform(String.class, String::toUpperCase)
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination("exit")
                )
                .get();
    }

}
