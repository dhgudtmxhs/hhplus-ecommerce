package kr.hhplus.be.server.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bean validation 예외 처리 (RequestBody 검증) 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String errorCode = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        ErrorCode error = ErrorCode.fromCode(errorCode);
        ErrorResponse response = new ErrorResponse(error.getCode(), error.getMessage());

        log.warn("MethodArgumentNotValidException 예외 발생 - code: {}, message: {}", error.getCode(), error.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Bean validation 예외 처리 (RequestParam, PathVariable 검증) 400
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {

        String errorCode = ex.getConstraintViolations().iterator().next().getMessage();

        ErrorCode error = ErrorCode.fromCode(errorCode);
        ErrorResponse response = new ErrorResponse(error.getCode(), error.getMessage());

        log.warn("ConstraintViolationException 예외 발생 - code: {}, message: {}", error.getCode(), error.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // IllegalArgumentException 예외 처리 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String code = ex.getMessage();
        ErrorCode errorCode = ErrorCode.fromCode(code);

        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        log.warn("IllegalArgumentException 예외 발생 - code: {}, message: {}", errorCode.getCode(), errorCode.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // NoSuchElementException 예외 처리 404
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {

        String code = ex.getMessage();
        ErrorCode errorCode = ErrorCode.fromCode(code);

        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        log.error("NoSuchElementException 예외 발생 - code: {}, message: {}", errorCode.getCode(), errorCode.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 핸들링 되지 않은 예외 처리 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unhandled exception 예외 발생 - code: {}, message: {}", errorCode.getCode(), errorCode.getMessage());

        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}