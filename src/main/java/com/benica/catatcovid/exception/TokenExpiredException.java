package com.benica.catatcovid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Your token has expired")
public class TokenExpiredException extends Exception
{
    private static final long serialVersionUID = 1L;
}