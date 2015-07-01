package net.nemerosa.httpclient.json;

import net.nemerosa.httpclient.ClientException;

import java.io.IOException;

public class JsonClientMappingException extends ClientException {
    public JsonClientMappingException(IOException e) {
        super(e, "Cannot parse JSON");
    }
}
