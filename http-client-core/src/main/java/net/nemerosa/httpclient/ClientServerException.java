package net.nemerosa.httpclient;


public class ClientServerException extends ClientMessageException {

    private final int statusCode;
    private final String reasonPhrase;

    public ClientServerException(Object request, int statusCode, String reasonPhrase) {
        super(String.format("%s [%d] %s", request, statusCode, reasonPhrase));
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
