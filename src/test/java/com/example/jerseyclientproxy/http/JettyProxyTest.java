package com.example.jerseyclientproxy.http;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jetty.connector.JettyConnectorProvider;

public class JettyProxyTest extends JerseyClientProxyApplicationTests {
    @Override
    public ClientConfig configureClient(ClientConfig config) {
        config.connectorProvider(new JettyConnectorProvider());
        return config;
    }
}
