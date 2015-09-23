package net.nemerosa.httpclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class BaseResponseHandler<T> implements ResponseHandler<T> {

    private final EntityParser<T> entityParser;

    public BaseResponseHandler(EntityParser<T> entityParser) {
        this.entityParser = entityParser;
    }

    @Override
    public T handleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity) throws ParseException, IOException {
        boolean content = checkCode(request, response);
        if (content) {
            return entityParser.parse(entity);
        } else {
            return null;
        }
    }

    protected boolean checkCode(HttpRequestBase request, HttpResponse response) throws IOException {
        return basicCheckCode(request, response);
    }

    public static boolean basicCheckCode(HttpRequestBase request, HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK ||
                statusCode == HttpStatus.SC_CREATED ||
                statusCode == HttpStatus.SC_ACCEPTED) {
            return true;
        } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
            throw new ClientValidationException(getMessage(response));
        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new ClientCannotLoginException(request);
        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            throw new ClientForbiddenException(request);
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            throw new ClientNotFoundException(getMessage(response));
        } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
            return false;
        } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            String content = getMessage(response);
            if (StringUtils.isNotBlank(content)) {
                throw new ClientMessageException(content);
            } else {
                // Generic error
                throw new ClientServerException(
                        request,
                        statusCode,
                        response.getStatusLine().getReasonPhrase());
            }
        } else {
            // Generic error
            throw new ClientServerException(
                    request,
                    statusCode,
                    response.getStatusLine().getReasonPhrase());
        }
    }

    public static String getMessage(HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }
}
