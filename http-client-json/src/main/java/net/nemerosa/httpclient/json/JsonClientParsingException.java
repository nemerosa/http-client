package net.nemerosa.httpclient.json;

import net.nemerosa.httpclient.ClientException;

import java.io.IOException;

public class JsonClientParsingException extends ClientException {
    public JsonClientParsingException(IOException e) {
        super(e, "Cannot parse JSON");
    }
}
