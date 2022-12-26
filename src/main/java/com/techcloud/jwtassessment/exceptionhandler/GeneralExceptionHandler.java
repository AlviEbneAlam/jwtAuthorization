package com.techcloud.jwtassessment.exceptionhandler;

import com.techcloud.jwtassessment.GeneralExceptions.ResourceNotFoundException;
import com.techcloud.jwtassessment.GeneralExceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ACCESS_DENIED = "Access denied!";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String ERROR_MESSAGE_TEMPLATE = "message: %s %n requested uri: %s";
    public static final String LIST_JOIN_DELIMITER = ",";
    public static final String FIELD_ERROR_SEPARATOR = ": ";
    private static final Logger local_logger = LoggerFactory.getLogger(GeneralExceptionHandler.class);
    private static final String ERRORS_FOR_PATH = "errors {} for path {}";
    private static final String PATH = "path";
    private static final String ERRORS = "error";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
    private static final String TYPE = "type";


    /**
     * Build a detailed information about the exception in the response
     */
    private ResponseEntity<Object> getExceptionResponseEntity(final Exception exception,
                                                              final HttpStatus status,
                                                              final WebRequest request,
                                                              final List<String> errors) {
        final Map<String, Object> body = new LinkedHashMap<>();
        final String path = request.getDescription(false);
        body.put(TIMESTAMP, Instant.now());
        body.put(STATUS, status.value());
        body.put(ERRORS, errors);
        body.put(TYPE, exception.getClass().getSimpleName());
        body.put(PATH, path);
        body.put(MESSAGE, getMessageForStatus(status));


       // final String path = request.getDescription(false);
        errors.size();
        final String errorsMessage = (!CollectionUtils.isEmpty(errors)) ?
                errors.stream().filter(t->!t.isEmpty()).collect(Collectors.joining(LIST_JOIN_DELIMITER))
                :status.getReasonPhrase();


        log.info("Specific error message is: "+errorsMessage);
        local_logger.error(ERRORS_FOR_PATH, errorsMessage, path);


    return new ResponseEntity<Object>(body, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + FIELD_ERROR_SEPARATOR + error.getDefaultMessage())
                .collect(Collectors.toList());
        return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
    }



    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return getExceptionResponseEntity(exception, status, request, Collections.singletonList(exception.getLocalizedMessage()));
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> unAuthorizedException(UnauthorizedException unauthorizedException,
                                                            WebRequest request) {
        return getExceptionResponseEntity(unauthorizedException, HttpStatus.UNAUTHORIZED, request, Collections.singletonList(unauthorizedException.getLocalizedMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException resourceNotFoundException,
                                                            WebRequest request) {
        return getExceptionResponseEntity(resourceNotFoundException, HttpStatus.NOT_FOUND, request, Collections.singletonList(resourceNotFoundException.getLocalizedMessage()));
    }



    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        return getExceptionResponseEntity(exception, status, request,
                Collections.singletonList(exception.getLocalizedMessage()));
    }

    /**
     * A general handler for all uncaught exceptions
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {
        ResponseStatus responseStatus =
                exception.getClass().getAnnotation(ResponseStatus.class);
        final HttpStatus status =
                responseStatus!=null ? responseStatus.value(): HttpStatus.INTERNAL_SERVER_ERROR;
        final String localizedMessage = exception.getLocalizedMessage();
        final String path = request.getDescription(false);
        String message = (!StringUtils.isEmpty(localizedMessage) ? localizedMessage:status.getReasonPhrase());
        logger.error(String.format(ERROR_MESSAGE_TEMPLATE, message, path), exception);
        return getExceptionResponseEntity(exception, status, request, Collections.singletonList(message));
    }



    private String getMessageForStatus(HttpStatus status) {
        switch (status) {
            case UNAUTHORIZED:
                return ACCESS_DENIED;
            case BAD_REQUEST:
                return INVALID_REQUEST;
            default:
                return status.getReasonPhrase();
        }
    }
}
