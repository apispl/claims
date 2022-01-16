package pl.pszczolkowski.claims;

public class ClaimContentCannotBeChanged extends Exception {

    ClaimContentCannotBeChanged() {
    }

    ClaimContentCannotBeChanged(String message) {
        super(message);
    }

    ClaimContentCannotBeChanged(String message, Throwable cause) {
        super(message, cause);
    }
}
