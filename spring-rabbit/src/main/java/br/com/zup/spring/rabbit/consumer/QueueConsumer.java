package br.com.zup.spring.rabbit.consumer;


import java.util.function.Consumer;

import br.com.zup.spring.rabbit.infra.MessageConverter;
import br.com.zup.spring.rabbit.infra.QMessage;

public interface QueueConsumer<T extends QMessage> {

    void consumeSync(Consumer<T> successCallback, boolean requeueWhenFailure);
    
    void consumeAsync(Consumer<T> successCallback, boolean requeueWhenFailure);

    void setMessageConverter(MessageConverter<T> messageConverter);
}
