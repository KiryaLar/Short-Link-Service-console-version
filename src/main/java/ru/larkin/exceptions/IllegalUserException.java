package ru.larkin.exceptions;

public class IllegalUserException extends Exception{
    @Override
    public String getMessage() {
        return "There is not such user";
    }
}
