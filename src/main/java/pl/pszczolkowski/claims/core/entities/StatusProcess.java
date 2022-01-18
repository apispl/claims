package pl.pszczolkowski.claims.core.entities;

import pl.pszczolkowski.claims.core.exceptions.ClaimProcessingException;

public interface StatusProcess<T> {

    T positiveProcess() throws ClaimProcessingException;
    T negativeProcess() throws ClaimProcessingException;
}
