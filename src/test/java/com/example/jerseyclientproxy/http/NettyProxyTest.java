package com.example.jerseyclientproxy.http;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.netty.connector.NettyConnectorProvider;

public class NettyProxyTest extends JerseyClientProxyApplicationTests {
    @Override
    public ClientConfig configureClient(ClientConfig config) {
        config.connectorProvider(new NettyConnectorProvider());
        return config;
    }
}
