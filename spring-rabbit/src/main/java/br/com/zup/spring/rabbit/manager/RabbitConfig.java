package br.com.zup.spring.rabbit.manager;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(ignoreResourceNotFound = false, value = {"classpath:rabbit.properties"})
@ImportResource(locations = {"classpath:rabbit-context.xml"})
public class RabbitConfig {

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.virtualhost}")
    private String virtualHost;

    @Value("${rabbitmq.user}")
    private String user;

    @Value("${rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setVirtualHost(virtualHost);
        factory.setUsername(user);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public Connection rabbitConnection(ConnectionFactory factory) throws IOException, TimeoutException {
        return factory.newConnection();
    }

    @Bean
    public RabbitManager rabbitManager(Connection rabbitConnection) {
        return new RabbitManager(rabbitConnection);
    }
}
