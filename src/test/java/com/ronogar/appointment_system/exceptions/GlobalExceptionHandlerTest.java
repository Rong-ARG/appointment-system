package com.ronogar.appointment_system.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private void assertResponse(ResponseEntity<Map<String, String>> response, HttpStatus status, String key, String message) {
        assertEquals(status, response.getStatusCode());
        assertEquals(message, response.getBody().get(key));
    }

    @Test
    void handleResourceNotFound_returns404WithMessage() {
        assertResponse(handler.handleResourceNotFoundException(new ResourceNotFoundException("not found")),
                HttpStatus.NOT_FOUND, "message", "not found");
    }

    @Test
    void handleDuplicateResource_returns409WithMessage() {
        assertResponse(handler.handleDuplicateResourceException(new DuplicateResourceException("duplicated")),
                HttpStatus.CONFLICT, "message", "duplicated");
    }

    @Test
    void handleAccessDenied_returns403WithMessage() {
        assertResponse(handler.handleAccessDeniedException(new AccessDeniedException("denied")),
                HttpStatus.FORBIDDEN, "message", "denied");
    }

    @Test
    void handleInvalidAccountState_returns409WithMessage() {
        assertResponse(handler.handleInvalidAccountStateException(new InvalidAccountStateException("bad account")),
                HttpStatus.CONFLICT, "message", "bad account");
    }

    @Test
    void handleInvalidAppointmentState_returns409WithMessage() {
        assertResponse(handler.handleInvalidAppointmentState(new InvalidAppointmentStateException("bad state")),
                HttpStatus.CONFLICT, "message", "bad state");
    }

    @Test
    void handleBadCredentials_returns401WithMessage() {
        assertResponse(handler.handleBadCredentialsException(new BadCredentialsException("bad creds")),
                HttpStatus.UNAUTHORIZED, "message", "bad creds");
    }

    @Test
    void handleValidation_returns400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "email", "must not be blank")));

        assertResponse(handler.handleValidationException(ex), HttpStatus.BAD_REQUEST, "email", "must not be blank");
    }

    @Test
    void handleHttpMessageNotReadable_returns400GenericMessage() {
        assertResponse(handler.handleHttpMessageNotReadableException(mock(HttpMessageNotReadableException.class)),
                HttpStatus.BAD_REQUEST, "message", "Malformed JSON or invalid value in request body");
    }

    @Test
    void handleNoResourceFound_returns404() {
        assertResponse(handler.handleNoResourceFound(mock(NoResourceFoundException.class)),
                HttpStatus.NOT_FOUND, "message", "Resource not found");
    }

    @Test
    void handleMethodNotSupported_returns405() {
        assertResponse(handler.handleMethodNotSupported(new HttpRequestMethodNotSupportedException("PUT")),
                HttpStatus.METHOD_NOT_ALLOWED, "message", "Method not allowed");
    }

    @Test
    void handleTypeMismatch_returns400WithParameterName() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");

        assertResponse(handler.handleTypeMismatch(ex), HttpStatus.BAD_REQUEST, "message", "Invalid value for parameter 'id'");
    }

    @Test
    void handleDataIntegrity_returns409WithoutLeakingDetails() {
        assertResponse(handler.handleDataIntegrity(new DataIntegrityViolationException("SQL secret")),
                HttpStatus.CONFLICT, "message", "The request conflicts with existing data");
    }

    @Test
    void handleGenericException_returns500WithoutLeakingDetails() {
        assertResponse(handler.handleGenericException(new RuntimeException("internal detail")),
                HttpStatus.INTERNAL_SERVER_ERROR, "message", "Something went wrong. Please try again.");
    }
}