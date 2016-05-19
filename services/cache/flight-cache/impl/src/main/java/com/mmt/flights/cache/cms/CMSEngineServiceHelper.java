package com.mmt.flights.cache.cms;

import com.mmt.pojo.cms.*;
import com.mmt.pojo.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by amit on 17/5/16.
 */
@Component
public class CMSEngineServiceHelper {

    @Autowired
    private CMSEngineService cmsEngine;





    public Map<String, HashMap<String, String>> getCredentialIDs(Map<String, List<String>> routeAndAirlineMap, SearchRequest searchRequest) {
        String fromCity = "";
        String toCity = "";
        String key = "";
        List<String> airlineCodes = null;
        int noOfRoutes = routeAndAirlineMap.size();
        CMSIDRequests requests = new CMSIDRequests();
        CMSIDRequest[] cmsIDRequest = new CMSIDRequest[noOfRoutes];
        requests.setCmsIDRequest(cmsIDRequest);
        int counter = 0;
        for (Map.Entry<String, List<String>> entry : routeAndAirlineMap.entrySet()) {
            key = entry.getKey();
            String[] routeArray = key.split("-");
            fromCity = routeArray[0];
            toCity = routeArray[1];
            airlineCodes = entry.getValue();

            cmsIDRequest[counter] = new CMSIDRequest();
            cmsIDRequest[counter].setAirlineCodes(airlineCodes);
            if(null == searchRequest.getRequestCore().getLob() || "".equals(searchRequest.getRequestCore().getLob())){
                cmsIDRequest[counter].setClientID("exIndia");
            }else{
                cmsIDRequest[counter].setClientID(searchRequest.getRequestCore().getLob());
            }
            SimpleDateFormat dateFormat_yyyy_MM_dd_T_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            cmsIDRequest[counter].setDepartureDate(dateFormat_yyyy_MM_dd_T_HH_mm_ss.format(searchRequest.getRequestCore().getItineraryList().get(0).getDepDate()));
            cmsIDRequest[counter].setFromCity(fromCity);
            cmsIDRequest[counter].setToCity(toCity);
            cmsIDRequest[counter].setTripType(searchRequest.getRequestCore().getTripType().toString());
            counter++;
        }
        Map<String,HashMap<String,String>> credentialIDMap = new HashMap<String,HashMap<String,String>>();
        CMSIDResponse response = cmsEngine.getCredentialID(requests);
        if(null != response && null != response.getCredentialMap()){
            credentialIDMap = response.getCredentialMap();
        }
        return credentialIDMap;
    }


    public Map<String, Map<String, String>> getCredentialMap(Map<String, String> credIDMap) {
        Map<String,Map<String,String>> airPropMap = new HashMap();
        Set<Map.Entry<String,String>> credIdSet = credIDMap.entrySet();
        Iterator<Map.Entry<String,String>> credIdIt = credIdSet.iterator();
        while(credIdIt.hasNext()){
            Map.Entry<String,String> credEntry = credIdIt.next();
            String airline = credEntry.getKey();
            String credID = credEntry.getValue();
            if(null != credID && !"".equals(credID)){
                try{
                    airPropMap.put(airline, getCMSPropMap(credID));
                }catch(Exception e){
                    StringBuilder sb = new StringBuilder("Error in fetching CMS properties. ");

                }
            }
        }
        return airPropMap;
    }

    private Map<String,String> getCMSPropMap(String credID) {
        CMSDetailRequest cmdRequest = getCMSDetailRequest(credID);
        CMSDetailResponse response = cmsEngine.getCredentialDetail(cmdRequest);
        return response.getPropMap();
    }
    private CMSDetailRequest getCMSDetailRequest(String credID) {
        CMSDetailRequest cmdRequest = new CMSDetailRequest();
        cmdRequest.setCredID(credID);
        return cmdRequest;
    }
}