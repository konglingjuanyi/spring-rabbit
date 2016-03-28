package br.com.zup.spring.rabbit.infra;

public interface MessageConverter<T extends QMessage> {

    T convert(byte[] body);
}
