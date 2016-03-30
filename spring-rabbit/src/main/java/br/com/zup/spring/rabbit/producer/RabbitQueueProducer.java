package br.com.zup.spring.rabbit.producer;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.zup.spring.rabbit.infra.QDelivery;
import br.com.zup.spring.rabbit.manager.RabbitManager;

public final class RabbitQueueProducer implements QueueProducer {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitQueueProducer.class);

    private final RabbitManager rabbitManager;

    public RabbitQueueProducer(RabbitManager rabbitManager) {
        this.rabbitManager = rabbitManager;
    }

    @Override
    public CompletableFuture<QDelivery.DeliveryStatus> publish(QDelivery delivery) {

        return CompletableFuture.supplyAsync(() -> {

            LOG.debug("Sending message [{}] to [{}] ...", delivery.message.toString(), delivery.queue.name);

            rabbitManager.publish(delivery);

            return QDelivery.DeliveryStatus.SENT;
        });
    }

}
