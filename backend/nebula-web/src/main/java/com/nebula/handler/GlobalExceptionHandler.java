package com.nebula.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.nebula.exception.BusinessException;
import com.nebula.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一处理异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理Sa-Token未登录异常
     * @param e 未登录异常
     * @return 错误响应
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Response<?>> handleNotLoginException(NotLoginException e) {
        log.error("未登录异常: {}", e.getMessage(), e);
        Response<?> errorResponse = Response.error(401, "请先登录");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理业务异常
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<?>> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        Response<?> errorResponse = Response.error(e.getCode(), e.getMessage());
        // 根据异常码设置合适的HTTP状态码
        HttpStatus status = getHttpStatusByCode(e.getCode());
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * 处理参数校验异常
     * @param e 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验失败: {}", e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        Response<?> errorResponse = Response.error(400, errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数校验异常
     * @param e 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<Response<?>> handleValidationException(org.springframework.validation.BindException e) {
        log.error("参数校验异常: {}", e.getMessage(), e);
        Response<?> errorResponse = Response.error(400, "参数校验异常: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数类型转换异常
     * @param e 参数类型转换异常
     * @return 错误响应
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<?>> handleTypeMismatchException(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e) {
        log.error("参数类型转换异常: {}", e.getMessage(), e);
        Response<?> errorResponse = Response.error(400, "参数类型转换异常: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数缺失异常
     * @param e 参数缺失异常
     * @return 错误响应
     */
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<Response<?>> handleMissingServletRequestParameterException(org.springframework.web.bind.MissingServletRequestParameterException e) {
        log.error("参数缺失异常: {}", e.getMessage(), e);
        Response<?> errorResponse = Response.error(400, "参数缺失异常: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }



    /**
     * 处理其他未捕获的异常
     * @param e 未捕获的异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception e) {
        log.error("系统错误: {}", e.getMessage(), e);
        Response<?> errorResponse = Response.error(500, "系统错误: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 根据异常码获取对应的HTTP状态码
     * @param code 异常码
     * @return HTTP状态码
     */
    private HttpStatus getHttpStatusByCode(int code) {
        return switch (code) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
