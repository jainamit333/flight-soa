package com.mmt.flights.cache.core.impl.couch.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.mmt.pojo.search.SearchResponseResult;

/**
 * Created by amit on 19/5/16.
 */
public class FlightSearchResponse extends MetaCache {

    @Field
    private SearchResponseResult searchResponse;

    public SearchResponseResult getSearchResponse() {
        return searchResponse;
    }

    public void setSearchResponse(SearchResponseResult searchResponse) {
        this.searchResponse = searchResponse;
    }
}
