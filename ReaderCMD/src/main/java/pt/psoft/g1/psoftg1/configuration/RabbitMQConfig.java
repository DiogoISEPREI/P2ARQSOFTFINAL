package pt.psoft.g1.psoftg1.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String READER_EXCHANGE = "reader.exchange";
    public static final String READER_CREATED_QUEUE = "reader.created.queue";
    public static final String READER_CREATED_ROUTING_KEY = "reader.created";

    @Bean
    public TopicExchange readerExchange() {
        return new TopicExchange(READER_EXCHANGE);
    }

    @Bean
    public Queue readerCreatedQueue() {
        return new Queue(READER_CREATED_QUEUE, true);
    }

    @Bean
    public Binding readerCreatedBinding() {
        return BindingBuilder
                .bind(readerCreatedQueue())
                .to(readerExchange())
                .with(READER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
