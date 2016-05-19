package com.mmt.flights.cache.core.impl.couch.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by amit on 19/5/16.
 */
@Document(expiry = 1,expiryUnit = TimeUnit.HOURS)
@Cacheable
public class MetaCache {

    @Id
    private String id;

    @Field
    private String service;

    @Field(value = "trip_type")
    private String tripType;

    @Field(value = "updated_at")
    private Date updatedAt;

    @Field
    private Boolean isArchived;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }
}
