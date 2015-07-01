package net.nemerosa.httpclient;


public class ClientServerException extends ClientMessageException {

    public ClientServerException(Object request, int statusCode, String reasonPhrase) {
        super(String.format("%s [%d] %s", request, statusCode, reasonPhrase));
    }

}
