package com.cosain.trilo.common.exception.trip;

import com.cosain.trilo.common.exception.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 여행 삭제 권한이 없을 때 발생하는 예외입니다.
 */
public class NoTripDeleteAuthorityException extends CustomException {

    private static final String ERROR_CODE = "trip-0007";
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public NoTripDeleteAuthorityException() {
    }

    public NoTripDeleteAuthorityException(String debugMessage) {
        super(debugMessage);
    }

    public NoTripDeleteAuthorityException(Throwable cause) {
        super(cause);
    }

    public NoTripDeleteAuthorityException(String debugMessage, Throwable cause) {
        super(debugMessage, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
