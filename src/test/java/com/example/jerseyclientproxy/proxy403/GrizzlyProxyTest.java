package com.example.jerseyclientproxy.proxy403;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;

public class GrizzlyProxyTest extends JerseyClientProxyApplicationTests {
    @Override
    public ClientConfig configureClient(ClientConfig config) {
        config.connectorProvider(new GrizzlyConnectorProvider());
        return config;
    }
}
