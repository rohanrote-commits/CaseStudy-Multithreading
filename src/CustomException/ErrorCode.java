package CustomException;

public enum ErrorCode {
    USER_NOT_FOUND("404","User Not Found"),
    AMOUNT_MUST_BE_POSITIVE("400","Amount must be positive"),
    INSUFFICIENT_BALANCE("500","Insufficient Balance"),
    ANOTHER_THREAD_EXECTING("410","Another Thread Executing"),;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
