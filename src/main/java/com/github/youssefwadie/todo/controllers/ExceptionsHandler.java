package com.github.youssefwadie.todo.controllers;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentMisMatchException(MethodArgumentTypeMismatchException ex) {
        MethodParameter parameter = ex.getParameter();
        Class<?> parameterType = parameter.getParameterType();
        boolean isNumber = Number.class.isAssignableFrom(parameterType);

        Map<String, String> mismatches = new HashMap<>();

        if (isNumber) {
            mismatches.put(ex.getName(), "out of range");
        }

        if (mismatches.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mismatches);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
