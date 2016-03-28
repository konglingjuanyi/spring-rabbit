package br.com.zup.spring.rabbit.infra;

public class QDelivery {

    public final QMessage message;

    public final Queue queue;

    public QDelivery(QMessage message, Queue queue) {
        this.message = message;
        this.queue = queue;
    }

    public static enum DeliveryStatus {
        SENT,
        ERROR;
    }
}
