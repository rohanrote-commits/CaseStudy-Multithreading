package CustomException;

public class BankingOperationException extends Exception {
    public String message;
    public String code;

    public BankingOperationException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;

    }
}
