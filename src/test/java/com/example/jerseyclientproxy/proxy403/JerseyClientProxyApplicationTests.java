package com.example.jerseyclientproxy.proxy403;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

public abstract class JerseyClientProxyApplicationTests {


    static final int SERVER_PORT = 1080;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, SERVER_PORT);

    public MockServerClient mockServerClient;

    public Client client;

    @Before
    public void setup() {
        ClientConfig config = configureClient(new ClientConfig());
        config.property(ClientProperties.PROXY_URI, "http://localhost:"+SERVER_PORT);
        client = ClientBuilder.newClient(config);
    }

    @After
    public void cleanup() {
        client.close();
    }

    public abstract ClientConfig configureClient(ClientConfig config);

    @Test(timeout = 30000)
    public void ProxyRespondsWithForbiddenHttps() {
        ProxyRespondsWithForbidden("https://localhost:" + 1337);
    }

    @Test(timeout = 30000)
    public void ProxyRespondsWithForbiddenHttp() {
        ProxyRespondsWithForbidden("http://localhost:" + 1337);
    }

    public void ProxyRespondsWithForbidden(String target) {
        HttpRequest getRequest = HttpRequest.request().withMethod("GET");
        HttpRequest connectRequest = HttpRequest.request().withMethod("CONNECT");
        HttpResponse response = HttpResponse.response()
                .withHeaders(new Header("Content-Type", "application/json"))
                .withBody("{\"error\":\"Forbidden\"}")
                .withStatusCode(403);

        mockServerClient.when(getRequest).respond(response);
        mockServerClient.when(connectRequest).respond(response);

        try (Response response1 = client.target(target).request(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertThat(response1.getStatus(), is(403));
        }
    }

}
