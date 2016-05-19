package com.mmt.flights.cache.cms;

import com.mmt.pojo.cms.CMSDetailRequest;
import com.mmt.pojo.cms.CMSDetailResponse;
import com.mmt.pojo.cms.CMSIDRequests;
import com.mmt.pojo.cms.CMSIDResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by amit on 17/5/16.
 */
@Component
public class CMSEngineServiceImpl implements CMSEngineService{

    @Autowired
    private CMSEngineRestService cmsEngineRestService;

    public CMSIDResponse getCredentialID(CMSIDRequests request) {
        CMSIDResponse response = cmsEngineRestService.getCredentialID(request);
        return response;
    }


    public CMSDetailResponse getCredentialDetail(CMSDetailRequest request) {
        CMSDetailResponse response = cmsEngineRestService.getCredentialDetail(request);
        return response;
    }
}
