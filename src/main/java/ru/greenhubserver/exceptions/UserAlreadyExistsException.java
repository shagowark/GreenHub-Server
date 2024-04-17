package ru.greenhubserver.exceptions;

public class UserAlreadyExistsException extends BadRequestException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
