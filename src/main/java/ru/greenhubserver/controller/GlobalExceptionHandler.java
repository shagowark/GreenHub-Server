package ru.greenhubserver.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import ru.greenhubserver.exceptions.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    @ApiResponse(responseCode = "400", description = "Bad request, ошибка в запросе")
    public ResponseEntity<AppError> catchBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Not found, cущность не найдена")
    public ResponseEntity<AppError> catchNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ApiResponse(responseCode = "401", description = "Unauthorized, не авторизован")
    public ResponseEntity<AppError> catchBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect login or password"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoRightsException.class)
    @ApiResponse(responseCode = "403", description = "Forbidden, нет прав на доступ к ресурсу")
    public ResponseEntity<AppError> catchForbiddenEndpointException(NoRightsException e) {
        return new ResponseEntity<>(new AppError(HttpStatus.FORBIDDEN.value(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

}
