package it.eng.parer.common.signature;

/**
 * Constants about the session of signature
 *
 * @author Moretti_Lu
 */
public abstract class SignatureSession {

    private SignatureSession() {
    }

    /**
     * The types of session error
     */
    public enum CdErr {
        HSM_NOT_RESPOND, ERROR_SESSION_CREATION, USER_BLOCKED, AUTH_WRONG, OTP_WRONG, OTP_EXPIRED, ERROR_SIGNING,
        BLOCKED_SESSION, UNKNOWN_ERROR
    }
}
