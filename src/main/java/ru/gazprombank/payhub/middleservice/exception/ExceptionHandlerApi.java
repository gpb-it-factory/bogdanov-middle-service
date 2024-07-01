package ru.gazprombank.payhub.middleservice.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerApi {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseMessage onMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        String primaryError = errors.stream()
                .filter(error -> error.contains("не должно быть пустым"))
                .findFirst()
                .orElse(String.join(" ", errors));

        log.error(primaryError);
        return new ResponseMessage(primaryError);
    }

    @ExceptionHandler(feign.RetryableException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseMessage onRetryableException(final feign.RetryableException e) {
        log.error(e.getMessage());
        return new ResponseMessage("Попробуйте позже");
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseMessage onFeignException(final FeignException e) {
        log.error(e.getMessage());
        if (e.status() == 409) {
            return new ResponseMessage("Вы уже зарегистрировались");
        }
        return new ResponseMessage(e.getMessage());
    }
}