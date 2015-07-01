package net.nemerosa.httpclient;

import static java.lang.String.format;

public abstract class ClientException extends RuntimeException {

    public ClientException(String message, Object... params) {
        super(format(message, params));
    }

    public ClientException(Exception error, String message, Object... params) {
        super(format(message, params), error);
    }
}
