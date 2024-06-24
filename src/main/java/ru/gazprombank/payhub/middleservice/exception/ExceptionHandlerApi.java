package ru.gazprombank.payhub.middleservice.exception;

import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerApi {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String onMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String exception = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new StringBuilder().append(error.getField()).append(": ").append(error.getDefaultMessage()))
                .collect(Collectors.joining(System.lineSeparator()));
        log.error(exception);
        return exception;
    }

    @ExceptionHandler(RetryableException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String onRetryableException(final RetryableException e) {
        log.error(e.getMessage());
        return "Попробуйте позже";
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String onFeignException(final FeignException e) {
        log.error(e.getMessage());
        if (e.status() == 409) {
            return "Пользователь уже зарегистрирован";
        }
        return e.getMessage();
    }
}