/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.stoicfree.free.module.core.mvc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.stoicfree.free.module.core.common.domain.Result;
import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.exception.BizException;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 *
 * @author zengzhifei
 * @date 2022/11/29 16:29
 */
@Slf4j
public abstract class AbstractExceptionHandler {
    @ExceptionHandler(BizException.class)
    public Result<String> handleBizException(BizException e) {
        log.error("handleBizException", e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        log.warn("handleMissingServletRequestParameterException", e);
        String message = String.format("缺少%s参数", e.getParameterName());
        return Result.fail(ErrorCode.EMPTY_PARAMS.getCode(), message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("handleMethodArgumentTypeMismatchException", e);
        String message = String.format("%s参数类型错误", e.getName());
        return Result.fail(ErrorCode.INVALID_PARAMS.getCode(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("handleMethodArgumentNotValidException", e);
        String message = null;
        for (ObjectError objectError : e.getBindingResult().getAllErrors()) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                message = fieldError.getDefaultMessage();
            }
        }
        return Result.fail(ErrorCode.INVALID_PARAMS.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        log.warn("handleBindException", e);
        String message = null;
        for (ObjectError objectError : e.getAllErrors()) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return Result.fail(ErrorCode.INVALID_PARAMS.getCode(), message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("handleIllegalArgumentException", e);
        return Result.fail(ErrorCode.INVALID_PARAMS.getCode(), ErrorCode.INVALID_PARAMS.getMsg());
    }

    @ExceptionHandler(IllegalStateException.class)
    public Result<String> handleIllegalStateException(IllegalStateException e) {
        log.error("handleIllegalArgumentException", e);
        return Result.fail(ErrorCode.UNKNOWN_ERROR.getCode(), ErrorCode.UNKNOWN_ERROR.getMsg());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadableException", e);
        return Result.fail(ErrorCode.UNKNOWN_ERROR.getCode(), ErrorCode.UNKNOWN_ERROR.getMsg());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        return Result.fail(ErrorCode.UNKNOWN_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("handleHttpMediaTypeNotSupportedException", e);
        return Result.fail(ErrorCode.UNKNOWN_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleNullPointerException(NullPointerException e) {
        log.error("handleNullPointerException", e);
        return Result.fail(ErrorCode.UNKNOWN_ERROR.getCode(), ErrorCode.UNKNOWN_ERROR.getMsg());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleOtherException(Exception e) {
        log.error("handleOtherException", e);
        return Result.fail(ErrorCode.UNKNOWN_ERROR.getCode(), ErrorCode.UNKNOWN_ERROR.getMsg());
    }
}
