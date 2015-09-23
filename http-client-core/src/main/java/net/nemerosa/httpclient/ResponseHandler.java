package net.nemerosa.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

@FunctionalInterface
public interface ResponseHandler<T> {

    T handleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity) throws ParseException, IOException;

}