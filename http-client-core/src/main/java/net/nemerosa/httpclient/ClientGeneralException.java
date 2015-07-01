package net.nemerosa.httpclient;


public class ClientGeneralException extends ClientMessageException {

    public ClientGeneralException(Object request, Exception ex) {
        super(ex, String.format("Error while executing %s: %s", request, ex));
    }

}
