package com.example.jerseyclientproxy.proxy403;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jdk.connector.JdkConnectorProvider;

public class JdkProxyTest extends JerseyClientProxyApplicationTests {
    @Override
    public ClientConfig configureClient(ClientConfig config) {
        config.connectorProvider(new JdkConnectorProvider());
        return config;
    }
}
