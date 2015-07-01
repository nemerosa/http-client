package net.nemerosa.httpclient.json;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.httpclient.Document;

public interface JsonClient {

    JsonNode toNode(Object data);

    JsonNode get(String path, Object... parameters);

    JsonNode delete(String path, Object... parameters);

    JsonNode post(Object data, String path, Object... parameters);

    JsonNode put(Object data, String path, Object... parameters);

    JsonNode upload(String name, Document o, String fileName, String path, Object... parameters);

    Document download(String path, Object... parameters);
}
