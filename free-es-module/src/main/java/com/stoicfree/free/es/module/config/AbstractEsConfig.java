package com.stoicfree.free.es.module.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author zengzhifei
 * @date 2023/2/13 12:28
 */
public abstract class AbstractEsConfig {
    public RestHighLevelClient buildRestHighLevelClient(EsClientConfig config) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(config.getUser(),
                config.getPassword()));
        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost(config.getHost(), config.getPort(),
                HttpHost.DEFAULT_SCHEME_NAME));
        clientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            // 线程设置
            httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(10).build());
            return httpClientBuilder;
        });
        return new RestHighLevelClient(clientBuilder);
    }
}
