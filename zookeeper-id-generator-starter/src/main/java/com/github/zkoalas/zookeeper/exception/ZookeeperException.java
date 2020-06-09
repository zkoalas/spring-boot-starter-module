package com.github.zkoalas.zookeeper.exception;



public class ZookeeperException extends RuntimeException {
    private static final long serialVersionUID = 7895884193269203187L;

    public ZookeeperException() {
        super();
    }

    public ZookeeperException(String message) {
        super(message);
    }

    public ZookeeperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperException(Throwable cause) {
        super(cause);
    }
}