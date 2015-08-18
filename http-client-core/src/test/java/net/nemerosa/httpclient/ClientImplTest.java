package net.nemerosa.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ClientImplTest {

    @Test
    public void encoding_for_spaces() throws MalformedURLException {
        URL url = new URL("http://host:443/context");
        ClientImpl client = new ClientImpl(
                url,
                new HttpHost("host", 443),
                () -> mock(CloseableHttpClient.class),
                mock(HttpClientContext.class),
                System.out::println
        );
        assertEquals(
                "http://host:443/context/Path/%20with%20spaces",
                client.getUrl("/Path/ with spaces")
        );
    }

    @Test
    public void encoding_no_spaces() throws MalformedURLException {
        URL url = new URL("http://host:443/context");
        ClientImpl client = new ClientImpl(
                url,
                new HttpHost("host", 443),
                () -> mock(CloseableHttpClient.class),
                mock(HttpClientContext.class),
                System.out::println
        );
        assertEquals(
                "http://host:443/context/Path/With/No/Spaces",
                client.getUrl("/Path/With/No/Spaces")
        );
    }

}
