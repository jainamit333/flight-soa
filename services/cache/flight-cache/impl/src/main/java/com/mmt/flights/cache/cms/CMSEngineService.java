package com.mmt.flights.cache.cms;

import com.mmt.pojo.cms.CMSDetailRequest;
import com.mmt.pojo.cms.CMSDetailResponse;
import com.mmt.pojo.cms.CMSIDRequests;
import com.mmt.pojo.cms.CMSIDResponse;

/**
 * Created by amit on 17/5/16.
 */

public interface CMSEngineService {
    CMSIDResponse getCredentialID(CMSIDRequests request);
    CMSDetailResponse getCredentialDetail(CMSDetailRequest request);
}

