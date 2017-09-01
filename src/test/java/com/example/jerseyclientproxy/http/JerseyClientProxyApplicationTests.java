package com.example.jerseyclientproxy.http;

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
import org.mockserver.client.proxy.ProxyClient;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.junit.ProxyRule;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

public abstract class JerseyClientProxyApplicationTests {

    static final int PROXY_PORT = 13010;

    static final int SERVER_PORT = 1080;

    @Rule
    public ProxyRule proxyRule = new ProxyRule(PROXY_PORT, this);

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, SERVER_PORT);

    public ProxyClient proxy;

    public MockServerClient mockServerClient;

    public Client client;

    private ClientConfig config;

    @Before
    public void setup() {
        config = configureClient(new ClientConfig());
        config.property(ClientProperties.PROXY_URI, "http://localhost:"+PROXY_PORT);
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
    public void SimpleProxy() {
        getClient(config);
        performRequest();
    }

    @Test(timeout = 10000)
    public void SimpleProxyWithAuth() {
        config.property(ClientProperties.PROXY_USERNAME, "user");
        config.property(ClientProperties.PROXY_PASSWORD, "pass");
        getClient(config);
        performRequest();
    }

    private void performRequest() {
        HttpRequest request = HttpRequest.request().withMethod("GET")
                .withHeaders(new Header("Accept", MediaType.APPLICATION_JSON));
        HttpResponse response = HttpResponse.response()
                .withHeaders(new Header("Content-Type", "application/json"))
                .withBody("{\"hello\":\"World\"}")
                .withStatusCode(200);

        mockServerClient.when(request).respond(response);

        Response response1 = client.target("http://localhost:" + SERVER_PORT).request(MediaType.APPLICATION_JSON_TYPE).get();

        assertThat(response1.getStatus(), is(200));
        response1.close();

        proxy.verify(HttpRequest.request(), VerificationTimes.exactly(1));
    }

}
