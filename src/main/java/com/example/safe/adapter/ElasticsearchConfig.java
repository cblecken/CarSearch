package com.example.safe.adapter;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cblecken
 *
 * Spring configuration object to initialize the elastic search connection
 */
@Configuration
public class ElasticsearchConfig extends AbstractFactoryBean {

	private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchConfig.class);
    @Value("${elasticsearch.cluster.name:safecb}")
    private String clusterName;
    
    @Value("${elasticsearch.host.name:ec2-35-163-214-164.us-west-2.compute.amazonaws.com}")
    private String hostname;
    
    // Needed by ES AMI
    @Value("${elasticsearch.username:user}")
    private String user;
    
    // Needed by ES AMI
    @Value("${elasticsearch.password:Kbx6MnzvJAA2}")
    private String password;
    
    private RestHighLevelClient restClient;

	@Override
	public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
	}

	@Override
	protected Object createInstance() throws Exception {
        try {
        	HttpHost hh = new HttpHost(hostname, 80, "http");
        	final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        	credentialsProvider.setCredentials(AuthScope.ANY,
        	        new UsernamePasswordCredentials(user, password));

        	RestClientBuilder builder = RestClient.builder(hh)
        	        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
        	            @Override
        	            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
        	                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        	            }
        	        }).setPathPrefix("/elasticsearch");

        	restClient = new RestHighLevelClient(builder);
        	LOG.info("Elastic search High level Client created " + restClient.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return restClient;
	}
	
}
