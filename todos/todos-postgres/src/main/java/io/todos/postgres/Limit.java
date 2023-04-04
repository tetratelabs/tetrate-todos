package io.todos.postgres;

public class Limit {

    private long size;
    private long limit;

    public Limit(long size, long limit) {
        this.size = size;
        this.limit = limit;
    }
}
