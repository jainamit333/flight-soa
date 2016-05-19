package com.mmt.flights.cache.impl;

import com.mmt.flights.cache.service.CacheManagmentService;
import com.mmt.pojo.search.AggregatedSearchResponse;
import com.mmt.pojo.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.List;

/**
 * Created by amit on 19/5/16.
 */
@Component
public class CacheManagementServiceImpl implements CacheManagmentService<SearchRequest,AggregatedSearchResponse> {


    @Autowired
    private CacheManagementExecutor cacheManagementExecutor;


    public void updateCache(final SearchRequest searchRequest, final AggregatedSearchResponse searchResponse) {
        Observable.from(searchRequest.getRequestCore().getAirlines()).flatMap(s -> {
            cacheManagementExecutor.startCacheUpdate(searchRequest,searchResponse);
            return null;
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public DeferredResult<AggregatedSearchResponse> getDataFromCache(SearchRequest searchRequest) {

        return cacheManagementExecutor.getDataFromCache(searchRequest);
    }


    public List<AggregatedSearchResponse> getDataByServiceName(String value) {
        return null;
    }

    public List<AggregatedSearchResponse> getDataByTripTypeName(String value) {
        return null;
    }

    public List<AggregatedSearchResponse> getDataByUpdatedAfter(Date value) {
        return null;
    }

    public List<AggregatedSearchResponse> getDataByUpdatedBefore(Date value) {
        return null;
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
