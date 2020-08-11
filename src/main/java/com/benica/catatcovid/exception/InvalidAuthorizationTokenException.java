package com.benica.catatcovid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid Authorization Token")
public class InvalidAuthorizationTokenException extends Exception
{
    private static final long serialVersionUID = 1L;
}