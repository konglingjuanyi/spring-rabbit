package br.com.zup.spring.rabbit.consumer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import br.com.zup.spring.rabbit.infra.JsonMessage;
import br.com.zup.spring.rabbit.infra.MessageConverter;
import br.com.zup.spring.rabbit.infra.QMessage;
import br.com.zup.spring.rabbit.infra.Queue;
import br.com.zup.spring.rabbit.manager.RabbitManager;

public final class RabbitQueueConsumer<T extends QMessage>
		implements
			QueueConsumer<T> {

	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitQueueConsumer.class);
	private static final Integer UNLIMITED_MESSAGES_CONFIG = 0;

	private final RabbitManager rabbitManager;
	private MessageConverter<T> messageConverter;
	private Queue queue;

	public RabbitQueueConsumer(RabbitManager rabbitManager, Queue queue) {
		this.rabbitManager = rabbitManager;
		this.queue = queue;
	}

	public void sync(java.util.function.Consumer<T> successCallback,
			boolean requeueWhenFailure) {
		Channel channel = rabbitManager.createChannelForConsumer(1);
		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					AMQP.BasicProperties properties, byte[] body)
							throws IOException {
				T message = messageConverter.convert(body);

				LOG.debug("Receiving message [{}] from [{}] ...",
						message.toString(), queue.name);

				try {
					successCallback.accept(message);
					channel.basicAck(envelope.getDeliveryTag(), false);
				} catch (Exception e) {
					LOG.error("Queue Consumer Callback error", e);
					channel.basicNack(envelope.getDeliveryTag(), false,
							requeueWhenFailure);
				}
			}
		};
		rabbitManager.bindConsumer(channel, consumer, queue);
	}

	public void async(java.util.function.Consumer<T> successCallback, boolean requeueWhenFailure) {
        Channel channel = rabbitManager.createChannelForConsumer(UNLIMITED_MESSAGES_CONFIG);
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {
            	CompletableFuture<Object> executionResult = CompletableFuture.supplyAsync(() -> {
            	    return convertMessageBody(body);
            	}).thenCompose((f) -> CompletableFuture.supplyAsync(() ->  {
					return processMessage(f, successCallback);
                }));
            	
				executionResult.whenComplete((res, throwable) -> handleResult(res, throwable, channel, envelope));
            }
            
            private JsonMessage convertMessageBody(byte[] body) {
            	LOG.debug("Handling Async message");
            	return (JsonMessage) messageConverter.convert(body);
            }
            
            private void handleResult(Object res, Throwable throwable, Channel channel, Envelope envelope) {
            	try {
					if (throwable != null) {
						channel.basicNack(envelope.getDeliveryTag(), false,
								requeueWhenFailure);
					} else {
						channel.basicAck(envelope.getDeliveryTag(), false);
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				} catch (Exception e) {
					LOG.error("Queue Consumer channel Ack error", e);
				}
            }
            
            @SuppressWarnings("unchecked")
			private CompletableFuture<Void> processMessage(JsonMessage message, java.util.function.Consumer<T> successCallback) {
            	LOG.debug("Processing Async message [{}] from [{}] ...", message.toString(), queue.name);
            	try {
            		successCallback.accept((T) message);
            	} catch (Exception e) {
            		LOG.error("Queue Consumer Callback error", e);
            	}
            	return new CompletableFuture<Void>();
            }
        };
        rabbitManager.bindConsumer(channel, consumer, queue);
    }

	public void setMessageConverter(MessageConverter<T> messageConverter) {
		this.messageConverter = messageConverter;
	}
}