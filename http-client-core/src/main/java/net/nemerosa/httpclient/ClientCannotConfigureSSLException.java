package net.nemerosa.httpclient;

import java.security.GeneralSecurityException;

public class ClientCannotConfigureSSLException extends ClientException {
    public ClientCannotConfigureSSLException(GeneralSecurityException e) {
        super(e, "Cannot configure SSL");
    }
}
