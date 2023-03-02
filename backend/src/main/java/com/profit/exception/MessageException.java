package com.profit.exception;

/**
 * @Author:liulongling
 * @Date:2022/4/26 10:00
 */
public class MessageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MessageException(String message, Exception e) {
        super(message, e);
    }

    public MessageException(String message) {
        super(message);
    }
}
