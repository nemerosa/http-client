package net.nemerosa.httpclient;

import java.util.Objects;

public class ClientCannotLoginException extends ClientException {

    public ClientCannotLoginException(Object request) {
        super(Objects.toString(request, ""));
    }

}
