package com.mmt.flights.cache.core.impl.couch.impl;

import com.mmt.flight.cache.core.service.CacheService;
import com.mmt.flights.cache.core.impl.couch.entity.FlightSearchResponse;
import com.mmt.flights.cache.core.impl.couch.repository.FlightSearchResponseRepository;
import com.mmt.pojo.search.SearchResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by amit on 19/5/16.
 */
@Component
@Qualifier("couchCacheService")
public class CacheServiceCouchImpl  implements CacheService<SearchResponseResult> {

    @Autowired
    FlightSearchResponseRepository flightSearchResponseRepository;


    public SearchResponseResult updateCache(String key,SearchResponseResult m ) {
        FlightSearchResponse flightSearchResponse = new FlightSearchResponse();
        flightSearchResponse.setId(key);
        flightSearchResponse.setSearchResponse(m);
        //// TODO: 11/5/16 set  and trip type
        flightSearchResponse.setService(m.getSearchResponse().getServiceName());
        flightSearchResponse.setTripType("");
        flightSearchResponse.setUpdatedAt(new Date());
        flightSearchResponseRepository.save(flightSearchResponse);
        return m;
    }

    public SearchResponseResult getDataFromCache(String key) {
        return flightSearchResponseRepository.findOne(key)!= null && flightSearchResponseRepository.findOne(key).getArchived() ?
                flightSearchResponseRepository.findOne(key).getSearchResponse() : null;
    }

    public List<SearchResponseResult> getDataByServiceName(String value) {
        return flightSearchResponseRepository.findByServiceIgnoreCase(value).parallelStream().map(a ->{
            return a.getSearchResponse();
        }).collect(Collectors.toList());
    }

    public List<SearchResponseResult> getDataByTripTypeName(String value) {

        return flightSearchResponseRepository.findByTripTypeIgnoreCase(value).parallelStream().map(a ->{
            return a.getSearchResponse();
        }).collect(Collectors.toList());
    }

    public List<SearchResponseResult> getDataByUpdatedAfter(Date value) {

        return flightSearchResponseRepository.findByUpdatedAtIsBefore(value).parallelStream().map(a ->{
            return a.getSearchResponse();
        }).collect(Collectors.toList());

    }

    public List<SearchResponseResult> getDataByUpdatedBefore(Date value) {
        return flightSearchResponseRepository.findByUpdatedAtIsAfter(value).parallelStream().map(a ->{
            return a.getSearchResponse();
        }).collect(Collectors.toList());

    }

    public Boolean archiveRecordByServiceName(String value) {
        return null;
    }

    public Boolean archiveRecordByTripTypeName(String value) {
        return null;
    }

    public Boolean archiveRecordByUpdatedAfter(Date value) {
        return null;
    }

    public Boolean archiveRecordByUdpatedBefore(Date value) {
        return null;
    }


}
