package com.benica.catatcovid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Username already exist.")
public class UsernameAlreadyExistException extends Exception
{
    private static final long serialVersionUID = 1L;
}