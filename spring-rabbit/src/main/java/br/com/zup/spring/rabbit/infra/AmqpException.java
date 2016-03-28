package br.com.zup.spring.rabbit.infra;

public class AmqpException extends RuntimeException {

    private static final long serialVersionUID = 3752347258263745787L;

    public AmqpException(Throwable cause) {
        super(cause);
    }

    public AmqpException(String message, Throwable cause) {
        super(message, cause);
    }
}
