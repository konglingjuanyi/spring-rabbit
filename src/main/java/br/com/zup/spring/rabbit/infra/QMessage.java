package br.com.zup.spring.rabbit.infra;

import java.io.Serializable;

public interface QMessage extends Serializable {

    byte[] getBody();

    String toString();
}
