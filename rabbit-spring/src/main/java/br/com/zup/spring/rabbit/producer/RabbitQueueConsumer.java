package br.com.zup.spring.rabbit.producer;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import br.com.zup.spring.rabbit.consumer.QueueConsumer;
import br.com.zup.spring.rabbit.infra.MessageConverter;
import br.com.zup.spring.rabbit.infra.QMessage;
import br.com.zup.spring.rabbit.infra.Queue;
import br.com.zup.spring.rabbit.manager.RabbitManager;

public final class RabbitQueueConsumer<T extends QMessage> implements QueueConsumer<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitQueueConsumer.class);

    private final RabbitManager rabbitManager;

    private MessageConverter<T> messageConverter;

    private Queue queue;

    public RabbitQueueConsumer(RabbitManager rabbitManager, Queue queue) {
        this.rabbitManager = rabbitManager;
        this.queue = queue;
    }

    public void consume(java.util.function.Consumer<T> successCallback, boolean requeueWhenFailure) {

        Channel channel = rabbitManager.createChannelForConsumer(1);

        final Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {

                T message = messageConverter.convert(body);

                LOG.debug("Receiving message [{}] from [{}] ...", message.toString(), queue.name);

                try {
                    successCallback.accept(message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    LOG.error("Queue Consumer Callback error", e);
                    channel.basicNack(envelope.getDeliveryTag(), false, requeueWhenFailure);
                }
            }
        };

        rabbitManager.bindConsumer(channel, consumer, queue);
    }

    public void setMessageConverter(MessageConverter<T> messageConverter) {
        this.messageConverter = messageConverter;
    }
}
