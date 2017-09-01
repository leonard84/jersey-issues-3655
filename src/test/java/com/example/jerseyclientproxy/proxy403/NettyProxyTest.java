package com.example.jerseyclientproxy.proxy403;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.netty.connector.NettyConnectorProvider;
import org.mockserver.model.HttpRequest;

public class NettyProxyTest extends JerseyClientProxyApplicationTests {
    @Override
    public ClientConfig configureClient(ClientConfig config) {
        config.connectorProvider(new NettyConnectorProvider());
        return config;
    }

    @Override
    protected HttpRequest constructHttpRequest() {
        return constructHttpsRequest();
    }
}
