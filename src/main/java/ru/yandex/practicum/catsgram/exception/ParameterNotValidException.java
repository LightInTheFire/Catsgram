package ru.yandex.practicum.catsgram.exception;

import lombok.Getter;

@Getter
public class ParameterNotValidException extends IllegalArgumentException {
    private String parameter;
    private String reason;

    public ParameterNotValidException(String reason, String parameter) {
        this.reason = reason;
        this.parameter = parameter;
    }

}
