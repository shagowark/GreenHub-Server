package ru.greenhubserver.exceptions;

public class UserBannedException extends RuntimeException{
    public UserBannedException(String message) {
        super(message);
    }
}
