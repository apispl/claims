package pl.pszczolkowski.claims.core.entities;

import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;

public enum StatusE implements StatusProcess<StatusE> {

    CREATED {
        @Override
        public StatusE positiveProcess() {
            return StatusE.VERIFIED;
        }

        @Override
        public StatusE negativeProcess() {
            return StatusE.DELETED;
        }
    },
    DELETED {
        @Override
        public StatusE positiveProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }

        @Override
        public StatusE negativeProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }
    },
    VERIFIED {
        @Override
        public StatusE positiveProcess() {
            return StatusE.ACCEPTED;
        }

        @Override
        public StatusE negativeProcess() {
            return StatusE.REJECTED;
        }
    },
    REJECTED {
        @Override
        public StatusE positiveProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }

        @Override
        public StatusE negativeProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }
    },
    ACCEPTED {
        @Override
        public StatusE positiveProcess() {
            return StatusE.PUBLISHED;
        }

        @Override
        public StatusE negativeProcess() {
            return StatusE.REJECTED;
        }
    },
    PUBLISHED {
        @Override
        public StatusE positiveProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }

        @Override
        public StatusE negativeProcess() throws ClaimProcessingException {
            throw new ClaimProcessingException(OPERATION_NOT_SUPPORTED);
        }
    };

    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported.";

}
