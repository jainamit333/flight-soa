package com.mmt.flights.cache.service;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.List;

/**
 * Created by amit on 19/5/16.
 */
public interface CacheManagmentService<T,M> {

    void updateCache(T t,M m);



    DeferredResult<M> getDataFromCache(T t);

    List<M> getDataByServiceName(String value);

    List<M> getDataByTripTypeName(String value);

    List<M> getDataByUpdatedAfter(Date value);

    List<M> getDataByUpdatedBefore(Date value);

    Boolean archiveRecordByServiceName(String value);

    Boolean archiveRecordByTripTypeName(String value);

    Boolean archiveRecordByUpdatedAfter(Date value);

    Boolean archiveRecordByUdpatedBefore(Date value);
}
