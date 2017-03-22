package com.damianmichalak.shopping_list.helper.guava;

import java.io.Serializable;

public class Optional<T> implements Serializable {

    private T value;

    public Optional(T value) {
        this.value = value;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<T>(value);
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isAbsent() {
        return value == null;
    }

    public T get() {
        return value;
    }

    public static <T> Optional<T> absent() {
        return new Optional<T>(null);
    }
}