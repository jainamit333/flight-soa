package com.mmt.flight.cache.core.service;

import java.util.Date;
import java.util.List;

/**
 * Created by amit on 19/5/16.
 */
public interface CacheService<T> {

    T updateCache(String Key , T t);

    T getDataFromCache(String key);

    List<T> getDataByServiceName(String value);

    List<T> getDataByTripTypeName(String value);

    List<T> getDataByUpdatedAfter(Date value);

    List<T> getDataByUpdatedBefore(Date value);

    Boolean archiveRecordByServiceName(String value);

    Boolean archiveRecordByTripTypeName(String value);

    Boolean archiveRecordByUpdatedAfter(Date value);

    Boolean archiveRecordByUdpatedBefore(Date value);

}
