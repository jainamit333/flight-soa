package com.mmt.flights.cache.core.impl;

import com.mmt.flights.cache.core.impl.couch.util.NaiveAuditorAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.CustomConversions;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.List;

/**
 * Created by amit on 19/5/16.
 */
@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = "com.mmt.flights.cache.core.impl")
@EnableCouchbaseRepositories(basePackages = "com.mmt.flights.cache.core.impl.couch.repository")
public class CouchBaseConfiguration extends AbstractCouchbaseConfiguration {


    @Value("${couch.bucket}")
    private  String bucketName;

    @Value("${couch.password}")
    private  String bucketPassword;

    @Value("${couch.clusters}")
    private List<String> clusters;

    @Override
    protected List<String> getBootstrapHosts() {
        return clusters;
    }

    @Override
    protected String getBucketName() {
        return bucketName;
    }

    @Override
    protected String getBucketPassword() {
        return bucketPassword;
    }

    @Bean
    public NaiveAuditorAware testAuditorAware() {
        return new NaiveAuditorAware();
    }

    @Override
    @Bean(name = "couchbaseConversions")
    public CustomConversions customConversions() {
        return new CustomConversions(Collections.emptyList());
    }


}
