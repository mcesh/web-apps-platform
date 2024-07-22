package za.co.web_app_platform.app_ws.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class BucketConfig {

    public static final String SERVICE_ENDPOINT = "ams3.digitaloceanspaces.com";
    public static final String SIGNING_REGION = "ams3";
    private static String ACCESS_KEY = "";
    private static String SECRET_KEY = "";

    @Bean
    public AmazonS3 s3Client() {

        AWSCredentialsProvider awscp = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        );

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(awscp)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(SERVICE_ENDPOINT, SIGNING_REGION)
                )
                .build();
    }
}
