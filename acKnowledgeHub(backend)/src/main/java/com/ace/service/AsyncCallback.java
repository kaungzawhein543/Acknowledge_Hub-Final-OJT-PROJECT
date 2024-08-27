package com.ace.service;

@FunctionalInterface
public interface AsyncCallback<T> {
    void onSuccess(T result);
    default void onFailure(Throwable throwable) {
        throwable.printStackTrace();
    }
}
