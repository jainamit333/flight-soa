package com.mmt.flights.cache.core.impl.couch.repository;

import com.mmt.flights.cache.core.impl.couch.entity.FlightSearchResponse;
import com.mmt.flights.cache.core.impl.repository.FlightSearchResponseGenericRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by amit on 19/5/16.
 */
@Repository
public interface FlightSearchResponseRepository extends FlightSearchResponseGenericRepository<FlightSearchResponse> {
}
