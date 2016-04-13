package br.com.zup.spring.rabbit.infra;

public final class JsonMessageConverter implements MessageConverter<JsonMessage> {

    public JsonMessage convert(byte[] body) {

        return new JsonMessage(body);
    }
}
