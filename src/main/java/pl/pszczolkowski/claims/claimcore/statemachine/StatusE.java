package pl.pszczolkowski.claims.claimcore.statemachine;

import pl.pszczolkowski.claims.claimcore.entities.StatusProcess;
import pl.pszczolkowski.claims.claimcore.enums.ActionE;
import pl.pszczolkowski.claims.claimcore.exceptions.ClaimProcessingException;

import java.util.Collections;
import java.util.List;

public enum StatusE implements StatusProcess<StatusE> {

    CREATED(List.of(ActionE.DELETE, ActionE.VERIFY, ActionE.EDIT)) {
        @Override
        public StatusE positiveProcess() {
            return StatusE.VERIFIED;
        }

        @Override
        public StatusE negativeProcess() {
            return StatusE.DELETED;
        }
    },
    DELETED(Collections.emptyList()) {
        @Override
        public StatusE positiveProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }

        @Override
        public StatusE negativeProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }
    },
    VERIFIED(List.of(ActionE.REJECT, ActionE.ACCEPT, ActionE.EDIT)) {
        @Override
        public StatusE positiveProcess() {
            return StatusE.ACCEPTED;
        }

        @Override
        public StatusE negativeProcess() {
            return StatusE.REJECTED;
        }
    },
    REJECTED(Collections.emptyList()) {
        @Override
        public StatusE positiveProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }

        @Override
        public StatusE negativeProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }
    },
    ACCEPTED(List.of(ActionE.REJECT, ActionE.PUBLISH)) {
        @Override
        public StatusE positiveProcess() {
            return StatusE.PUBLISHED;
        }

        @Override
        public StatusE negativeProcess() {
            return StatusE.REJECTED;
        }
    },
    PUBLISHED(Collections.emptyList()) {
        @Override
        public StatusE positiveProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }

        @Override
        public StatusE negativeProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }
    };

    public final List<ActionE> allowedActions;

    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported.";

    StatusE(List<ActionE> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
