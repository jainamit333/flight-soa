package com.mmt.flights.cache.helper;

import com.mmt.pojo.search.SearchRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amit on 11/5/16.
 */
@FunctionalInterface
public interface KeyGeneratorInterface {

    List<String> KEYS = new ArrayList<>();


    public void initiate();

    default public KeyGeneratorInterface keyAppend(String s) {
        this.KEYS.stream().forEach(a -> {
            a.concat(s);
        });
        return this;
    }

    default public KeyGeneratorInterface extractService(SearchRequest searchRequest) {
        this.KEYS.stream().forEach(a -> {
            a.concat("_").concat(searchRequest.getRequestCore().getAirlines().get(0));
        });
        return this;
    }

    default public KeyGeneratorInterface extractType(SearchRequest searchRequest) {
        this.KEYS.stream().forEach(a -> {
            a.concat("_").concat(searchRequest.getRequestCore().getTripType().name());
        });
        return this;
    }

    default public KeyGeneratorInterface destinationEvaluatorBucket(SearchRequest searchRequest, Boolean isRtDivideKeyReq) {

        if (isRtDivideKeyReq) {
            List<String> tempKeys = new ArrayList<>();
            searchRequest.getRequestCore().getItineraryList().stream().forEach(a -> {
                this.KEYS.stream().forEach(b -> {
                    tempKeys.add(b + "_" + a.getFrom() + "_" + a.getTo());
                });
            });
            this.KEYS.clear();
            this.KEYS.addAll(tempKeys);
        } else {
            this.KEYS.stream().forEach(a -> {
                int i = searchRequest.getRequestCore().getItineraryList().size();
                String from = searchRequest.getRequestCore().getItineraryList().get(0).getFrom();
                String to = searchRequest.getRequestCore().getItineraryList().get(i - 1).getTo();
                a.concat("_").concat(from).concat("_").concat(to);

            });
        }
        return this;
    }

    default public KeyGeneratorInterface paxEvaluatorBucket(SearchRequest searchRequest, Boolean isPaxKeyRequired, String paxKey) {

        if (isPaxKeyRequired) {
            this.KEYS.stream().forEach(a -> {
                a.concat("_").concat(paxKey);
            });
        }
        return this;
    }

    default public KeyGeneratorInterface isNearBySearchActivated(Boolean isNearByActivated, String key){

        if(isNearByActivated)
            this.keyAppend(key);

        return this;
    }

    default public List<String> generate() {
        return this.KEYS;
    }

}
