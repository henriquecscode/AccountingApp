package com.fivault.fivault.service;

import com.fivault.fivault.service.exception.ErrorCode;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents the Output of an operation that can either succeed or fail
 */
public sealed interface Output<T> permits Output.Success, Output.Failure {

    record Success<T>(T data) implements Output<T> {
    }

    record Failure<T>(ErrorCode errorCode, String message) implements Output<T> {
    }

    // Factory methods
    static <T> Output<T> success(T data) {
        return new Success<>(data);
    }

    static <T> Output<T> failure(ErrorCode errorCode, String message) {
        return new Failure<>(errorCode, message);
    }

    static <T> Output<T> failure(ErrorCode errorCode) {
        return new Failure<>(errorCode, errorCode.getDefaultMessage());
    }

    // Utility methods
    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return this instanceof Failure<T>;
    }

    default Optional<T> getData() {
        if (this instanceof Success<T> success) {
            return Optional.of(success.data());
        }
        return Optional.empty();
    }

    default <U> Output<U> mapFailure() {
        if (this instanceof Failure<T> failure) {
            return Output.failure(failure.errorCode(), failure.message());
        }
        return (Output<U>) this; // it's a success, just cast
    }

    default Optional<ErrorCode> getErrorCode() {
        if (this instanceof Failure<T> failure) {
            return Optional.of(failure.errorCode());
        }
        return Optional.empty();
    }

    default Optional<String> getErrorMessage() {
        if (this instanceof Failure<T> failure) {
            return Optional.of(failure.message());
        }
        return Optional.empty();
    }

    // Functional methods
    default <U> Output<U> map(Function<T, U> mapper) {
        if (this instanceof Success<T> success) {
            return success(mapper.apply(success.data()));
        }
        return (Output<U>) this;
    }

    default void ifSuccess(Consumer<T> consumer) {
        if (this instanceof Success<T> success) {
            consumer.accept(success.data());
        }
    }

    default void ifFailure(Consumer<String> consumer) {
        if (this instanceof Failure<T> failure) {
            consumer.accept(failure.message());
        }
    }
}