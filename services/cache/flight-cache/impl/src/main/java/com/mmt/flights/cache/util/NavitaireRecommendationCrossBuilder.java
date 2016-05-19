package com.mmt.flights.cache.util;

import com.mmt.pojo.search.CombinedInfo;
import com.mmt.pojo.search.Journey;
import com.mmt.pojo.search.ReturnJourneyMapping;
import com.mmt.pojo.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * Created by amit on 18/5/16.
 */


@Component
public class NavitaireRecommendationCrossBuilder {

    private final long MINIMUM_DIFFERENCE_FOR_RETURN = 120l;
    private static final Logger LOGGER = LoggerFactory.getLogger(NavitaireRecommendationCrossBuilder.class);

    public void populateReturnMap(SearchResponse searchResponse, List<List<Journey>> listOfJourneys, String serviceName) {
        searchResponse.addReturnJourneyMap(serviceName, new ReturnJourneyMapping());
        //Todo add null check
        List<Journey> forwardJourneys = listOfJourneys.get(0);
        List<Journey> reverseJourneys = listOfJourneys.get(1);
        forwardJourneys.stream().forEach(forward -> {
            reverseJourneys.stream().forEach(reverse -> {
                if (isCombinationPossible(forward, reverse)) {
                    addValueToStringToListMap(forward.getAggregatedFlightNumber(), reverse.getAggregatedFlightNumber(), searchResponse.getJourneyReturnMap().get(serviceName).getReturnJourneyMap());
                    addValueToCombinedMap(forward.getAggregatedFlightNumber(), reverse.getAggregatedFlightNumber(), searchResponse.getJourneyDetails().get(serviceName).getCombinedInfoMap());
                }

            });
        });
    }

    private void addValueToCombinedMap(String aggregatedFlightNumber, String aggregatedFlightNumber1, Map<String, CombinedInfo> combinedInfoMap) {
        String key = aggregatedFlightNumber;
        if (aggregatedFlightNumber1 != null && !"".equals(aggregatedFlightNumber1)) {
            key += "_" + aggregatedFlightNumber1;
        }
        if (combinedInfoMap.containsKey(key)) {
            combinedInfoMap.get(key).getData().put("validatingCarrier", "SG");
            combinedInfoMap.get(key).getData().put("refferenceAirline", "SG");
        } else {
            CombinedInfo combinedInfo = new CombinedInfo();
            combinedInfo.getData().put("validatingCarrier", "SG");
            combinedInfo.getData().put("refferenceAirline", "SG");
            combinedInfoMap.put(key, combinedInfo);
        }
    }

    private boolean isCombinationPossible(Journey forward, Journey reverse) {
        try {
            Date forwardArrivalDate = forward.getFlights().get(forward.getFlights().size() - 1).getArrivalDate();
            String forwardTomeZone = forward.getFlights().get(forward.getFlights().size() - 1).getArrivalInfo().getTimeZone();
            Date returnDepartureDate = reverse.getFlights().get(0).getDepartureDate();
            String returnTomeZone = reverse.getFlights().get(0).getDepartureInfo().getTimeZone();
            return forwardArrivalDate.compareTo(returnDepartureDate) < 0 && DateUtil.getDurationInMinutes(forwardArrivalDate, forwardTomeZone, returnDepartureDate, returnTomeZone) >= MINIMUM_DIFFERENCE_FOR_RETURN ? true : false;
        } catch (ParseException e) {
            LOGGER.error("Error while computing cross recommendation is possible or not for return", e);
            return false;
        }
    }

    private void addValueToStringToListMap(String forward, String rt, HashMap<String, List<String>> returnJourneyMapping) {
        if (returnJourneyMapping.containsKey(forward)) {
            returnJourneyMapping.get(forward).add(rt);
        } else {
            List<String> newList = new ArrayList<>();
            newList.add(rt);
            returnJourneyMapping.put(forward, newList);
        }
    }
}
