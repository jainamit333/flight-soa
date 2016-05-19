package com.mmt.flights.cache.impl;

import com.mmt.flight.cache.core.service.CacheService;
import com.mmt.flights.cache.cms.CMSEngineServiceHelper;
import com.mmt.flights.cache.cms.CMSRequestBuilder;
import com.mmt.flights.cache.helper.KeyGenerator;
import com.mmt.flights.cache.util.CacheResponseEnhancer;
import com.mmt.pojo.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amit on 19/5/16.
 */
@Component
public class CacheManagementExecutor {


    @Autowired
    @Qualifier("couchCacheService")
    private CacheService cacheService;

    @Autowired
    private CacheResponseEnhancer cacheResponseEnhancer;

    @Autowired
    private CMSEngineServiceHelper cmsEngineServiceHelper;

    @Autowired
    private CMSRequestBuilder cmsRequestBuilder;

    @Autowired
    private KeyGenerator keyGenerator;


    public void startCacheUpdate(SearchRequest searchRequest, AggregatedSearchResponse searchResponse) {

        //// TODO: 17/5/16 check if have to move to RX
        Map<String,String> cmsIdsMap = cmsRequestBuilder.getCMSCredRQMap(searchRequest);
        Map<String,Map<String,String>> airlineInfoMap = cmsRequestBuilder.getCMSCredDetails(cmsIdsMap);
        searchRequest.getRequestCore().getAirlines().parallelStream().forEach(a->{
            SearchResponseResult extractedSearchResponse = extractResponse(a, searchResponse);
            List<String> keys = keyGenerator.generateKeys(searchRequest,airlineInfoMap.get(a));
            enhanceResponseAndSaveCache(extractedSearchResponse,keys,a);
        });
    }

    public DeferredResult<AggregatedSearchResponse> getDataFromCache(SearchRequest searchRequest) {

        DeferredResult<AggregatedSearchResponse> defferedResult = new DeferredResult<>();
        SearchResponseResultHandlerAsync searchResultHandler=new SearchResponseResultHandlerAsync(defferedResult,searchRequest.logKey());

        Map<String,String> cmsIdsMap = cmsRequestBuilder.getCMSCredRQMap(searchRequest);
        Map<String,Map<String,String>> airlineInfoMap = cmsRequestBuilder.getCMSCredDetails(cmsIdsMap);
        Observable<SearchResponseResult> respObservable = executeSearch(searchRequest,airlineInfoMap);
        respObservable.subscribe(searchResultHandler);
        return defferedResult;
    }

    private Observable<SearchResponseResult> executeSearch(SearchRequest searchRequest, Map<String, Map<String, String>> airlineInfoMap) {
        return Observable.from(searchRequest.getRequestCore().getAirlines()).flatMap(s->{
            List<String> keys = keyGenerator.generateKeys(searchRequest,airlineInfoMap.get(s));
            List<SearchResponseResult> searchResponses = new ArrayList<>();
            keys.stream().forEach(a->{
                searchResponses.add((SearchResponseResult) cacheService.getDataFromCache(a));
            });
            SearchResponseResult searchResponse1 = cacheResponseEnhancer.enhanceSearchResponseAfterCached(keys.size(), searchResponses);
            return Observable.just(searchResponse1);
        }).subscribeOn(Schedulers.io());
    }



    public List<SearchResponse> getDataByServiceName(String serviceName) {
        return cacheService.getDataByServiceName(serviceName);
    }


    private void enhanceResponseAndSaveCache(SearchResponseResult searchResponse,List<String> keys,String serviceName) {
        //todo in dom RT , we have to further remove the unwanted data   from search response
        if(keys.size()==1){
            keys.stream().forEach(a->{
                cacheService.updateCache(a, searchResponse);
            });
        }else{
            keys.stream().forEach(a->{
                SearchResponseResult dividedSearchResponse = divideSearchResponse(searchResponse,a,serviceName);
                cacheService.updateCache(a, dividedSearchResponse);
            });
        }


    }

    private SearchResponseResult divideSearchResponse(SearchResponseResult searchResponse, String a,String serviceName) {
        List<Journey> journeys = new ArrayList<>();
        final String[] key = {null};
        SearchResponseResult searchResponseResult = new SearchResponseResult();
        searchResponseResult.setError(searchResponse.getError());
        searchResponse.getSearchResponse().getJourneys().entrySet().stream().forEach( b ->{
            if(a.contains(b.getKey())){
                key[0] = b.getKey();
                journeys.addAll(searchResponse.getSearchResponse().getJourneys().get(b.getKey()));
            }
        });
        if(!journeys.isEmpty()){
            JourneyDetails journeyDetails = new JourneyDetails();
            searchResponseResult.getSearchResponse().getJourneys().put(key[0],journeys);
            searchResponseResult.getSearchResponse().addJourneyDetail(serviceName,journeyDetails);

            final int[] fareCounter = {0};
            final int[] mnrCounter = {0};
            final int[] fareMapperCounter = {0};
            final int[] segmentProdInfoCounter = {0};

            journeys.stream().forEach(c->{

                int fareIndex = c.getFareIndex().get(0);
                int mnrIndex = c.getMinFareRuleIndex();
                Fare fare = searchResponse.getSearchResponse().getJourneyDetails(serviceName).getFareList().get(fareIndex);
                journeyDetails.getFareList().add(fare);
                journeyDetails.getBaggageList().add(searchResponse.getSearchResponse().getJourneyDetails(serviceName).getBaggageList().get(mnrIndex));
                c.getFareIndex().set(fareCounter[0]++,fareIndex);
                c.setMinFareRuleIndex(mnrCounter[0]++);

                fare.getPaxTypeFareMap().entrySet().stream().forEach(d->{
                    fare.getPaxTypeFareMap().get(d.getKey()).stream().forEach(e->{
                        int fareBreakupIndex = e.getIndexOfFareBreakupMap();
                        journeyDetails.getFareBreakUps().add(searchResponse.getSearchResponse().getJourneyDetails(serviceName).getFareBreakUps().get(fareBreakupIndex));
                        e.setIndexOfFareBreakupMap(fareMapperCounter[0]++);
                        e.getIndexOfSegmentProductInfo().stream().forEach(f->{
                            journeyDetails.getSegmentProductInfo().add(searchResponse.getSearchResponse().getJourneyDetails(serviceName).getSegmentProductInfo().get(f));
                            f = segmentProdInfoCounter[0]++;
                        });
                    });
                });
            });
        }
        return searchResponseResult;
    }

    private SearchResponse accessCacheData(String key) {
        return (SearchResponse) cacheService.getDataFromCache(key);
    }


    // TODO: 11/5/16 this should move to util , as need all this manipulation on response
    private SearchResponseResult extractResponse(final String serviceName, AggregatedSearchResponse searchResponse) {
        try {
            return searchResponse.getStringSearchResponseMap().get(serviceName);

        } catch (Exception e) {
            //todo add log stating issue while seggregating the SearchResponse
            return null;
        }
    }
}
