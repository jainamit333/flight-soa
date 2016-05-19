package com.mmt.flights.cache.core.impl.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.Date;
import java.util.List;

/**
 * Created by amit on 19/5/16.
 */
public interface FlightSearchResponseGenericRepository<M> extends CrudRepository<M,String> {



    List<M> findByServiceIgnoreCase(String service);

    List<M> findByTripTypeIgnoreCase(String tripType);

    List<M> findByUpdatedAtIsAfter(Date date);

    List<M> findByUpdatedAtIsBefore(Date date);
}

