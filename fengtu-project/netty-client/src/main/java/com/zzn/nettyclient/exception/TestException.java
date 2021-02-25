package com.zzn.nettyclient.exception;

public class TestException extends RuntimeException {

    private String message;
    private Integer code;


    public TestException(String message) {
        super(message);
        this.message = message;
    }

    public TestException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code=code;
    }

    public TestException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
