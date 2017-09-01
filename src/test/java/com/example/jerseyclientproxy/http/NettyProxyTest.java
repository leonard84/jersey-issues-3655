package com.example.jerseyclientproxy.http;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

public class NettyProxyTest extends JerseyClientProxyApplicationTests {
    @Override
    public ClientConfig configureClient(ClientConfig config) {
        config.connectorProvider(new ApacheConnectorProvider());
        return config;
    }
}
