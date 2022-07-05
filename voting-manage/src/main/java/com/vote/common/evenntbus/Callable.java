package com.vote.common.evenntbus;

@FunctionalInterface
public interface Callable<T> {

    void call(T data);

}