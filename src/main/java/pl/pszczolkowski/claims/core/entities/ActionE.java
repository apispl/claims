package pl.pszczolkowski.claims.core.entities;

public enum ActionE {

    CREATE(true),
    DELETE(false),
    VERIFY(true),
    REJECT(false),
    ACCEPT(true),
    PUBLISH(true);

    public final boolean isPositive;

    ActionE(boolean isPositive) {
        this.isPositive = isPositive;
    }
}