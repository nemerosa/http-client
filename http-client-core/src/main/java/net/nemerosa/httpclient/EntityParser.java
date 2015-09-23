package net.nemerosa.httpclient;

import org.apache.http.HttpEntity;

import java.io.IOException;

@FunctionalInterface
public interface EntityParser<T> {

    T parse(HttpEntity entity) throws IOException;

}