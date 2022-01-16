package pl.pszczolkowski.claims.exceptions;

public class ClaimNotFoundException extends Exception {

    public ClaimNotFoundException() {
    }

    ClaimNotFoundException(String message) {
        super(message);
    }

    ClaimNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
