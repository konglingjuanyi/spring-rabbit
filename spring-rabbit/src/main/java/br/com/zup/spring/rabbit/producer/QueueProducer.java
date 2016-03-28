package br.com.zup.spring.rabbit.producer;

import java.util.concurrent.CompletableFuture;

import br.com.zup.spring.rabbit.infra.QDelivery;


public interface QueueProducer {

    CompletableFuture<QDelivery.DeliveryStatus> publish(QDelivery delivery);

}
