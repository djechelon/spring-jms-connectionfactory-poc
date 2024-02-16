# Spring JMS ConnectionFactory POC

This repository contains a demo application to simplify an issue occurred with the `@ConditionalOnBean` annotation from Spring Boot.

In this repository, we define an example `IntegrationFlow` that:

- Reads from `entry` JMS channel
- Makes the word uppercase
- Outputs the result to `exit` JMS channel

The IntegrationFlow depends on the `jakarta.jms.ConnectionFactory` to have been defined as a bean. In my business case, the Spring Boot application **may start without** JMS at all, so the IntegrationFlow **must not** be defined.
For that purpose, we use the `@ConditionalOnBean` annotation.

Per [Spring documentation](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/condition/ConditionalOnBean.java#L53), that annotation should be used only on `@AutoConfiguration` classes, which makes things a little more complicated.
Half of the world uses the same annotation for a variety of purpose, and we are actively using it in our real application to instantiate a bean only if a collaborator is defined as a bean. None of these require that one uses `@ConditionalOnBean` on `@AutoConfiguration`s only

- [Example](https://www.baeldung.com/spring-conditional-annotations)
- [Example](https://reflectoring.io/spring-boot-conditionals/)

To provide an example, we use a number of _RestService_s that depend on `RestTemplate` (bearing the root url and authentication info), as well as _SoapService_s depending on `WebServiceTemplate`, and they depend as such:

- `WebServiceTemplate` bean depends on _business property_ `com.example.ws.{url|username|password}`
- _SoapService_ component is annotated `@ConditionalOnBean(value = WebServiceTemplate.class, name = "mySpecificWebServiceTemplateBecauseWeHavePlentiesOf")`

All beautiful, except that it doesn't work with JMS ConnectionFactory. In this case, the connection factory exists only if `spring.artemis.embedded=true` (or another MQ broker is defined according to profile configuration).

In this POC, I can see that Spring refuses to instantiate the IntegrationFlow despite the ConnectionFactory exists.

Other considerations:

- In the POC, I have simplified declaring the bean in the application class, but in my application we have dedicated `@Configuration` classes
- Declaring that Configuration as an `@AutoConfiguration(after = JmsAutoConfiguration.class)` did not work
- `@DependsOn("jmsConnectionFactory")` fails when there is no JMS ConnectionFactory
- I can clearly see in the logs that `MyJmsConfiguration` does not match the Condition
- Debugging into `ArtemisAutoConfiguration`, the ConnectionFactory eventually is created
- Debugging the test with the `BeanFactory`, the ConnectionFactory bean is eventually available
- `@SpringIntegrationTest` is currently not present in the POC but makes no difference (the real application shows it)
