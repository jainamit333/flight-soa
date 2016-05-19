package com.mmt.flights.cache.helper;


import com.mmt.flights.cache.cms.CMSEngineService;
import com.mmt.pojo.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by amit on 10/5/16.
 */

@Component
public class KeyGenerator {

    @Autowired
    @Qualifier("searhcCmsService")
    private CMSEngineService cmsService;


    public List<String> generateKeys(SearchRequest searchRequest,Map<String,String> requestConfig){

        return keyGeneratorExecutor(searchRequest,requestConfig, new KeyGeneratorInterface() {
            @Override
            public void initiate() {
                this.KEYS.add("");
            }
        });
    }
    private List<String> keyGeneratorExecutor(SearchRequest searchRequest,Map<String,String> requestConfig, KeyGeneratorInterface key){

        //// FIXME: 17/5/16 string should come from Prop Man
        String keyEvaluater = "FLIGHTS";
        String nearbyString = "NEARBY";

        return key.keyAppend(keyEvaluater).extractService(searchRequest).extractType(searchRequest).destinationEvaluatorBucket(searchRequest,isRtDivideKeyReq(requestConfig)).
                paxEvaluatorBucket(searchRequest,isPaxEvaluationOverriden(requestConfig),paxEvalucationKey(requestConfig)).isNearBySearchActivated(isNearBySearchActivated(requestConfig),nearbyString).
                keyAppend(getCredentialId(requestConfig)).generate();
    }

    private boolean isPaxEvaluationOverriden(Map<String,String> requestConfig) {
        //// FIXME: 17/5/16 string should come from Prop Man
            return requestConfig.get("").equalsIgnoreCase("TRUE") ? true : false;
    }

    private String paxEvalucationKey(Map<String,String> requestConfig ) {
        //// FIXME: 17/5/16 string should come from Prop Man
            return requestConfig.get("");
    }

    private String getCredentialId(Map<String,String> requestConfig) {
        //// FIXME: 17/5/16 string should come from Prop Man
            return requestConfig.get("");
    }

    private boolean isNearBySearchActivated(Map<String,String> requestConfig) {
        //// FIXME: 17/5/16 string should come from Prop Man
        return requestConfig.get("").equalsIgnoreCase("TRUE") ? true : false;
    }

    private boolean isRtDivideKeyReq(Map<String,String> requestConfig) {
        //// FIXME: 17/5/16 string should come from Prop Man
        return requestConfig.get("").equalsIgnoreCase("TRUE") ? true : false;
    }


}
