package com.mmt.flights.cache.util;

import com.mmt.pojo.search.Journey;
import com.mmt.pojo.search.JourneyDetails;
import com.mmt.pojo.search.SearchResponse;
import com.mmt.pojo.search.SearchResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by amit on 19/5/16.
 */
@Component
public class CacheResponseEnhancer {


    @Autowired
    NavitaireRecommendationCrossBuilder navitaireRecommendationCrossBuilder;

    public SearchResponseResult enhanceSearchResponseAfterCached(Integer keyCounts, List<SearchResponseResult> searchResponses){

        SearchResponseResult searchResponseResult = null;
        if(!searchResponses.isEmpty() && searchResponses.size()== 2 && searchResponses.get(0).getError()==null && searchResponses.get(1).getError()==null){

            SearchResponse searchResponseForward = searchResponses.get(0).getSearchResponse();
            SearchResponse searchResponseReturn = searchResponses.get(1).getSearchResponse();
            populateAggregatedSearchResponseResult(searchResponseResult,searchResponseForward,searchResponseReturn);
        }
        return searchResponseResult;
    }

    private void populateAggregatedSearchResponseResult(SearchResponseResult searchResponseResult, SearchResponse searchResponseForward,
                                                        SearchResponse searchResponseReturn) {
        SearchResponse searchResponse = new SearchResponse();
        JourneyDetails journeyDetailsResult = new JourneyDetails();
        searchResponse.getJourneyDetails().put(searchResponseForward.getServiceName(),journeyDetailsResult);
        searchResponse.setServiceName(searchResponseForward.getServiceName());
        populateJourneyDetail(searchResponseForward,journeyDetailsResult,searchResponseForward.getServiceName(),searchResponse);
        populateJourneyDetail(searchResponseReturn,journeyDetailsResult,searchResponseReturn.getServiceName(),searchResponse);
        prepareReturnMap(searchResponse,searchResponseForward.getJourneys(),searchResponseReturn.getJourneys(),searchResponse.getServiceName());
    }

    private void prepareReturnMap(SearchResponse searchResponse, HashMap<String, List<Journey>> journeysForward, HashMap<String, List<Journey>> journeysReturn, String serviceName) {

        List<List<Journey>> journeyList = new ArrayList<>();
        journeysForward.entrySet().stream().forEach(a->{
            journeyList.add(a.getValue());
        });
        journeysReturn.entrySet().stream().forEach(a->{
            journeyList.add(a.getValue());
        });
        navitaireRecommendationCrossBuilder.populateReturnMap(searchResponse,journeyList,serviceName);
    }


    private void populateJourneyDetail(SearchResponse searchResponseForward,JourneyDetails journeyDetailsResult,String serviceName,SearchResponse searchResponse){

        searchResponseForward.getJourneys().entrySet().stream().forEach(a->{
            searchResponse.getJourneys().put(a.getKey(),a.getValue());
            a.getValue().stream().forEach(b->{
                JourneyDetails journeyDetails = searchResponseForward.getJourneyDetails(serviceName);
                final int[] fareCounterToBeChange = {0};
                b.getFareIndex().stream().forEach(c->{
                    journeyDetailsResult.getFareList().add(journeyDetails.getFareList().get(c));
                    b.getFareIndex().set(fareCounterToBeChange[0]++,journeyDetailsResult.getFareList().size()-1);
                    journeyDetails.getFareList().get(c).getPaxTypeFareMap().entrySet().stream().forEach(e->{
                        e.getValue().stream().forEach(f->{
                            journeyDetails.getFareBreakUps().add(journeyDetails.getFareBreakUps().get(f.getIndexOfFareBreakupMap()));
                            f.setIndexOfFareBreakupMap(journeyDetailsResult.getFareBreakUps().size()-1);
                            final int[] fareBreakUpToBeChange = {0};
                            f.getIndexOfSegmentProductInfo().stream().forEach(g->{
                                journeyDetailsResult.getSegmentProductInfo().add(journeyDetails.getSegmentProductInfo().get(g));
                                f.getIndexOfSegmentProductInfo().set(fareBreakUpToBeChange[0]++,journeyDetailsResult.getSegmentProductInfo().size());
                            });
                        });
                    });

                });
            });
        });
    }

    private boolean isGotResultForAllTheKeys(Integer keyCount,Integer searchResponses){
        return keyCount==searchResponses;
    }

    private boolean isAggregationRequired(Integer searchResponsesSize){
        return searchResponsesSize>1;
    }
}
