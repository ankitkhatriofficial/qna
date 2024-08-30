package org.khatri.sto.qna.external;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.khatri.sto.qna.RefType;
import org.khatri.sto.qna.constant.Constants;
import org.khatri.sto.qna.dto.external.TagMappingDto;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ankit Khatri
 */

@Slf4j
@Service
public class TagServiceImpl implements TagService{

    @Autowired private RestTemplate restTemplate;

    @Value("${tag.service.url}")
    private String tagServiceBaseUrl;

    private static HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Constants.USER_ID_ATTR, MDC.get(Constants.USER_ID_ATTR));
        headers.set("AMBASSADOR-REQUEST-ID", MDC.get("AMBASSADOR-REQUEST-ID"));
        return headers;
    }

    private static <T> HttpEntity<T> getHttpEntity(T inputBody){
        return new HttpEntity<>(inputBody, getHeaders());
    }

    @Override
    public void mapTagWithReference(final Long refId, final RefType refType, final List<String> tags) {
        final String url = this.tagServiceBaseUrl.concat("/tags/mapping/").concat(String.valueOf(refId)).concat("?refType=").concat(refType.name());
        log.info("[RestService] Calling tag service for mapping ref:{}", refId);
        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(tags), String.class);
        log.info("[RestService] Response status:{}, bodyDataPresent:{}", response.getStatusCode(), response.hasBody());
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("[RestService] Error response from uri:{}", url);
        }
    }

    @Override
    public List<TagMappingDto> findRefIdByTags(final Object request, final RefType refType){
        final String url = this.tagServiceBaseUrl.concat("/tags/search");
        log.info("[RestService] Calling tag service for mapping ref:{}, request:{}", refType, request);
        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(request), String.class);
        log.info("[RestService] Response status:{}, bodyDataPresent:{}, ", response.getStatusCode(), response.hasBody());
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("[RestService] Error response from uri:{},", url);
        }
        return convertJsonStringInListObject(response.getBody(), TagMappingDto.class);
    }

    public static <T> List<T> convertJsonStringInListObject(String value, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        List<T> Objectlist = new ArrayList<>();
        try {
            Objectlist = objectMapper.readValue(value, type);
        } catch (Exception e) {
            log.error("Error occurs to convert json String into List Object: {}", e);
        }
        return Objectlist;
    }
}
