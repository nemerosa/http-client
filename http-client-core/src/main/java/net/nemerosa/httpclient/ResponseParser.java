package net.nemerosa.httpclient;

import java.io.IOException;

@FunctionalInterface
public interface ResponseParser<T> {

    T parse(String content) throws IOException;

}
