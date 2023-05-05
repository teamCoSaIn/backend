package com.cosain.trilo.trip.command.domain.exception;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidTripDayException extends CustomException {

    private static final String ERROR_NAME = "InvalidTripDay";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidTripDayException() {
    }

    public InvalidTripDayException(String debugMessage) {
        super(debugMessage);
    }

    public InvalidTripDayException(Throwable cause) {
        super(cause);
    }

    public InvalidTripDayException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    @Override
    public String getErrorName() {
        return ERROR_NAME;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
