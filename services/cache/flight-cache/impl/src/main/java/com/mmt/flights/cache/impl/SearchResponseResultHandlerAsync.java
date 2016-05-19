package com.mmt.flights.cache.impl;

import com.mmt.pojo.search.AggregatedSearchResponse;
import com.mmt.pojo.search.SearchResponseResult;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Subscriber;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by amit on 19/5/16.
 */
public class SearchResponseResultHandlerAsync extends Subscriber<SearchResponseResult> {

    private final String logKey;

    private final DeferredResult<AggregatedSearchResponse> defResult;
    private final AtomicReference<AggregatedSearchResponse> response;
    private long startTime;

    public SearchResponseResultHandlerAsync(DeferredResult<AggregatedSearchResponse> defResult,String logKey) {
        this.defResult=defResult;
        response=new AtomicReference<AggregatedSearchResponse>(new AggregatedSearchResponse());
        //this.defResult.onTimeout(()->defResult.setResult(response.get()));
        this.logKey=logKey;
    }

    @Override
    public void onStart(){
        super.onStart();
        startTime=System.currentTimeMillis();
    }

    @Override
    public void onNext(SearchResponseResult searchResponseResult) {
        AggregatedSearchResponse dirtySearchResponseResult = new AggregatedSearchResponse(response.get());
        if(searchResponseResult!=null){
            dirtySearchResponseResult.getStringSearchResponseMap().put(searchResponseResult.getSearchResponse().getServiceName(),searchResponseResult);
        }
        response.set(dirtySearchResponseResult);

    }

    @Override
    public void onCompleted() {
        defResult.setResult(response.get());
    }

    @Override
    public void onError(Throwable e) {
        defResult.setErrorResult(e);
        StringBuilder sb = new StringBuilder(logKey).append("Error in SearchResponseResultHandlerAsync");
    }


}

