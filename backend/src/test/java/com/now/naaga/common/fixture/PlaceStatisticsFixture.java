package com.now.naaga.common.fixture;

import com.now.naaga.placestatistics.PlaceStatistics;

public class PlaceStatisticsFixture {

    public static PlaceStatistics PLACE_STATISTICS() {
        return new PlaceStatistics(PlaceFixture.PLACE(), 0L);
    }
}
