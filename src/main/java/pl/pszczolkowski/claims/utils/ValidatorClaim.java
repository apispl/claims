package pl.pszczolkowski.claims.utils;

import pl.pszczolkowski.claims.claimcore.enums.ActionE;
import pl.pszczolkowski.claims.claimcore.exceptions.ClaimProcessingException;
import pl.pszczolkowski.claims.claimcore.statemachine.StatusE;

public class ValidatorClaim {

    ValidatorClaim() {}

    public static void checkIsActionAllowed(ActionE action, StatusE status) throws ClaimProcessingException {
        boolean isNotAllowedAction = !status.allowedActions.contains(action);
        if (isNotAllowedAction)
            throw new ClaimProcessingException("Action not allowed.");
    }

    public static void checkIsStringNull(String str) throws ClaimProcessingException {
        if (str == null)
            throw new ClaimProcessingException("Reason cannot be null.");
    }
}
