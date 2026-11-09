package henrique.igor.iiot.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.events:iiot.events.exchange}")
    private String eventsExchangeName;

    @Value("${rabbitmq.exchange.dlx:iiot.events.dlx}")
    private String deadLetterExchangeName;

    @Value("${rabbitmq.queue.critical-alerts:iiot.alerts.critical.queue}")
    private String criticalAlertsQueueName;

    @Value("${rabbitmq.queue.work-orders:iiot.workorders.queue}")
    private String workOrdersQueueName;

    @Value("${rabbitmq.queue.dlq:iiot.events.dlq}")
    private String deadLetterQueueName;

    @Value("${rabbitmq.routing-key.critical-alert:alert.critical}")
    private String criticalAlertRoutingKey;

    @Value("${rabbitmq.routing-key.work-order-created:workorder.created}")
    private String workOrderCreatedRoutingKey;

    @Value("${rabbitmq.routing-key.dlq:events.dlq}")
    private String deadLetterRoutingKey;

    @Bean
    public TopicExchange eventsExchange() {
        return ExchangeBuilder.topicExchange(eventsExchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(deadLetterExchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public Queue criticalAlertsQueue() {
        return QueueBuilder.durable(criticalAlertsQueueName)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-dead-letter-routing-key", deadLetterRoutingKey)
                .build();
    }

    @Bean
    public Queue workOrdersQueue() {
        return QueueBuilder.durable(workOrdersQueueName)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-dead-letter-routing-key", deadLetterRoutingKey)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueueName).build();
    }

    @Bean
    public Binding criticalAlertsBinding(Queue criticalAlertsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(criticalAlertsQueue)
                .to(eventsExchange)
                .with(criticalAlertRoutingKey);
    }

    @Bean
    public Binding workOrdersBinding(Queue workOrdersQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(workOrdersQueue)
                .to(eventsExchange)
                .with(workOrderCreatedRoutingKey);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(deadLetterRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
