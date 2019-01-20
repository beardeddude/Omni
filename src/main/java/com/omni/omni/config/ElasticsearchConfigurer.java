package com.omni.omni.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;

@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfigurer {

    private final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConfigurer.class);

    @Value("${elasticsearch.type:internal}")
    private String nodeType;

    @Value("${elasticsearch.host:localhost}")
    private String nodeAddress;

    @Value("${elasticsearch.port:9300}")
    private Integer nodePort;

    @Value("${elasticsearch.cluster:omni}")
    private String clusterName;

    @Value("${elasticsearch.home:./omni/elasticsearch}")
    private String elasticsearchHomeDirectory;

    @Value("${elasticsearch.bind.address:0.0.0.0")
    private String bindAddress;

    @Bean
    public Client createClient() {

        if ("external".equalsIgnoreCase(nodeType)) {
            return buildTransportClient();
        } else {
            return buildFullNode();
        }
    }

    private NodeClient buildFullNode() {

        NodeClient nodeClient = null;
        Settings settings = Settings.builder()
                .put("node.master", true)
                .put("node.data", true)
                .put("network.host", bindAddress)
                .put("path.home", elasticsearchHomeDirectory)
                .build();

        while(true) {
            try {

            } catch (Exception e) {
                LOGGER.error("Unable to init internal elasticsearch node. Sleeping for 5 seconds...", e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    return null;
                }

                if (nodeClient != null) {
                    nodeClient.close();
                }
            }
            break;
        }

        return nodeClient;
    }

    private TransportClient buildTransportClient() {

        PreBuiltTransportClient esClient = null;
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("network.host", bindAddress)
                .build();

        while(true) {
            try {
                esClient = new PreBuiltTransportClient(settings);
                esClient.addTransportAddress(new TransportAddress(InetAddress.getByName(nodeAddress), nodePort));
            } catch (Exception e) {
                LOGGER.error("Failed to init elasticsearch connection. Sleeping for 5 seconds...", e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    return null;
                }

                if (esClient != null) {
                    esClient.close();
                }
                continue;
            }
            break;
        }

        return esClient;
    }
}
