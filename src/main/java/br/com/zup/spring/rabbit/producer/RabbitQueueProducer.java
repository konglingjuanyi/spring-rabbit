package br.com.zup.spring.rabbit.producer;

import br.com.zup.spring.rabbit.infra.QDelivery;
import br.com.zup.spring.rabbit.manager.RabbitManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public final class RabbitQueueProducer implements QueueProducer {

    private static final Logger LOG = LogManager.getLogger(RabbitQueueProducer.class.getName());

    private final RabbitManager rabbitManager;

    public RabbitQueueProducer(RabbitManager rabbitManager) {
        this.rabbitManager = rabbitManager;
    }

    @Override
    public CompletableFuture<QDelivery.DeliveryStatus> publish(QDelivery delivery) {

        return CompletableFuture.supplyAsync(() -> {

            LOG.debug("Publishing message [{}] to [{}] ...", delivery.message.toString(), delivery.queue.name);

            rabbitManager.publish(delivery);

            return QDelivery.DeliveryStatus.SENT;
        });
    }

}
