package org.khatri.sto.qna.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author Ankit Khatri
 */
@Slf4j
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                if(!response.getStatusCode().is2xxSuccessful()){
                    log.error("[RestClientConfig] REST API call error: {}, statusText:{}", response.getStatusCode(), response.getStatusText());
                    return true;
                } else{
                    log.info("[RestClientConfig] REST API call successful");
                }
                return false;
            }
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });
        return restTemplate;
    }

}