package com.plcpipeline.ingestion.configs;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.plcpipeline.ingestion.hik.config.HikCentralProperties;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

// @Configuration
// public class HikWebClientConfig {

//     @Bean(name = "hikWebClient")
//     public WebClient webClient(WebClient.Builder builder) {

//         return builder
//                 .build();
//     }
// }

// @Configuration
// public class WebClientConfig {

//     @Bean(name = "hikWebClient")
//     public WebClient webClient(WebClient.Builder builder) {
//         // Increase memory limit for large responses if needed
//         ExchangeStrategies strategies = ExchangeStrategies.builder()
//                 .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
//                 .build();

//         return builder
//                 .exchangeStrategies(strategies)
//                 .build();
//     }
// }

@Configuration
public class HikWebClientConfig {

    @Bean(name = "hikWebClient")
    public WebClient hikWebClient(WebClient.Builder builder,HikCentralProperties props) throws SSLException {
        // Increase memory limit and create an "insecure" HTTP client for dev
        // ExchangeStrategies strategies = ExchangeStrategies.builder()
        //         .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
        //         .build();

        // Reactor Netty HttpClient with InsecureTrustManagerFactory (dev only)
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> {
                    try {
                        spec.sslContext(
                            SslContextBuilder
                            .forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                        );
                    } catch (SSLException e) {
                        throw new RuntimeException("Failed to build SSL context", e);
                    }
                });

        return builder
                .baseUrl(props.getHost())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // .exchangeStrategies(strategies)
                .build();
    }
}
