package pl.pszczolkowski.claims.claimcore.entities;

import pl.pszczolkowski.claims.claimcore.exceptions.ClaimProcessingException;

public interface StatusProcess<T> {

    T positiveProcess() throws ClaimProcessingException;
    T negativeProcess() throws ClaimProcessingException;
}
