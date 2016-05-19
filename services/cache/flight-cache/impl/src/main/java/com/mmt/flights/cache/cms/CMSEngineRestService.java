package com.mmt.flights.cache.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmt.pojo.cms.CMSDetailRequest;
import com.mmt.pojo.cms.CMSDetailResponse;
import com.mmt.pojo.cms.CMSIDRequests;
import com.mmt.pojo.cms.CMSIDResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

/**
 * Created by amit on 17/5/16.
 */
@Component
public class CMSEngineRestService implements ApplicationContextAware {

    private static RestTemplate jsonRestTemplate  ;//= (RestTemplate)CFlightServiceFactory.getBean("jsonRestTemplate");

    private ApplicationContext context;
    private String CMS_REST_CRED_ID_URL;// = "http://flight-cm.mmt.mmt/cms-service/credentialID";//MMTKernal.getKernal().getProperty("cms.rest.cred.id.url");
    private String CMS_REST_CRED_DETAIL_URL;// = "http://flight-cm.mmt.mmt/cms-service/credentialDetail";//MMTKernal.getKernal().getProperty("cms.rest.cred.detail.url");
    private static final Logger logger = LoggerFactory.getLogger(CMSEngineRestService.class);

    @PostConstruct
    public void init(){
        //todo make it from property manager
        Optional<String> credIdOpt = Optional.ofNullable("http://flight-cm.mmt.mmt/cms-service/credentialID");
        Assert.isTrue(credIdOpt.isPresent());
        CMS_REST_CRED_ID_URL = credIdOpt.get();
        Optional<String> credDtlOpt = Optional.ofNullable("//http://flight-cm.mmt.mmt/cms-service/credentialDetail");
        Assert.isTrue(credDtlOpt.isPresent());
        CMS_REST_CRED_DETAIL_URL = credDtlOpt.get();
        jsonRestTemplate=(RestTemplate) context.getBean("jsonRestTemplate");
    }
    public CMSIDResponse getCredentialID(CMSIDRequests request) {
        try {
            CMSIDResponse response = null;
            int timeoutInSeconds = 30;
            long diff = 0;
            SimpleClientHttpRequestFactory rfactory = (SimpleClientHttpRequestFactory)jsonRestTemplate.getRequestFactory();
            rfactory.setReadTimeout(timeoutInSeconds*1000);
            Date preDate = new Date();
            response = getFromService(CMS_REST_CRED_ID_URL, request, CMSIDResponse.class,"CMS getCredentialID");
            Date postDate = new Date();
            diff = postDate.getTime() - preDate.getTime();
            if(logger.isDebugEnabled())
                logger.debug("Time Diff for Cred ID Rest Service in milli seconds "+diff);
            return response;
        } catch (IOException e) {
            logger.error("Error in getting CredentialID",e);
            throw new RuntimeException(e);
        }
    }

    public CMSDetailResponse getCredentialDetail(CMSDetailRequest request) {
        try{
            CMSDetailResponse response = null;
            int timeoutInSeconds = 30;
            long diff = 0;
            SimpleClientHttpRequestFactory rfactory = (SimpleClientHttpRequestFactory)jsonRestTemplate.getRequestFactory();
            rfactory.setReadTimeout(timeoutInSeconds*1000);
            Date preDate = new Date();
            response = getFromService(CMS_REST_CRED_DETAIL_URL,request,CMSDetailResponse.class,"CMS getCredentialDetail");
            Date postDate = new Date();
            diff = postDate.getTime() - preDate.getTime();
            logger.info(new StringBuilder("CMS cred Map for credID=").append(request.getCredID()).append("::").append(response.getPropMap()).toString());
            if(logger.isDebugEnabled()){
                logger.debug("Time Diff for Cred Detail Rest Service in milli seconds "+diff);
            }
            return response;
        }catch(IOException e){
            logger.error("Error in getting CredentialID Detail",e);
            throw new RuntimeException(e);
        }
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context=context;
    }
    /**
     * @param  <Q> type of request object
     * @param  <R> type of response object
     * @param  url service url
     * @param  request object which will be send in the body of request
     * @param  respClass class Object of the expected response object
     * @param  logStringPrefix prefix to be added to log string
     * @return response from service
     * @throws IOException
     */
    private <Q,R> R getFromService(String url,Q request,Class<R> respClass,String logStringPrefix) throws IOException{
        ObjectMapper objectMapper=new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJson;
        requestJson = objectMapper.writeValueAsString(request);
        logger.info(new StringBuilder(logStringPrefix).append(" Request: ").append(requestJson).toString());
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        String responseJson=jsonRestTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        logger.info(new StringBuilder(logStringPrefix).append(" Response: ").append(responseJson).toString());
        return objectMapper.readValue(responseJson, respClass);
    }

}
