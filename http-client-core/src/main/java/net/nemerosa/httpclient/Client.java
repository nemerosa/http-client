package net.nemerosa.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URL;
import java.util.Map;

public interface Client {

    URL getUrl();

    <T> T get(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T delete(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T post(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T put(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T upload(ResponseParser<T> responseParser, String name, Document file, String fileName, String path, Object... parameters);

    /**
     * Uploads a document.
     *
     * @param responseParser Parser for the response
     * @param file           Document to upload
     * @param path           Path to upload to
     * @param parameters     Parameters for the path
     * @param <T>            Type of the response
     * @return Parsed response
     */
    <T> T upload(ResponseParser<T> responseParser, Document file, String path, Object... parameters);

    /**
     * With some headers
     */
    Client withHeader(String name, String value);

    /**
     * With some headers
     */
    Client withHeaders(Map<String, String> headers);

    /**
     * Downloads a document
     */
    Document download(String path, Object... parameters);

    /**
     * Low level call which allows to deal with the response content, after the return codes
     * have been processed.
     *
     * @param request        Request to send
     * @param responseParser Parser for the response
     * @param <T>            Type of object to return
     * @return Return
     */
    <T> T request(HttpRequestBase request, final ResponseParser<T> responseParser);

    /**
     * Low level call which allows to deal with the response directly, without any preprocessing.
     *
     * @param request         Request to send
     * @param responseHandler Handler for the response
     * @param <T>             Type of object to return
     * @return Return
     */
    <T> T call(HttpRequestBase request, ResponseHandler<T> responseHandler);

    /**
     * Underlying HTTP client
     */
    CloseableHttpClient getHttpClient();

    /**
     * HTTP host
     */
    HttpHost getHttpHost();

    /**
     * HTTP call context
     */
    HttpClientContext getHttpClientContext();

    /**
     * Gets the logger
     */
    ClientLogger getClientLogger();
}
