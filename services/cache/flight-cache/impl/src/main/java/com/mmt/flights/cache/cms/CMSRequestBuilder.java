package com.mmt.flights.cache.cms;

import com.mmt.pojo.search.SearchRequest;
import com.mmt.pojo.search.SearchRequestCore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amit on 19/5/16.
 */
public class CMSRequestBuilder {


    @Autowired
    private CMSEngineServiceHelper cmsEngineServiceHelper;


    public Map<String,Map<String,String>> getCMSCredDetails(Map<String,String> credIDMap){
        Map<String,Map<String,String>> cmsDetailsMap = cmsEngineServiceHelper.getCredentialMap(credIDMap);
        return cmsDetailsMap;
    }

    public Map<String,String> getCMSCredRQMap(SearchRequest flightSearchRequest){
        Map<String,List<String>> routeMap = getRouteMap(flightSearchRequest);
        Map<String, HashMap<String, String>> routeCredsMap = cmsEngineServiceHelper.getCredentialIDs(routeMap,flightSearchRequest);
        Map<String,String> officeIdsMap = getCredentialIDMap(routeCredsMap);
        return officeIdsMap;
    }

    public Map<String,List<String>> getRouteMap(SearchRequest flightSearchRequest){
        SearchRequestCore flightSearchRequestCore = flightSearchRequest.getRequestCore();
        Map<String,List<String>> routeMap = new HashMap<>();
        String from = flightSearchRequestCore.getItineraryList().get(0).getFrom();
        String to = flightSearchRequestCore.getItineraryList().get(0).getTo();
        if(flightSearchRequestCore.getTripType()== SearchRequestCore.TripType.MC)
            to = flightSearchRequestCore.getItineraryList().get(flightSearchRequestCore.getItineraryList().size()-1).getTo();
        routeMap.put(from+"-"+to, flightSearchRequestCore.getAirlines());
        return routeMap;
    }

    public Map<String,String> getCredentialIDMap(Map<String, HashMap<String, String>> routeCredsMap){
        Map<String,String> officeIdsMap = new HashMap<String, String>();
        for(HashMap<String, String> val: routeCredsMap.values()){
            officeIdsMap.putAll(val);
        }
        return officeIdsMap;
    }

}
