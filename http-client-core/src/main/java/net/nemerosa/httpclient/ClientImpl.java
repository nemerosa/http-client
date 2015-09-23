package net.nemerosa.httpclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.stripEnd;

public class ClientImpl implements Client {

    private final URL url;
    private final HttpHost host;
    private final Supplier<CloseableHttpClient> httpClientSupplier;
    private final HttpClientContext httpContext;
    private final ClientLogger clientLogger;
    private final Map<String, String> headers;

    public ClientImpl(URL url, HttpHost host, Supplier<CloseableHttpClient> httpClientSupplier, HttpClientContext httpContext, ClientLogger clientLogger) {
        this(url, Collections.emptyMap(), host, httpClientSupplier, httpContext, clientLogger);
    }

    public ClientImpl(URL url, Map<String, String> headers, HttpHost host, Supplier<CloseableHttpClient> httpClientSupplier, HttpClientContext httpContext, ClientLogger clientLogger) {
        this.url = url;
        this.headers = headers;
        this.host = host;
        this.httpClientSupplier = httpClientSupplier;
        this.httpContext = httpContext;
        this.clientLogger = clientLogger;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    protected String getUrl(String path, Object... parameters) {
        if (StringUtils.startsWith(path, "http")) {
            return format(path, parameters);
        } else {
            String formattedPath = format(path, parameters);
            if (!formattedPath.startsWith("/")) {
                formattedPath = "/" + formattedPath;
            }
            formattedPath = encode(formattedPath);
            return format(
                    "%s%s",
                    stripEnd(url.toString(), "/"),
                    formattedPath
            );
        }
    }

    @Override
    public <T> T get(ResponseParser<T> responseParser, String path, Object... parameters) {
        return request(new HttpGet(getUrl(path, parameters)), responseParser);
    }

    @Override
    public <T> T delete(ResponseParser<T> responseParser, String path, Object... parameters) {
        return request(new HttpDelete(getUrl(path, parameters)), responseParser);
    }

    @Override
    public <T> T post(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters) {
        HttpPost post = new HttpPost(getUrl(path, parameters));
        if (data != null) {
            post.setEntity(data);
        }
        return request(post, responseParser);
    }

    @Override
    public <T> T put(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters) {
        HttpPut put = new HttpPut(getUrl(path, parameters));
        if (data != null) {
            put.setEntity(data);
        }
        return request(put, responseParser);
    }

    @Override
    public <T> T upload(ResponseParser<T> responseParser, String name, Document document, String fileName, String path, Object... parameters) {
        HttpPost post = new HttpPost(getUrl(path));
        // Sets the content
        post.setEntity(
                MultipartEntityBuilder.create()
                        .addBinaryBody(
                                name,
                                document.getContent(),
                                ContentType.parse(document.getType()),
                                fileName
                        )
                        .build()
        );
        // OK
        return request(post, responseParser);
    }

    @Override
    public <T> T upload(ResponseParser<T> responseParser, Document file, String path, Object... parameters) {
        HttpPost post = new HttpPost(getUrl(path));
        // Binary content
        ByteArrayEntity entity = new ByteArrayEntity(file.getContent());
        entity.setContentType(file.getType());
        // Sets the content
        post.setEntity(entity);
        // OK
        return request(post, responseParser);
    }

    @Override
    public Client withHeader(String name, String value) {
        return withHeaders(Collections.singletonMap(name, value));
    }

    @Override
    public Client withHeaders(Map<String, String> headers) {
        return new ClientImpl(
                this.url,
                headers,
                this.host,
                this.httpClientSupplier,
                this.httpContext,
                this.clientLogger
        );
    }

    @Override
    public Document download(String path, Object... parameters) {
        HttpGet get = new HttpGet(getUrl(path));
        return call(get, (request, response, entity) -> {
            // Gets the content as bytes
            byte[] bytes = EntityUtils.toByteArray(entity);
            if (bytes == null || bytes.length == 0) {
                return Document.EMPTY;
            }
            // OK
            return new Document(
                    entity.getContentType().getValue(),
                    bytes
            );
        });
    }

    @Override
    public <T> T request(HttpRequestBase request, final ResponseParser<T> responseParser) {
        return call(
                request,
                new BaseResponseHandler<>(entity -> {
                    // Gets the content as a string
                    String content = entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
                    // Parses the response
                    return responseParser.parse(content);
                })
        );
    }

    @Override
    public CloseableHttpClient getHttpClient() {
        return httpClientSupplier.get();
    }

    @Override
    public HttpHost getHttpHost() {
        return host;
    }

    @Override
    public HttpClientContext getHttpClientContext() {
        return httpContext;
    }

    @Override
    public ClientLogger getClientLogger() {
        return clientLogger;
    }

    @Override
    public <T> T call(HttpRequestBase request, ResponseHandler<T> responseHandler) {
        clientLogger.trace("[request] " + request);
        // Headers
        headers.forEach(request::setHeader);
        // Executes the call
        try {
            try (CloseableHttpClient http = httpClientSupplier.get()) {
                HttpResponse response = http.execute(host, request, httpContext);
                clientLogger.trace("[response] " + response);
                // Entity response
                HttpEntity entity = response.getEntity();
                try {
                    return responseHandler.handleResponse(request, response, entity);
                } finally {
                    EntityUtils.consume(entity);
                }
            }
        } catch (IOException e) {
            throw new ClientGeneralException(request, e);
        } finally {
            request.releaseConnection();
        }
    }

    private static String encode(String name) {
        return name.replace(" ", "%20");
    }

}
