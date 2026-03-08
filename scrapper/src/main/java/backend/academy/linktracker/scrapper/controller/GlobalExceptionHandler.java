package backend.academy.linktracker.scrapper.controller;

import backend.academy.linktracker.scrapper.dto.ApiErrorResponse;
import backend.academy.linktracker.scrapper.exception.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.exception.LinkAlreadyTrackedException;
import backend.academy.linktracker.scrapper.exception.LinkNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleChatAlreadyExists(ChatAlreadyExistsException e) {
        return buildResponse(HttpStatus.CONFLICT, "Chat already exists", e);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleChatNotFound(ChatNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Chat not found", e);
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkNotFound(LinkNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Link not found", e);
    }

    @ExceptionHandler(LinkAlreadyTrackedException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkAlreadyTracked(LinkAlreadyTrackedException e) {
        return buildResponse(HttpStatus.CONFLICT, "Link already tracked", e);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String description, RuntimeException e) {
        ApiErrorResponse response = new ApiErrorResponse(
                description, String.valueOf(status.value()), e.getClass().getSimpleName(), e.getMessage(), List.of());

        return ResponseEntity.status(status).body(response);
    }
}
