package ru.greenhubserver.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import ru.greenhubserver.exceptions.*;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    @ApiResponse(responseCode = "400", description = "Bad request, ошибка в запросе")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError catchBadRequestException(BadRequestException e) {
        return new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Not found, cущность не найдена")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppError catchNotFoundException(NotFoundException e) {
        return new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ApiResponse(responseCode = "401", description = "Unauthorized, не авторизован")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AppError catchBadCredentialsException(BadCredentialsException e) {
        return new AppError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(UserBannedException.class)
    @ApiResponse(responseCode = "406", description = "Unauthorized, не авторизован")
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AppError catchBadCredentialsException(UserBannedException e) {
        return new AppError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }

    @ExceptionHandler(NoRightsException.class)
    @ApiResponse(responseCode = "403", description = "Forbidden, нет прав на доступ к ресурсу")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AppError catchForbiddenEndpointException(NoRightsException e) {
        return new AppError(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ApiResponse(responseCode = "404", description = "Bad request, ошибка валидации полей")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleValidationExceptions(ValidationException e) {
        return new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public AppError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder builder = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> builder.append(error.getDefaultMessage()).append("\n"));
        return new AppError(HttpStatus.BAD_REQUEST.value(), builder.toString());
    }
}
