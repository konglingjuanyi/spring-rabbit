package br.com.zup.spring.rabbit.infra;

public class Queue {

    public final String name;

    public final boolean durable;

    public final boolean exclusive;

    public final boolean autoDelete;

    public Queue(String name) {
        this.name = name;
        this.durable = true;
        this.exclusive = false;
        this.autoDelete = false;
    }

    public Queue(String name, boolean durable) {
        this.name = name;
        this.durable = durable;
        this.exclusive = false;
        this.autoDelete = false;
    }
}
