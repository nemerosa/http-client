package net.nemerosa.httpclient;

public class ClientCannotGetContentException extends ClientException {
    public ClientCannotGetContentException(String context, Exception e) {
        super(e, "Cannot get any response from %s: %s", context, e);
    }
}
