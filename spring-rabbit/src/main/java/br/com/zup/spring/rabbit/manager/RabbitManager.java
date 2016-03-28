package br.com.zup.spring.rabbit.manager;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

import br.com.zup.spring.rabbit.infra.AmqpException;
import br.com.zup.spring.rabbit.infra.QDelivery;
import br.com.zup.spring.rabbit.infra.Queue;

@Component
public final class RabbitManager {

	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitManager.class);

	private Connection connection;

	@Autowired
	public RabbitManager(Connection connection) {
		this.connection = connection;
	}

	public Channel createChannel() {
		try {
			return connection.createChannel();
		} catch (IOException e) {
			throw new AmqpException("Failed to create channel.", e);
		}
	}

	public Channel createChannelForConsumer(int prefetchCount) {
		try {
			Channel channel = connection.createChannel();
			channel.basicQos(prefetchCount);
			return channel;
		} catch (IOException e) {
			throw new AmqpException("Failed to create channel for consumer.",
					e);
		}
	}

	public void closeChannel(Channel channel) {
		try {
			channel.close();
		} catch (Exception e) {
			throw new AmqpException("Failed to close channel.", e);
		}
	}

	public void bindConsumer(Channel channel, Consumer consumer, Queue queue) {
		try {
			channel.queueDeclare(queue.name, queue.durable, queue.exclusive,
					queue.autoDelete, null);
			channel.basicConsume(queue.name, false, consumer);
		} catch (IOException e) {
			throw new AmqpException("Failed to bind consumer.", e);
		}
	}

	public void publish(QDelivery delivery) {
		try {
			Channel channel = createChannel();
			Queue queue = delivery.queue;
			channel.queueDeclare(queue.name, queue.durable, queue.exclusive,
					queue.autoDelete, null);
			channel.basicPublish("", queue.name, null,
					delivery.message.getBody());
			closeChannel(channel);
		} catch (IOException e) {
			LOG.error("Failed to send message [{}] to [{}].",
					delivery.message.toString(), delivery.queue.name);
			throw new AmqpException("Failed to send message.", e);
		}
	}
}
