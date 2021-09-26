package edu.hue.community.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author 47552
 * @date 2021/09/24
 * elasticsearch配置类
 */
@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private Integer port;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        //RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
        //return new RestHighLevelClient(builder);

        //final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        //        .connectedTo(host + ":" + port)
        //        .build();
        //return RestClients.create(clientConfiguration).rest();

        return RestClients.create(ClientConfiguration.localhost()).rest();
    }

}
