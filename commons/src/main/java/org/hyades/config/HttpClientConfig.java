package org.hyades.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Optional;

@ConfigMapping(prefix = "client.http.config")
public interface HttpClientConfig {
    Optional<String> proxyAddress();

    Optional<Integer> proxyPort();

    Optional<String> proxyUsername();

    Optional<String> proxyPassword();

    Optional<String> noProxy();

    @WithDefault("30")
    int proxyTimeoutConnection();

    @WithDefault("60")
    int proxyTimeoutPool();

    @WithDefault("30")
    int proxyTimeoutSocket();

    @WithDefault("200")
    int maxTotalConnections();

    @WithDefault("20")
    int maxDefaultConnectionsPerRoute();

    @WithDefault("true")
    boolean isConnectionManagerShared();

}
