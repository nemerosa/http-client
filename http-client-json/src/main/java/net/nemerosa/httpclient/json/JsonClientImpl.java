package net.nemerosa.httpclient.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.httpclient.Client;
import net.nemerosa.httpclient.Document;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

public class JsonClientImpl implements JsonClient {

    private final Client httpClient;
    private final ObjectMapper objectMapper;

    public JsonClientImpl(Client httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public JsonClientImpl(Client httpClient) {
        this(httpClient, new ObjectMapper());
    }

    @Override
    public JsonNode get(String path, Object... parameters) {
        return httpClient.get(this::toJson, path, parameters);
    }

    @Override
    public JsonNode delete(String path, Object... parameters) {
        return httpClient.delete(this::toJson, path, parameters);
    }

    @Override
    public JsonNode post(Object data, String path, Object... parameters) {
        try {
            return httpClient.post(
                    this::toJson,
                    new StringEntity(
                            objectMapper.writeValueAsString(data),
                            ContentType.create("application/json", "UTF-8")
                    ),
                    path,
                    parameters
            );
        } catch (JsonProcessingException e) {
            throw new JsonClientMappingException(e);
        }
    }

    @Override
    public JsonNode put(Object data, String path, Object... parameters) {
        try {
            return httpClient.put(
                    this::toJson,
                    new StringEntity(
                            objectMapper.writeValueAsString(data),
                            ContentType.create("application/json", "UTF-8")
                    ),
                    path,
                    parameters
            );
        } catch (JsonProcessingException e) {
            throw new JsonClientMappingException(e);
        }
    }

    @Override
    public JsonNode upload(String name, Document o, String fileName, String path, Object... parameters) {
        return httpClient.upload(this::toJson, name, o, fileName, path, parameters);
    }

    @Override
    public Document download(String path, Object... parameters) {
        return httpClient.download(path, parameters);
    }

    @Override
    public JsonNode toNode(Object data) {
        return objectMapper.valueToTree(data);
    }

    private JsonNode toJson(String content) {
        if (StringUtils.isBlank(content)) {
            return new ObjectNode(objectMapper.getNodeFactory());
        } else {
            JsonNode tree;
            try {
                tree = objectMapper.readTree(content);
            } catch (IOException e) {
                throw new JsonClientParsingException(e);
            }
            return tree;
        }
    }
}
