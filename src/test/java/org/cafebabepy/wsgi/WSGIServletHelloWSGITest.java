package org.cafebabepy.wsgi;

import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WSGIServletHelloWSGITest {
    private Client client = ClientBuilder.newClient();

    @Test
    void helloWSGI() {
        String expected = "Hello WSGI!!";
        WebTarget target = client.target("http://localhost:8765")
                .path("/hello-wsgi");

        String actual = target.request().get(String.class);

        assertEquals(actual, expected);
    }
}
