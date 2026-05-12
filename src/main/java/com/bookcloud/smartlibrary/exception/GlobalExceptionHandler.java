package com.bookcloud.smartlibrary.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> notFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<Map<String, String>> businessRule(BusinessRuleException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> forbidden(AccessDeniedException ex) {
		String message = ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : "Access denied";
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", message));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.findFirst()
				.orElse("Validation failed");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, String>> responseStatus(ResponseStatusException ex) {
		HttpStatusCode status = ex.getStatusCode();
		String message = ex.getReason() != null && !ex.getReason().isBlank() ? ex.getReason() : "Request failed";
		return ResponseEntity.status(status).body(Map.of("error", message));
	}
}
