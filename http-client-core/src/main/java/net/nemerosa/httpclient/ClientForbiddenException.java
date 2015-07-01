package net.nemerosa.httpclient;

import java.util.Objects;

public class ClientForbiddenException extends ClientException {

    public ClientForbiddenException(Object request) {
        super(Objects.toString(request, ""));
    }

}
