package web.thaiticketmajor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    CONFIGURATION_STARTKWH_EXISTED(1001, "StartKWH already existed!", HttpStatus.BAD_REQUEST),
    CONFIGURATION_ENDKWH_EXISTED(1002, "EndKWH already existed!", HttpStatus.BAD_REQUEST),
    CONFIGURATION_VALIDRANGE_ERROR(1003, "EndKWH must be greater than StartKWH!", HttpStatus.BAD_REQUEST),
    CONFIGURATION_STARTKWH_INVALID(1004, "StartKWH must be greater than 0!", HttpStatus.BAD_REQUEST),
    USAGEHISTORY_CONSUMPTION_INVALID(1005, "Consumption must be greater than 0!", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1006, "Username already existed!", HttpStatus.CONFLICT),
    USERNAME_VALID(1007, "Username must be at least 3 characters!", HttpStatus.BAD_REQUEST),
    PASSWORD_VALID(1008, "Password must be at least 8 characters!", HttpStatus.BAD_REQUEST),
    EMAIL_VALID(1008, "Password must be at least 8 characters!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1009, "User does not exist!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1010, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    // UNAUTHORIZED(1011, "You do not have permission!", HttpStatus.FORBIDDEN),
    USER_ID_INVALID(1011, "UserID must be greater than 0 !", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(1012, "Role not found !", HttpStatus.FORBIDDEN),


    ;



    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
