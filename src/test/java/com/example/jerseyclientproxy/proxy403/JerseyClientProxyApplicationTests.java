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

    private ClientConfig config;

    @Before
    public void setup() {
        config = configureClient(new ClientConfig());
        config.property(ClientProperties.PROXY_URI, "http://localhost:"+SERVER_PORT);
    }

    private void getClient(ClientConfig config) {
        client = ClientBuilder.newClient(config);
    }

    @After
    public void cleanup() {
        client.close();
    }

    public abstract ClientConfig configureClient(ClientConfig config);

    @Test(timeout = 10000)
    public void ProxyRespondsWithForbiddenHttps() {
        getClient(config);
        ProxyRespondsWithForbidden("https://localhost:" + 1337, constructHttpsRequest());
    }

    @Test(timeout = 10000)
    public void ProxyRespondsWithForbiddenHttp() {
        getClient(config);
        ProxyRespondsWithForbidden("http://localhost:" + 1337, constructHttpRequest());
    }

    @Test(timeout = 10000)
    public void ProxyRespondsWithForbiddenHttpsAndAuth() {
        config.property(ClientProperties.PROXY_USERNAME, "user");
        config.property(ClientProperties.PROXY_PASSWORD, "pass");
        getClient(config);
        ProxyRespondsWithForbidden("https://localhost:" + 1337, constructHttpsRequest());
    }

    @Test(timeout = 10000)
    public void ProxyRespondsWithForbiddenHttpAndAuth() {
        config.property(ClientProperties.PROXY_USERNAME, "user");
        config.property(ClientProperties.PROXY_PASSWORD, "pass");
        getClient(config);
        ProxyRespondsWithForbidden("http://localhost:" + 1337, constructHttpRequest());
    }

    public void ProxyRespondsWithForbidden(String target, HttpRequest request) {
        HttpResponse response = HttpResponse.response()
                .withHeaders(new Header("Content-Type", "application/json"))
                .withBody("{\"error\":\"Forbidden\"}")
                .withStatusCode(403);

        mockServerClient.when(request).respond(response);

        try (Response response1 = client.target(target).request(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertThat(response1.getStatus(), is(403));
        }
    }

    protected HttpRequest constructHttpsRequest() {
        return HttpRequest.request().withMethod("CONNECT");
    }

    protected HttpRequest constructHttpRequest() {
        return HttpRequest.request().withMethod("GET");
    }

}
