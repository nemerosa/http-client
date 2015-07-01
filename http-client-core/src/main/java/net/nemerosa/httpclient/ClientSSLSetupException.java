package net.nemerosa.httpclient;

public class ClientSSLSetupException extends ClientException {
    public ClientSSLSetupException(Exception ex) {
        super(ex, "Cannot setup SSL client");
    }
}


