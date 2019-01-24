package com.omni.omni.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;


/**
 * Configures the Elasticsearch connection
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.omni")
public class ElasticsearchConfigurer {

    /*
    TODO: Elasticsearch should really use a read and write alias per index but spring-data has no support
            for it. For MVP we will use what spring-data supports and work directly on indices.
    TODO: _all is generally not needed but spring-data does not support disabling it. Will cause
            indexes to bloat. Leaving it be for MVP.
     */

    private final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConfigurer.class);

    @Value("${elasticsearch.host:localhost}")
    private String nodeAddress;

    @Value("${elasticsearch.port:9300}")
    private Integer nodePort;

    @Value("${elasticsearch.cluster:omni}")
    private String clusterName;


    @Bean(destroyMethod = "close")
    public Client createClient() {

        PreBuiltTransportClient esClient = null;
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .build();

        try {
            LOGGER.info("Connecting to Elasticsearch cluster...");
            esClient = new PreBuiltTransportClient(settings);
            esClient.addTransportAddress(new TransportAddress(InetAddress.getByName(nodeAddress), nodePort));

            // Wait for connection as we need it for everything
            LOGGER.info("Node {} on port {} not connected yet. Waiting for connection...", nodeAddress, nodePort);
            while (esClient.connectedNodes().isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.debug("Interupted while waiting for Elasticsearch connection");
                    // If interrupted give up
                    return null;
                }
            }

            LOGGER.info("Sucsessfully connected to Elasticsearch node at {} on port {}", nodeAddress, nodePort);
        } catch (Exception e) {
            // App is not useful without a database
            LOGGER.error("Failed to connect to Elasticsearch cluster. Shutting down...", e);
            System.exit(-1);
        }


        return esClient;
    }

}
